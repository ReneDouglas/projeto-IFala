# Configuracao do NGINX Gateway

## Visao Geral

O NGINX atua como **gateway unico de entrada** para toda a aplicacao IFala em producao. Ele centraliza o acesso aos servicos (frontend, backend, Grafana) e elimina problemas de CORS entre frontend e backend.

## Arquitetura

```
Cliente (Navegador)
        |
        | http://localhost
        v
    NGINX Gateway (porta 80)
        |
        +---> / -----------> Frontend (React + NGINX interno)
        |
        +---> /api --------> Backend (Spring Boot:8080)
        |
        +---> /grafana ----> Grafana (Dashboard:3000)
```

## Estrutura do Arquivo nginx.conf

### 1. Contexto Main (Global)

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;
```

#### Explicacao:

- **user nginx**: Define que os processos worker rodam com o usuario `nginx` (seguranca - nao usa root)
- **worker_processes auto**: Cria um processo worker por nucleo da CPU automaticamente
- **error_log**: Grava erros em `/var/log/nginx/error.log` com nivel `warn`
- **pid**: Armazena o Process ID do NGINX em `/var/run/nginx.pid`

---

### 2. Bloco Events

```nginx
events {
    worker_connections 1024;
    use epoll;
}
```

#### Explicacao:

- **worker_connections 1024**: Cada worker pode gerenciar ate 1024 conexoes simultaneas
- **use epoll**: Usa o metodo epoll do Linux (eficiente para muitas conexoes)

**Capacidade total**: Se tem 4 cores → 4 workers × 1024 = **4096 conexoes simultaneas**

---

### 3. Bloco HTTP

```nginx
http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    # Formato de log customizado
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    
    access_log /var/log/nginx/access.log main;
```

#### Explicacao:

- **include /etc/nginx/mime.types**: Importa mapeamento de extensoes → Content-Type
  - Exemplo: `.html` → `text/html`, `.css` → `text/css`
- **default_type**: Define tipo padrao como `application/octet-stream` (arquivo binario)
- **log_format**: Define formato personalizado de log com IP, requisicao, status, etc.
- **access_log**: Grava todos os acessos em `/var/log/nginx/access.log`

---

#### Otimizacoes de Performance

```nginx
sendfile on;
tcp_nopush on;
tcp_nodelay on;
keepalive_timeout 65;
types_hash_max_size 2048;
```

**sendfile on**:
- Transfere arquivos diretamente do disco para socket (kernel-level)
- Muito mais rapido para servir arquivos estaticos

**tcp_nopush on**:
- Envia headers HTTP + inicio do arquivo em um unico pacote TCP
- Reduz latencia

**tcp_nodelay on**:
- Desabilita algoritmo de Nagle (envia dados imediatamente)
- Ideal para APIs e conexoes interativas

**keepalive_timeout 65**:
- Mantem conexoes TCP abertas por 65 segundos
- Cliente reutiliza mesma conexao para multiplas requisicoes
- Economia: evita handshake TCP/SSL repetidos

**types_hash_max_size 2048**:
- Tamanho da tabela hash para buscar MIME types rapidamente

---

#### Compressao Gzip

```nginx
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css text/xml text/javascript 
           application/x-javascript application/xml+rss 
           application/json application/javascript;
