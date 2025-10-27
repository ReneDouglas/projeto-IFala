# 🧪 Guia Completo de Testes - Ambiente de Produção

Este guia fornece um passo a passo detalhado para testar toda a configuração de produção do IFala.

---

## 📋 Índice

1. [Passo 1: Validar a Configuração](#passo-1-validar-a-configuração)
2. [Passo 2: Gerar Credenciais](#passo-2-gerar-credenciais)
3. [Passo 3: Iniciar o Ambiente](#passo-3-iniciar-o-ambiente-de-produção)
4. [Passo 4: Verificar Status](#passo-4-verificar-status-dos-containers)
5. [Passo 5: Verificar Logs](#passo-5-verificar-logs)
6. [Passo 6: Testar Frontend](#passo-6-testar-o-frontend)
7. [Passo 7: Testar Backend](#passo-7-testar-a-api-do-backend)
8. [Passo 8: Testar Grafana](#passo-8-testar-o-grafana)
9. [Passo 9: Verificar NGINX](#passo-9-verificar-nginx)
10. [Passo 10: Teste de Integração](#passo-10-teste-de-integração-frontend--backend)
11. [Passo 11: Teste de Performance](#passo-11-teste-de-performance)
12. [Passo 12: Teste de Persistência](#passo-12-teste-de-persistência)
13. [Troubleshooting](#-troubleshooting-rápido)
14. [Checklist de Sucesso](#-checklist-de-sucesso)

---

## Passo 1: Validar a Configuração

**⏱️ Tempo estimado: 30 segundos**

Execute o script de validação:

```powershell
.\validate-prd.ps1
```

### O que este script verifica:

- ✓ Docker instalado e rodando
- ✓ Docker Compose instalado
- ✓ Arquivos necessários existem
- ✓ `.env` configurado corretamente
- ✓ Sem senhas padrão ou fracas
- ✓ NGINX configurado
- ✓ Profile `prod` ativado
- ✓ Espaço em disco suficiente (>10GB)
- ✓ Variáveis de ambiente presentes

### Resultado esperado:

```
========================================
           SUMARIO DA VALIDACAO
========================================
Tudo OK! Pronto para deploy!

Execute: .\deploy-prd.ps1 start
```

⚠️ **Se houver erros**: Corrija-os antes de prosseguir!

---

## Passo 2: Gerar Credenciais

**⏱️ Tempo estimado: 2 minutos**

Se ainda não gerou as credenciais, execute:

```powershell
.\generate-credentials.ps1
```

### Opções disponíveis:

1. Gerar apenas senha do PostgreSQL
2. Gerar apenas JWT Secret
3. **Gerar TODAS as credenciais** ← **RECOMENDADO**

### Criar e configurar o `.env`:

```powershell
# 1. Copiar template
Copy-Item .env.example .env

# 2. Editar arquivo
notepad .env
```

Cole as credenciais geradas e salve o arquivo.

### Variáveis obrigatórias no `.env`:

```bash
POSTGRES_USER=postgres
POSTGRES_PASSWORD=<senha_gerada>
POSTGRES_DB=ifala

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ifala
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<mesma_senha_postgres>

JWT_SECRET=<jwt_secret_gerado>

KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=<senha_gerada>

GF_SECURITY_ADMIN_USER=admin
GF_SECURITY_ADMIN_PASSWORD=<senha_gerada>
```

---

## Passo 3: Iniciar o Ambiente de Produção

**⏱️ Tempo estimado: 2-3 minutos**

### Opção 1: Usando o script (Recomendado)

```powershell
.\deploy-prd.ps1 start
```

### Opção 2: Manual

```powershell
docker-compose -f docker-compose-prd.yml up -d --build
```

### O que acontece:

1. 🏗️ Build das imagens Docker (frontend e backend)
2. 📦 Download das imagens base (PostgreSQL, Keycloak, etc.)
3. 🚀 Inicialização de 8 containers
4. 🔍 Health checks dos serviços
5. 🗄️ Criação dos volumes persistentes

**Aguarde 2-3 minutos para todos os serviços subirem completamente.**

---

## Passo 4: Verificar Status dos Containers

**⏱️ Tempo estimado: 30 segundos**

### Ver todos os containers:

```powershell
docker-compose -f docker-compose-prd.yml ps
```

**OU**

```powershell
docker ps
```

### Status esperado:

```
NAME                    STATUS
ifala-frontend-prd      Up (healthy)
ifala-backend-prd       Up (healthy)
ifala-db-prd            Up (healthy)
keycloak-prd            Up
grafana-prd             Up
prometheus-prd          Up
loki-prd                Up
promtail-prd            Up
```

### Sinais de problemas:

- ❌ `Restarting` - Container está em loop de restart
- ❌ `Exited` - Container parou com erro
- ⚠️ `(health: starting)` por muito tempo - Health check falhando

---

## Passo 5: Verificar Logs

**⏱️ Tempo estimado: 2 minutos**

### Ver logs de todos os serviços:

```powershell
docker-compose -f docker-compose-prd.yml logs -f
```

**Pressione `Ctrl+C` para sair**

### Logs específicos:

```powershell
# Frontend
docker logs ifala-frontend-prd

# Backend
docker logs ifala-backend-prd

# Banco de dados
docker logs ifala-db-prd

# Últimas 50 linhas
docker logs ifala-backend-prd --tail 50

# Seguir logs em tempo real
docker logs ifala-backend-prd -f
```

### O que procurar:

#### ✅ Backend (Spring Boot)

```
Started IfalaApplication in X seconds
Tomcat started on port 8080
```

#### ✅ Frontend (NGINX)

```
Configuration complete; ready for start up
```

#### ✅ PostgreSQL

```
database system is ready to accept connections
```

#### ❌ Erros comuns

- `Connection refused` - Serviço não está rodando
- `password authentication failed` - Credenciais incorretas
- `port already in use` - Porta em uso por outro processo
- `Failed to bind` - Problema de rede/porta

---

## Passo 6: Testar o Frontend

**⏱️ Tempo estimado: 3 minutos**

### 1. Acessar a aplicação

Abra o navegador e acesse:

```
http://localhost:8080
```

### 2. Testes visuais

| Teste | O que verificar | ✅ Passou |
|-------|----------------|----------|
| **Página inicial carrega** | Logo, menu, layout aparecem | ☐ |
| **Navegação funciona** | Home → Login → Denúncia → Acompanhamento | ☐ |
| **React Router** | Atualizar página em `/denuncia` mantém a rota | ☐ |
| **Imagens carregam** | Logo, ícones aparecem corretamente | ☐ |
| **Estilos aplicados** | CSS carregado, cores corretas | ☐ |
| **Responsivo** | Layout se adapta ao redimensionar | ☐ |

### 3. Console do navegador (F12)

Abra as **DevTools** (F12) e verifique:

- ✅ **Aba Console**: Sem erros em vermelho (exceto avisos)
- ✅ **Aba Network**: Arquivos carregam com status 200
- ✅ **Aba Application**: Service Worker (se tiver) funcionando

### 4. Testar SPA (Single Page Application)

```
1. Acesse http://localhost:8080/denuncia
2. Pressione F5 (atualizar página)
3. A página deve permanecer em /denuncia (não ir para 404)
```

Se funcionar = **NGINX configurado corretamente!** ✅

---

## Passo 7: Testar a API do Backend

**⏱️ Tempo estimado: 2 minutos**

### Opção 1: Usando PowerShell

```powershell
# Testar health check (se existir)
Invoke-WebRequest -Uri http://localhost:8080/api/health

# OU testar qualquer endpoint
Invoke-WebRequest -Uri http://localhost:8080/api
```

### Opção 2: Usando curl (se instalado)

```powershell
curl http://localhost:8080/api/health
```

### Opção 3: Navegador

Acesse diretamente:

```
http://localhost:8080/api
```

### Resultados esperados:

- ✅ **200 OK** - API respondendo
- ✅ **404 Not Found** - Normal se endpoint não existe
- ✅ **JSON response** - API retornando dados
- ❌ **Connection refused** - Backend não está rodando
- ❌ **502 Bad Gateway** - NGINX não consegue acessar backend

### Testar endpoints específicos:

```powershell
# GET - Listar denúncias
Invoke-WebRequest -Uri http://localhost:8080/api/denuncia

# POST - Criar denúncia (exemplo)
$body = @{
    descricao = "Teste"
    categoria = "outros"
} | ConvertTo-Json

Invoke-WebRequest -Uri http://localhost:8080/api/denuncia `
    -Method POST `
    -Body $body `
    -ContentType "application/json"
```

---

## Passo 8: Testar o Grafana

**⏱️ Tempo estimado: 2 minutos**

### 1. Acessar o Grafana

```
http://localhost:8081
```

### 2. Fazer login

Credenciais (conforme `.env`):

```
Usuário: admin
Senha: <valor de GF_SECURITY_ADMIN_PASSWORD>
```

### 3. Verificações

| Teste | O que verificar | ✅ Passou |
|-------|----------------|----------|
| **Página carrega** | Interface do Grafana aparece | ☐ |
| **Login funciona** | Credenciais são aceitas | ☐ |
| **Dashboards** | Dashboards pré-configurados aparecem | ☐ |
| **Datasources** | Prometheus e Loki conectados | ☐ |
| **Métricas** | Gráficos mostram dados | ☐ |

### 4. Testar datasources

```
1. Menu lateral → Configuration → Data sources
2. Verificar:
   - Prometheus (http://prometheus:9090)
   - Loki (http://loki:3100)
3. Clicar em "Test" em cada um
4. Deve mostrar "Data source is working"
```

---

## Passo 9: Verificar NGINX

**⏱️ Tempo estimado: 2 minutos**

### Entrar no container do frontend:

```powershell
docker exec -it ifala-frontend-prd sh
```

### Dentro do container:

```bash
# Listar arquivos do React
ls -la /usr/share/nginx/html

# Deve mostrar:
# index.html
# assets/
# vite.svg
# etc.

# Testar configuração do NGINX
nginx -t

# Deve retornar:
# nginx: configuration file /etc/nginx/nginx.conf test is successful

# Ver processos
ps aux | grep nginx

# Sair do container
exit
```

### Verificar logs do NGINX:

```powershell
# Logs de acesso
docker exec ifala-frontend-prd cat /var/log/nginx/access.log

# Logs de erro
docker exec ifala-frontend-prd cat /var/log/nginx/error.log
```

---

## Passo 10: Teste de Integração Frontend → Backend

**⏱️ Tempo estimado: 5 minutos**

### 1. Abrir DevTools

```
1. Acesse http://localhost:8080
2. Pressione F12
3. Vá para aba "Network"
4. Marque "Preserve log"
```

### 2. Testar funcionalidade

```
1. Navegue até "Nova Denúncia"
2. Preencha o formulário
3. Submeta a denúncia
4. Observe a aba Network
```

### 3. O que verificar

| Item | O que procurar | Status |
|------|----------------|--------|
| **Request Method** | POST | ✅ |
| **Request URL** | http://localhost:8080/api/denuncia | ✅ |
| **Status Code** | 200 OK ou 201 Created | ✅ |
| **Response** | JSON com dados retornados | ✅ |
| **Headers** | Content-Type: application/json | ✅ |

### 4. Erros comuns

#### ❌ 502 Bad Gateway

```powershell
# NGINX não consegue acessar backend
# Verificar se backend está rodando:
docker logs ifala-backend-prd
```

#### ❌ CORS Error

```
Access to fetch at 'http://localhost:8080/api' has been blocked by CORS policy
```

**Solução**: Configurar CORS no Spring Boot

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:8080")
                    .allowedMethods("*");
            }
        };
    }
}
```

#### ❌ 404 Not Found

```powershell
# Endpoint não existe ou rota incorreta
# Verificar rotas disponíveis nos logs do Spring
docker logs ifala-backend-prd | findstr "Mapped"
```

---

## Passo 11: Teste de Performance

**⏱️ Tempo estimado: 5 minutos**

### 1. Ver uso de recursos em tempo real

```powershell
docker stats
```

**Pressione `Ctrl+C` para sair**

### 2. Valores esperados (em idle)

| Container | CPU | Memória | Rede |
|-----------|-----|---------|------|
| Backend | < 5% | 500MB-1GB | Baixo |
| Frontend | < 1% | 20-50MB | Baixo |
| PostgreSQL | < 2% | 50-100MB | Baixo |
| Keycloak | < 10% | 300-500MB | Baixo |
| Grafana | < 2% | 50-100MB | Baixo |
| Prometheus | < 3% | 100-200MB | Médio |
| Loki | < 2% | 50-100MB | Baixo |
| Promtail | < 1% | 20-50MB | Baixo |

### 3. Uso de disco

```powershell
docker system df
```

### Resultado esperado:

```
TYPE            TOTAL     ACTIVE    SIZE
Images          15        8         2.5GB
Containers      8         8         1.2GB
Local Volumes   6         6         500MB
Build Cache     50        0         1.5GB
```

### 4. Limpar recursos não utilizados (se necessário)

```powershell
# Limpar containers parados
docker container prune

# Limpar imagens não utilizadas
docker image prune -a

# Limpar volumes não utilizados (CUIDADO!)
docker volume prune

# Limpar tudo (CUIDADO!)
docker system prune -a --volumes
```

⚠️ **ATENÇÃO**: `docker system prune` remove TODOS os dados não utilizados!

---

## Passo 12: Teste de Persistência

**⏱️ Tempo estimado: 5 minutos**

### 1. Criar dados de teste

```
1. Acesse http://localhost:8080
2. Crie uma denúncia de teste
3. Anote o token de acompanhamento
```

### 2. Parar todos os containers

```powershell
docker-compose -f docker-compose-prd.yml down
```

⚠️ **NÃO** use `-v` para manter os volumes!

### 3. Reiniciar

```powershell
docker-compose -f docker-compose-prd.yml up -d
```

### 4. Verificar dados

```
1. Aguarde containers subirem (2-3 minutos)
2. Acesse http://localhost:8080
3. Tente acessar a denúncia criada
4. Use o token de acompanhamento
```

### ✅ Teste passou se:

- Denúncia ainda existe
- Token de acompanhamento funciona
- Dados do Grafana persistiram
- Configurações do Keycloak mantidas

---

## 🐛 Troubleshooting Rápido

### Frontend não carrega (localhost:8080)

```powershell
# 1. Verificar se container está rodando
docker ps | findstr frontend

# 2. Ver logs
docker logs ifala-frontend-prd

# 3. Verificar porta
netstat -an | findstr 8080

# 4. Testar NGINX
docker exec -it ifala-frontend-prd nginx -t

# 5. Reiniciar container
docker restart ifala-frontend-prd
```

### Backend não responde (502 Bad Gateway)

```powershell
# 1. Verificar se backend está rodando
docker ps | findstr backend

# 2. Ver logs
docker logs ifala-backend-prd

# 3. Verificar se Spring iniciou
docker logs ifala-backend-prd | findstr "Started"

# 4. Testar conexão com banco
docker logs ifala-backend-prd | findstr "HikariPool"

# 5. Verificar health check
docker exec ifala-backend-prd wget -O- http://localhost:8080/actuator/health
```

### Banco de dados não conecta

```powershell
# 1. Verificar se PostgreSQL está healthy
docker-compose -f docker-compose-prd.yml ps postgres

# 2. Ver logs
docker logs ifala-db-prd

# 3. Verificar credenciais no .env
cat .env | findstr POSTGRES

# 4. Testar conexão manual
docker exec -it ifala-db-prd psql -U postgres -d ifala

# 5. Verificar se banco foi criado
docker exec -it ifala-db-prd psql -U postgres -c "\l"
```

### Porta já em uso

```powershell
# Descobrir processo usando porta 8080
netstat -ano | findstr :8080

# Matar processo (substitua <PID>)
taskkill /PID <PID> /F

# OU mudar porta no docker-compose-prd.yml
# De: "8080:80"
# Para: "8090:80"
```

### Container em loop de restart

```powershell
# Ver últimos logs antes do crash
docker logs ifala-backend-prd --tail 100

# Verificar health check
docker inspect ifala-backend-prd | findstr -i health

# Desabilitar restart temporariamente
docker update --restart=no ifala-backend-prd

# Iniciar manualmente para debug
docker start -ai ifala-backend-prd
```

### Memória insuficiente

```powershell
# Ver uso de memória
docker stats --no-stream

# Limitar memória do container (exemplo: 1GB)
docker update --memory="1g" ifala-backend-prd

# OU no docker-compose-prd.yml:
# services:
#   ifala-backend:
#     deploy:
#       resources:
#         limits:
#           memory: 1G
```

### Problemas de rede entre containers

```powershell
# Inspecionar rede
docker network inspect projeto-ifala_ifala-network

# Testar conectividade (dentro de um container)
docker exec ifala-backend-prd ping postgres

# Verificar DNS
docker exec ifala-backend-prd nslookup postgres

# Recriar rede
docker-compose -f docker-compose-prd.yml down
docker network prune
docker-compose -f docker-compose-prd.yml up -d
```

---

## ✅ Checklist de Sucesso

Marque conforme for testando:

### Pré-requisitos
- [ ] `validate-prd.ps1` passou sem erros
- [ ] Arquivo `.env` criado e configurado
- [ ] Credenciais fortes geradas
- [ ] Docker e Docker Compose instalados

### Infraestrutura
- [ ] Todos os containers estão "Up"
- [ ] Health checks passando (healthy)
- [ ] Volumes persistentes criados
- [ ] Rede Docker funcionando

### Frontend
- [ ] Página inicial carrega (http://localhost:8080)
- [ ] React Router funciona (navegação entre páginas)
- [ ] SPA funciona (F5 mantém rota)
- [ ] NGINX servindo arquivos estáticos
- [ ] Estilos e imagens carregam
- [ ] Console sem erros críticos

### Backend
- [ ] API responde (http://localhost:8080/api)
- [ ] Spring Boot iniciou completamente
- [ ] Conexão com banco estabelecida
- [ ] Flyway executou migrações
- [ ] Endpoints funcionando

### Banco de Dados
- [ ] PostgreSQL rodando e healthy
- [ ] Banco `ifala` criado
- [ ] Tabelas criadas (migrações)
- [ ] Dados persistem após restart

### Monitoramento
- [ ] Grafana acessível (http://localhost:8081)
- [ ] Login no Grafana funciona
- [ ] Dashboards carregam
- [ ] Prometheus coletando métricas
- [ ] Loki agregando logs

### Integração
- [ ] Frontend → Backend comunicando
- [ ] Sem erros de CORS
- [ ] Requisições API funcionando
- [ ] Formulários submetem dados
- [ ] Respostas JSON corretas

### Performance
- [ ] Uso de CPU < 50% (idle)
- [ ] Memória adequada (< 3GB total)
- [ ] Disco suficiente (> 10GB livres)
- [ ] Logs sem warnings críticos

### Segurança
- [ ] Senhas fortes geradas
- [ ] `.env` não versionado (.gitignore)
- [ ] Portas internas não expostas
- [ ] Debug desabilitado (produção)

### Persistência
- [ ] Dados sobrevivem a restart
- [ ] Volumes mantêm estado
- [ ] Backups possíveis

---

## 📊 Teste Visual Rápido (TL;DR)

Execute este comando e veja se está tudo verde/saudável:

```powershell
docker-compose -f docker-compose-prd.yml ps
```

Depois acesse no navegador:

1. **http://localhost:8080** → Deve ver o IFala 🎯
2. **http://localhost:8081** → Deve ver login do Grafana 📊

### Se ambos carregarem = **SUCESSO!** 🎉

---

## 🚀 Comandos Úteis de Referência Rápida

```powershell
# Ver status
docker-compose -f docker-compose-prd.yml ps

# Ver logs
docker-compose -f docker-compose-prd.yml logs -f

# Reiniciar tudo
docker-compose -f docker-compose-prd.yml restart

# Parar tudo
docker-compose -f docker-compose-prd.yml down

# Iniciar tudo
docker-compose -f docker-compose-prd.yml up -d

# Rebuild e iniciar
docker-compose -f docker-compose-prd.yml up -d --build

# Ver recursos
docker stats

# Limpar
docker system prune
```

---

## 📝 Próximos Passos

Após todos os testes passarem:

1. ✅ Configurar CI/CD (GitHub Actions)
2. ✅ Configurar backup automático
3. ✅ Configurar alertas no Grafana
4. ✅ Documentar API (Swagger/OpenAPI)
5. ✅ Configurar monitoramento externo
6. ✅ Implementar estratégia de rollback
7. ✅ Configurar SSL/TLS (HTTPS)
8. ✅ Testes de carga (k6, JMeter)

---

## 📚 Documentação Relacionada

- [PRODUCTION.md](PRODUCTION.md) - Guia de deploy em produção
- [DEPLOY.md](DEPLOY.md) - Guia de deployment
- [DOCKER.md](DOCKER.md) - Documentação Docker
- [MONITORING.md](MONITORING.md) - Monitoramento e observabilidade
- [SECURITY.md](SECURITY.md) - Boas práticas de segurança

---

**🎉 Parabéns! Se chegou até aqui e todos os testes passaram, sua aplicação está pronta para produção!**
