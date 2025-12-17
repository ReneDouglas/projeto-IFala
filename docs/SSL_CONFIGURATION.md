# Configuração SSL/TLS - IFala

Este documento descreve a configuração SSL/TLS implementada para o domínio
`ifala.cacor.ifpi.edu.br`.

## Visão Geral

O sistema foi configurado para:

- **Redirecionar automaticamente** todo tráfego HTTP para HTTPS
- **Usar certificados SSL/TLS** válidos para o domínio `ifala.cacor.ifpi.edu.br`
- **Implementar headers de segurança** modernos
- **Suportar HTTP/2** para melhor performance

## Arquivos Modificados

### 1. `docker-compose-prd.yml`

- **Porta 443 adicionada** para tráfego HTTPS
- **Volumes dos certificados** mapeados da pasta `../certs/`
- **Health check atualizado** para usar HTTPS

### 2. `nginx/nginx.conf`

- **Servidor HTTP (porta 80)**: Redirecionamento automático para HTTPS
- **Servidor HTTPS (porta 443)**: Configuração SSL completa
- **Headers de segurança**: Implementação de best practices de segurança

## Certificados SSL

### Localização

Os certificados devem estar localizados em:

```
../certs/
├── ifala.cacor.ifpi.edu.br.crt  # Certificado público
└── ifala.cacor.ifpi.edu.br.key  # Chave privada
```

### Mapeamento no Container

- **Certificado**: `/etc/nginx/ssl/ifala.cacor.ifpi.edu.br.crt`
- **Chave privada**: `/etc/nginx/ssl/ifala.cacor.ifpi.edu.br.key`

## Configurações de Segurança Implementadas

### Protocolos SSL/TLS

- **TLS 1.2 e 1.3** suportados
- **Cifras seguras** configuradas
- **Perfect Forward Secrecy** habilitado

### Headers de Segurança

- `Strict-Transport-Security`: Força HTTPS por 1 ano
- `X-Frame-Options`: Previne clickjacking
- `X-Content-Type-Options`: Previne MIME sniffing
- `X-XSS-Protection`: Proteção contra XSS
- `Referrer-Policy`: Controla informações de referência

## Proxy Headers

Todos os serviços recebem os headers necessários:

- `X-Real-IP`: IP real do cliente
- `X-Forwarded-For`: Chain de proxies
- `X-Forwarded-Proto`: Protocolo original (https)
- `X-Forwarded-Host`: Host original

## Serviços Expostos

### Frontend (`/`)

- **Destino**: `http://ifala-frontend-prd:80`
- **Descrição**: Interface React servida pelo NGINX

### Backend API (`/api`)

- **Destino**: `http://ifala-backend-prd:8080`
- **Descrição**: API Spring Boot
- **Timeouts**: 60s para conexão, envio e leitura

### Grafana Dashboard (`/grafana/`)

- **Destino**: `http://grafana-prd:3000/`
- **Descrição**: Dashboard de monitoramento

## Como Aplicar as Mudanças

1. **Parar os serviços**:

   ```bash
   docker compose -f docker-compose-prd.yml down
   ```

2. **Verificar certificados**:

   ```bash
   ls -la ../certs/
   ```

3. **Reiniciar com SSL**:

   ```bash
   docker compose -f docker-compose-prd.yml up -d
   ```

4. **Verificar logs**:
   ```bash
   docker logs nginx-gateway-prd
   ```

## Verificação

### Testar redirecionamento HTTP → HTTPS

```bash
curl -I http://ifala.cacor.ifpi.edu.br
```

Deve retornar: `301 Moved Permanently`

### Testar HTTPS

```bash
curl -I https://ifala.cacor.ifpi.edu.br
```

Deve retornar: `200 OK`

### Verificar certificado

```bash
openssl s_client -connect ifala.cacor.ifpi.edu.br:443 -servername ifala.cacor.ifpi.edu.br
```

## Troubleshooting

### Problemas Comuns

1. **Certificados não encontrados**

   - Verificar se os arquivos existem em `../certs/`
   - Verificar permissões dos arquivos

2. **Health check falhando**

   - Verificar se o container nginx está rodando
   - Verificar logs: `docker logs nginx-gateway-prd`

3. **Erro SSL**
   - Verificar validade do certificado
   - Verificar se o certificado corresponde ao domínio

### Logs Úteis

```bash
# Logs do nginx
docker logs nginx-gateway-prd

# Health check status
docker ps --filter name=nginx-gateway-prd

# Testar configuração nginx
docker exec nginx-gateway-prd nginx -t
```
