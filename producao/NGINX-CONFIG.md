# Configuração do NGINX - IFala Frontend

## 📋 Visão Geral

O NGINX é utilizado em produção para servir os arquivos estáticos do React e fazer proxy reverso para o backend.

## 🏗️ Arquitetura

```
Cliente (Browser)
    ↓
NGINX (porta 80) ← Docker expõe na porta 8080
    ├─→ Arquivos estáticos React (/, /denuncia, etc)
    └─→ Proxy para Backend (/api/*)
            ↓
        Backend Spring Boot (porta 8080 interna)
```

## 📝 Arquivo nginx.conf

O arquivo `apps/ifala-frontend/nginx.conf` contém toda a configuração do NGINX.

### Principais Configurações

#### 1. Server Block
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;
}
```
- **listen 80**: NGINX escuta na porta 80 dentro do container
- **server_name**: Define o hostname (localhost para desenvolvimento/produção local)
- **root**: Diretório onde estão os arquivos estáticos do React
- **index**: Arquivo padrão a ser servido

#### 2. Compressão Gzip
```nginx
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/json application/javascript;
```
- Reduz o tamanho dos arquivos transferidos
- Melhora a performance de carregamento
- Aplica-se a HTML, CSS, JS, JSON, etc.

#### 3. Cache de Assets Estáticos
```nginx
location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
}
```
- Arquivos estáticos são cacheados por 30 dias
- Melhora drasticamente o tempo de carregamento em visitas subsequentes
- `immutable` indica que o arquivo nunca muda (versionamento via hash)

#### 4. Proxy Reverso para Backend
```nginx
location /api {
    proxy_pass http://localhost:8080;
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
- **Importante**: Note que é `http://localhost:8080` e NÃO `http://ifala-backend:8080`
- Isso funciona porque estamos usando `network_mode: host` no docker-compose de produção
- Todas as requisições para `/api/*` são redirecionadas para o backend
- Headers preservam informações do cliente original

#### 5. SPA (Single Page Application) Support
```nginx
location / {
    try_files $uri $uri/ /index.html;
    add_header Cache-Control "no-cache, no-store, must-revalidate";
}
```
- **Crucial para React Router funcionar!**
- Tenta servir o arquivo solicitado
- Se não encontrar, retorna `index.html` (deixa o React Router lidar)
- `index.html` não é cacheado para sempre pegar a versão mais recente

#### 6. Headers de Segurança
```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
```
- **X-Frame-Options**: Previne clickjacking
- **X-Content-Type-Options**: Previne MIME sniffing
- **X-XSS-Protection**: Proteção contra XSS (browsers antigos)

## 🔧 Modificações Comuns

### Alterar Porta do Backend

Se o backend estiver em outra porta:
```nginx
location /api {
    proxy_pass http://localhost:NOVA_PORTA;
    # ... resto da configuração
}
```

### Adicionar Autenticação Básica

```nginx
location /admin {
    auth_basic "Área Restrita";
    auth_basic_user_file /etc/nginx/.htpasswd;
    try_files $uri $uri/ /index.html;
}
```

### Configurar HTTPS (quando tiver certificado)

```nginx
server {
    listen 443 ssl http2;
    server_name seu-dominio.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # ... resto da configuração
}

# Redirecionar HTTP para HTTPS
server {
    listen 80;
    server_name seu-dominio.com;
    return 301 https://$server_name$request_uri;
}
```

### Aumentar Timeout para Requisições Longas

```nginx
location /api {
    proxy_pass http://localhost:8080;
    proxy_connect_timeout 120s;
    proxy_send_timeout 120s;
    proxy_read_timeout 120s;
    # ... resto
}
```

### Rate Limiting (limitar requisições)

```nginx
# No topo do arquivo, fora do server block
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

# Dentro do location /api
location /api {
    limit_req zone=api_limit burst=20 nodelay;
    proxy_pass http://localhost:8080;
    # ... resto
}
```

## 🐛 Troubleshooting

### Frontend não carrega

```bash
# Verificar se NGINX está rodando
docker exec -it ifala-frontend-prd nginx -t

# Ver logs do NGINX
docker logs ifala-frontend-prd

# Entrar no container e verificar arquivos
docker exec -it ifala-frontend-prd sh
ls -la /usr/share/nginx/html
```

### API não responde (502 Bad Gateway)

- Verificar se o backend está rodando: `docker ps`
- Verificar se o backend está acessível na porta 8080
- Verificar logs do backend: `docker logs ifala-backend-prd`
- Testar conexão: `curl http://localhost:8080/api/health`

### React Router não funciona (404 em rotas)

Verifique se a configuração `try_files` está correta:
```nginx
location / {
    try_files $uri $uri/ /index.html;  # ← Deve ter /index.html
}
```

### Assets não carregam (404)

Verifique se os arquivos estão no local correto:
```bash
docker exec -it ifala-frontend-prd ls -la /usr/share/nginx/html/assets
```

## 📊 Monitoramento

### Ver Logs em Tempo Real

```powershell
docker logs -f ifala-frontend-prd
```

### Verificar Configuração

```bash
# Testa a sintaxe da configuração
docker exec -it ifala-frontend-prd nginx -t

# Recarregar configuração sem downtime
docker exec -it ifala-frontend-prd nginx -s reload
```

### Estatísticas de Acesso

Para habilitar página de status do NGINX:
```nginx
location /nginx_status {
    stub_status on;
    access_log off;
    allow 127.0.0.1;
    deny all;
}
```

Acesse: http://localhost:8080/nginx_status

## 📚 Referências

- [NGINX Official Docs](https://nginx.org/en/docs/)
- [NGINX as Reverse Proxy](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [NGINX Security Controls](https://docs.nginx.com/nginx/admin-guide/security-controls/)
- [NGINX Performance Tuning](https://www.nginx.com/blog/tuning-nginx/)

## 🔐 Segurança

### Não fazer em Produção Real:

- ❌ Usar HTTP sem HTTPS
- ❌ Expor informações de versão do servidor
- ❌ Não configurar rate limiting
- ❌ Não configurar CORS adequadamente

### Fazer em Produção Real:

- ✅ Configurar HTTPS com certificado válido
- ✅ Implementar rate limiting
- ✅ Configurar headers de segurança
- ✅ Habilitar logs de acesso e erro
- ✅ Configurar firewall
- ✅ Usar autenticação quando necessário
