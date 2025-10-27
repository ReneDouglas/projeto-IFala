# Ambiente de Produção - IFala

Este diretório contém todos os arquivos necessários para executar o projeto IFala em ambiente de produção.

## 📋 Estrutura de Arquivos

```
projeto-IFala/
├── docker-compose-prd.yml          # Orquestração de containers em produção
├── .env                            # Variáveis de ambiente sensíveis (NÃO VERSIONADO)
├── .env.example                    # Exemplo de variáveis de ambiente
├── apps/
│   ├── ifala-frontend/
│   │   ├── Dockerfile.prd          # Build otimizado do frontend com NGINX
│   │   └── nginx.conf              # Configuração do NGINX
│   └── ifala-backend/
│       └── Dockerfile.prd          # Build otimizado do backend
```

## 🚀 Configuração Inicial

### 1. Criar o arquivo .env

Copie o arquivo de exemplo e preencha com suas credenciais:

```powershell
Copy-Item .env.example .env
```

Edite o arquivo `.env` e configure as seguintes variáveis obrigatórias:

```env
# PostgreSQL
POSTGRES_PASSWORD=SuaSenhaSuperSegura123!

# Spring Boot
SPRING_DATASOURCE_PASSWORD=SuaSenhaSuperSegura123!

# JWT Secret (gere com: openssl rand -base64 32)
JWT_SECRET=sua_chave_jwt_com_no_minimo_32_caracteres_aqui

# Keycloak
KEYCLOAK_ADMIN_PASSWORD=AdminSeguro123!

# Grafana
GF_SECURITY_ADMIN_PASSWORD=GrafanaSeguro123!
```

### 2. Verificar configurações do Backend

Certifique-se de que o arquivo `apps/ifala-backend/src/main/resources/application-prod.properties` 
está configurado corretamente para produção.

## 🏗️ Build e Deploy

### Opção 1: Build completo (primeira vez)

```powershell
# Build de todas as imagens
docker-compose -f docker-compose-prd.yml build

# Iniciar todos os serviços
docker-compose -f docker-compose-prd.yml up -d
```

### Opção 2: Build e iniciar em um comando

```powershell
docker-compose -f docker-compose-prd.yml up -d --build
```

### Verificar status dos containers

```powershell
docker-compose -f docker-compose-prd.yml ps
```

### Visualizar logs

```powershell
# Todos os serviços
docker-compose -f docker-compose-prd.yml logs -f

# Apenas um serviço específico
docker-compose -f docker-compose-prd.yml logs -f ifala-backend
docker-compose -f docker-compose-prd.yml logs -f ifala-frontend
docker-compose -f docker-compose-prd.yml logs -f postgres
```

## 🌐 Acessar os Serviços

Após iniciar os containers, os serviços estarão disponíveis em:

| Serviço | URL | Porta |
|---------|-----|-------|
| **Frontend (NGINX)** | http://localhost:8080 | 8080 |
| **Backend API** | http://localhost:8080/api | 8080 (via NGINX) |
| **Grafana** | http://localhost:8081 | 8081 |
| **Keycloak** | http://localhost:9090 | 9090 |

### Credenciais Padrão

**Grafana:**
- Usuário: definido em `GF_SECURITY_ADMIN_USER` (.env)
- Senha: definido em `GF_SECURITY_ADMIN_PASSWORD` (.env)

**Keycloak:**
- Usuário: definido em `KEYCLOAK_ADMIN` (.env)
- Senha: definido em `KEYCLOAK_ADMIN_PASSWORD` (.env)

## 🛠️ Comandos Úteis

### Parar todos os serviços

```powershell
docker-compose -f docker-compose-prd.yml down
```

### Parar e remover volumes (CUIDADO: apaga dados do banco!)

```powershell
docker-compose -f docker-compose-prd.yml down -v
```

### Rebuild de um serviço específico