```

**gzip on**: Habilita compressao Gzip nas respostas
**gzip_vary on**: Adiciona header `Vary: Accept-Encoding` (para proxies)
**gzip_min_length 1024**: So comprime se resposta tiver pelo menos 1KB
**gzip_types**: Define quais Content-Types serao comprimidos

**Beneficio**: Arquivo de 100KB pode virar 20KB → 80% menos dados trafegados

**NAO comprimir**: Imagens (JPEG, PNG), videos, arquivos .zip (ja sao comprimidos)

---

### 4. Bloco Server

```nginx
server {
    listen 80;
    server_name localhost;
```

- **listen 80**: NGINX escuta requisicoes HTTP na porta 80
- **server_name localhost**: Atende requisicoes para `http://localhost`

---

## Locations (Roteamento)

### Location / (Frontend)

```nginx
location / {
    proxy_pass http://ifala-frontend-prd:80;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_cache_bypass $http_upgrade;
}
```

#### O que faz:
- Captura todas as requisicoes que comecam com `/` (todas!)
- Encaminha para container `ifala-frontend-prd` na porta 80

#### Headers importantes:

**proxy_http_version 1.1**:
- Usa HTTP/1.1 para comunicacao com backend
- Necessario para WebSocket e keep-alive

**proxy_set_header Upgrade / Connection**:
- Permite upgrade de protocolo (HTTP → WebSocket)
- Essencial se frontend usa WebSocket para real-time

**proxy_set_header Host**:
- Envia dominio original para o backend
- Backend pode precisar saber o dominio (URLs, CORS, multi-tenant)

**proxy_set_header X-Real-IP**:
- Informa ao backend o IP real do cliente
- Sem isso: backend ve IP do NGINX, nao do usuario
- Uso: logs, geolocalizacao, rate limiting

**proxy_set_header X-Forwarded-For**:
- Adiciona IP do cliente a lista de proxies
- Preserva cadeia de proxies (cliente → proxy1 → proxy2 → backend)

**proxy_set_header X-Forwarded-Proto**:
- Informa se requisicao original foi HTTP ou HTTPS
- Backend pode fazer redirect correto, gerar URLs corretas

**proxy_cache_bypass**:
- Nao cacheia requisicoes de upgrade (WebSocket)

---

### Location /api (Backend)

```nginx
location /api {
    proxy_pass http://ifala-backend-prd:8080;
    # ... mesmos headers do frontend ...
    
    # Timeouts
    proxy_connect_timeout 60s;
    proxy_send_timeout 60s;
    proxy_read_timeout 60s;
}
```

#### O que faz:
- Captura requisicoes que comecam com `/api`
- Encaminha para container `ifala-backend-prd` na porta 8080

#### Timeouts:

**proxy_connect_timeout 60s**:
- Tempo maximo para conectar ao backend
- Se backend demorar mais de 60s para aceitar conexao, da timeout

**proxy_send_timeout 60s**:
- Tempo maximo para enviar requisicao ao backend (entre sucessivos writes)
- Reinicia a cada write bem-sucedido
- Aumentar para upload de arquivos grandes

**proxy_read_timeout 60s**:
- Tempo maximo para receber resposta do backend (entre sucessivos reads)
- Reinicia a cada read bem-sucedido
- Aumentar se backend tem processamento demorado (relatorios, exports)

---

### Location /grafana (Monitoramento)

```nginx
location /grafana/ {
    proxy_pass http://grafana-prd:3000/;
    # ... mesmos headers ...
}
```

#### O que faz:
- Captura requisicoes que comecam com `/grafana/`
- Encaminha para container `grafana-prd` na porta 3000

#### Detalhe importante - Trailing slash:

**COM `/` no final**:
```
http://nginx/grafana/dashboard → http://grafana:3000/dashboard
(remove /grafana do caminho)
```

**SEM `/` no final**:
```
http://nginx/grafana/dashboard → http://grafana:3000/grafana/dashboard
(mantem /grafana no caminho)
```

---

## Fluxo de uma Requisicao

### Exemplo 1: Acessar Frontend

```
1. Cliente faz requisicao: http://localhost/
2. NGINX recebe na porta 80
3. Avalia locations: location / tem match
4. Proxy pass: Encaminha para http://ifala-frontend-prd:80/
5. Frontend responde com HTML
6. NGINX retorna resposta ao cliente
7. Log gravado em /var/log/nginx/access.log
```

### Exemplo 2: API Backend

```
1. Cliente faz requisicao: http://localhost/api/users
2. NGINX recebe na porta 80
3. Avalia locations: location /api tem match
4. Proxy pass: Encaminha para http://ifala-backend-prd:8080/api/users
5. Backend processa e retorna JSON
6. NGINX retorna resposta ao cliente (possivelmente comprimida com Gzip)
7. Log gravado
```

### Exemplo 3: Grafana Dashboard

```
1. Cliente faz requisicao: http://localhost/grafana/dashboard
2. NGINX recebe na porta 80
3. Avalia locations: location /grafana/ tem match
4. Proxy pass: Encaminha para http://grafana-prd:3000/dashboard
5. Grafana retorna interface
6. NGINX retorna resposta ao cliente
```

---

## Integracao com Docker Compose

### Volume Mount

```yaml
nginx-gateway:
  image: nginx:1.27-alpine
  volumes:
    - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
```

- Monta o arquivo local `nginx.conf` dentro do container
- `:ro` = read-only (seguranca)
- Substitui o arquivo principal `/etc/nginx/nginx.conf`

### Network

```yaml
networks:
  - ifala-network
```

Todos os servicos estao na mesma rede Docker:
- `ifala-frontend-prd` (resolve DNS automaticamente)
- `ifala-backend-prd`
- `grafana-prd`

---

## Vantagens desta Arquitetura

### 1. Ponto Unico de Entrada
- Apenas porta 80 exposta ao host
- Servicos internos nao precisam expor portas
- Seguranca: menos superficie de ataque

### 2. Eliminacao de CORS
- Frontend e Backend sob mesmo dominio (`localhost`)
- `/` → Frontend
- `/api` → Backend
- Navegador nao ve requisicao cross-origin

### 3. SSL/TLS Simplificado (futuro)
- Apenas NGINX precisa de certificado
- Servicos internos podem usar HTTP
- NGINX faz SSL termination

### 4. Load Balancing (futuro)
- NGINX pode distribuir carga entre multiplas instancias
- Exemplo: 3 containers backend-prd

### 5. Caching (futuro)
- NGINX pode cachear respostas do backend
- Reduz carga no backend

### 6. Rate Limiting (futuro)
- NGINX pode limitar requisicoes por IP
- Protecao contra DDoS

---

## Logs

### Access Log

Formato:
```
192.168.1.10 - - [29/Oct/2025:14:30:15 +0000] "GET /api/users HTTP/1.1" 200 1234 "http://localhost/" "Mozilla/5.0"
```

Campos:
- `192.168.1.10` - IP do cliente
- `[29/Oct/2025:14:30:15 +0000]` - Data/hora
- `GET /api/users HTTP/1.1` - Requisicao
- `200` - Codigo HTTP
- `1234` - Bytes enviados
- `http://localhost/` - Referer
- `Mozilla/5.0` - User-Agent

### Error Log

Nivel `warn` registra:
- Avisos de configuracao
- Erros ao conectar com backend
- Timeouts
- Erros HTTP 5xx

---

## Troubleshooting

### NGINX nao inicia

**Erro**: `"user" directive is not allowed here`
**Causa**: Arquivo montado em `/etc/nginx/conf.d/` em vez de `/etc/nginx/nginx.conf`
**Solucao**: Montar em `/etc/nginx/nginx.conf`

### Backend nao responde (502 Bad Gateway)

**Causa**: Container backend nao esta rodando ou inacessivel
**Verificar**:
```bash
docker ps --filter name=ifala-backend-prd
docker logs ifala-backend-prd
```

### Timeout (504 Gateway Timeout)

**Causa**: Backend demorou mais que 60s para responder
**Solucao**: Aumentar `proxy_read_timeout` no location /api

### CORS ainda aparece

**Causa**: Requisicao direta para `http://localhost:8080` em vez de `http://localhost/api`
**Solucao**: Frontend deve fazer requisicoes para `/api`, nao para porta 8080

---

## Melhorias Futuras

### 1. HTTPS (SSL/TLS)

```nginx
server {
    listen 443 ssl http2;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    # ...
}
```

### 2. Cache de Respostas

```nginx
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m;

location /api {
    proxy_cache api_cache;
    proxy_cache_valid 200 5m;
    # ...
}
```

### 3. Rate Limiting

```nginx
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

location /api {
    limit_req zone=api_limit burst=20;
    # ...
}
```

### 4. Compressao Brotli (melhor que Gzip)

```nginx
brotli on;
brotli_comp_level 6;
brotli_types text/plain text/css application/json;
```

---

## Comandos Uteis

### Verificar sintaxe da configuracao

```bash
docker exec nginx-gateway-prd nginx -t
```

### Recarregar configuracao (sem downtime)

```bash
docker exec nginx-gateway-prd nginx -s reload
```

### Ver logs em tempo real

```bash
docker logs -f nginx-gateway-prd
```

### Ver configuracao carregada

```bash
docker exec nginx-gateway-prd cat /etc/nginx/nginx.conf
```

---

## Referencias

- [Documentacao oficial do NGINX](https://nginx.org/en/docs/)
- [NGINX Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [NGINX Performance Tuning](https://www.nginx.com/blog/tuning-nginx/)