```powershell
# Frontend
docker-compose -f docker-compose-prd.yml build ifala-frontend
docker-compose -f docker-compose-prd.yml up -d ifala-frontend

# Backend
docker-compose -f docker-compose-prd.yml build ifala-backend
docker-compose -f docker-compose-prd.yml up -d ifala-backend
```

### Executar comandos dentro de um container

```powershell
# Acessar bash do backend
docker exec -it ifala-backend-prd sh

# Acessar PostgreSQL
docker exec -it ifala-db-prd psql -U postgres -d ifala
```

### Verificar uso de recursos

```powershell
docker stats
```

## 🔒 Segurança

### ⚠️ IMPORTANTE

1. **NUNCA** versione o arquivo `.env` no Git
2. Sempre use senhas fortes e únicas para produção
3. Gere uma nova `JWT_SECRET` para cada ambiente
4. Mantenha as credenciais seguras e compartilhe apenas com pessoas autorizadas

### Gerar JWT Secret forte

```powershell
# Windows PowerShell
$bytes = New-Object byte[] 32; (New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes); [Convert]::ToBase64String($bytes)
```

## 📊 Monitoramento

### Grafana Dashboards

Acesse http://localhost:8081 e faça login. Os dashboards já estarão provisionados automaticamente:

- **Spring Boot Observability**: Métricas da aplicação
- **PostgreSQL**: Métricas do banco de dados
- **Logs**: Visualização de logs com Loki

### Prometheus

As métricas estão sendo coletadas pelo Prometheus. Você pode configurar alertas editando:
`monitoring/prometheus/prometheus.yml`

### Logs

Os logs são coletados pelo Promtail e enviados para o Loki. Visualize no Grafana.

## 🔄 Atualização

Para atualizar a aplicação após mudanças no código:

```powershell
# 1. Parar os serviços
docker-compose -f docker-compose-prd.yml down

# 2. Rebuild
docker-compose -f docker-compose-prd.yml build

# 3. Iniciar novamente
docker-compose -f docker-compose-prd.yml up -d
```

## 🐛 Troubleshooting

### Container não inicia

```powershell
# Verificar logs detalhados
docker-compose -f docker-compose-prd.yml logs --tail=100 [nome-do-servico]
```

### Banco de dados não conecta

```powershell
# Verificar health do PostgreSQL
docker-compose -f docker-compose-prd.yml ps postgres

# Verificar logs do banco
docker-compose -f docker-compose-prd.yml logs postgres
```

### Frontend não carrega

```powershell
# Verificar se o NGINX está rodando
docker exec -it ifala-frontend-prd nginx -t

# Verificar logs do NGINX
docker-compose -f docker-compose-prd.yml logs ifala-frontend
```

### Resetar tudo (CUIDADO!)

```powershell
# Para todos os containers, remove volumes e imagens
docker-compose -f docker-compose-prd.yml down -v --rmi all

# Rebuild completo
docker-compose -f docker-compose-prd.yml up -d --build
```

## 📝 Notas Adicionais

### NGINX Configuration

O NGINX está configurado para:
- Servir arquivos estáticos do React
- Fazer proxy reverso para o backend em `/api`
- Compressão Gzip
- Cache de assets estáticos (30 dias)
- Headers de segurança

### Backend Optimizations

O backend está otimizado com:
- Multi-stage build (reduz tamanho da imagem)
- G1 Garbage Collector
- Virtual Threads (Java 21+)
- Configurações de memória apropriadas para produção

### Volumes Persistentes

Os seguintes dados são persistidos em volumes Docker:
- `pgdata_prd`: Dados do PostgreSQL
- `keycloak_data_prd`: Dados do Keycloak
- `grafana_data_prd`: Configurações e dashboards do Grafana
- `loki_data_prd`: Logs armazenados
- `prometheus_data_prd`: Métricas históricas

## 🆘 Suporte

Para problemas ou dúvidas, consulte a documentação do projeto ou entre em contato com a equipe de desenvolvimento.
