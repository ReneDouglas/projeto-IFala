# 🎨 Diagramas da Arquitetura de Produção

## 📐 Fluxo de Requisição

```
┌──────────────────────────────────────────────────────────────────┐
│                         CLIENTE (Browser)                         │
│                    http://localhost:8080                          │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                    Docker Host (localhost)                        │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  CONTAINER: ifala-frontend-prd                             │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  NGINX (porta 80)                                    │  │  │
│  │  │  - Serve arquivos estáticos (/dist)                 │  │  │
│  │  │  - Proxy reverso (/api → backend)                   │  │  │
│  │  └────────────┬───────────────────┬────────────────────┘  │  │
│  └───────────────┼───────────────────┼───────────────────────┘  │
│                  │                   │                           │
│      Rota React  │                   │  /api/*                   │
│      (/, /login) │                   │                           │
│                  │                   ▼                           │
│                  │  ┌────────────────────────────────────────┐  │
│                  │  │  CONTAINER: ifala-backend-prd          │  │
│                  │  │  ┌──────────────────────────────────┐  │  │
│                  │  │  │  Spring Boot (porta 8080)        │  │  │
│                  │  │  │  - Profile: prod                 │  │  │
│                  │  │  │  - JRE 25 + Virtual Threads      │  │  │
│                  │  │  │  - G1GC, Xmx2g                   │  │  │
│                  │  │  └────────────┬─────────────────────┘  │  │
│                  │  └───────────────┼────────────────────────┘  │
│                  │                  │                           │
│                  │                  ▼                           │
│                  │  ┌────────────────────────────────────────┐  │
│                  │  │  CONTAINER: ifala-db-prd               │  │
│                  │  │  ┌──────────────────────────────────┐  │  │
│                  │  │  │  PostgreSQL 16 (porta 5432)      │  │  │
│                  │  │  │  - Volume: pgdata_prd            │  │  │
│                  │  │  │  - User/Pass: via .env           │  │  │
│                  │  │  └──────────────────────────────────┘  │  │
│                  │  └────────────────────────────────────────┘  │
│                  │                                               │
│                  ▼                                               │
│           index.html                                             │
│           + assets                                               │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Arquitetura de Containers

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Docker Network: ifala-network                │
│                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐    │
│  │  Frontend       │  │  Backend        │  │  PostgreSQL     │    │
│  │  (NGINX)        │  │  (Spring Boot)  │  │  (DB)           │    │
│  │  Port: 8080→80  │  │  Port: internal │  │  Port: internal │    │
│  │  restart:always │  │  restart:always │  │  restart:always │    │
│  │  healthcheck✓   │  │  healthcheck✓   │  │  healthcheck✓   │    │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘    │
│           │                    │                    │              │
│           │  /api/*           │  JDBC              │              │
│           └───────────────────►└───────────────────►│              │
│                                                                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐    │
│  │  Keycloak       │  │  Prometheus     │  │  Grafana        │    │
│  │  (Auth)         │  │  (Metrics)      │  │  (Dashboards)   │    │
│  │  Port: 9090     │  │  Port: internal │  │  Port: 8081     │    │
│  │  restart:always │  │  restart:always │  │  restart:always │    │
│  └─────────────────┘  └────────┬────────┘  └────────┬────────┘    │
│                                 │                    │              │
│                                 │  scrape            │  query       │
│                                 └───────────────────►│              │
│                                                      │              │
│  ┌─────────────────┐  ┌─────────────────┐          │              │
│  │  Loki           │  │  Promtail       │          │              │
│  │  (Logs)         │  │  (Collector)    │          │              │
│  │  Port: internal │  │  Port: internal │          │              │
│  │  restart:always │  │  restart:always │          │              │
│  └────────┬────────┘  └────────┬────────┘          │              │
│           │                    │  push              │  query       │
│           │◄───────────────────┘                    │              │
│           └────────────────────────────────────────►│              │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo de Build (Multi-Stage)

### Frontend (React + NGINX)

```
┌─────────────────────────────────────────────────────────────┐
│  ESTÁGIO 1: BUILDER                                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  FROM node:22-alpine                                  │  │
│  │  WORKDIR /app                                         │  │
│  │                                                        │  │
│  │  COPY package*.json ./                                │  │
│  │  RUN npm ci                                           │  │
│  │                                                        │  │
│  │  COPY . .                                             │  │
│  │  RUN npm run build  ──────► Gera /app/dist/          │  │
│  │                                      │                 │  │
│  └──────────────────────────────────────┼─────────────────┘  │
│                                         │                    │
│                                         │ Arquivos estáticos │
│                                         │ otimizados         │
│                                         ▼                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ESTÁGIO 2: PRODUCTION                                │  │
│  │  FROM nginx:1.27-alpine                               │  │
│  │                                                        │  │
│  │  COPY nginx.conf /etc/nginx/conf.d/                   │  │
│  │  COPY --from=builder /app/dist /usr/share/nginx/html │  │
│  │                                                        │  │
│  │  EXPOSE 80                                            │  │
│  │  CMD ["nginx", "-g", "daemon off;"]                   │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  Resultado: Imagem ~50MB (vs ~1GB sem multi-stage)         │
└─────────────────────────────────────────────────────────────┘
```

### Backend (Spring Boot)

```
┌─────────────────────────────────────────────────────────────┐
│  ESTÁGIO 1: BUILDER                                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  FROM maven:3.9.11-amazoncorretto-25-alpine           │  │
│  │  WORKDIR /app                                         │  │
│  │                                                        │  │
│  │  COPY pom.xml checkstyle.xml ./                       │  │
│  │  RUN mvn dependency:go-offline                        │  │
│  │                                                        │  │
│  │  COPY src ./src                                       │  │
│  │  RUN mvn clean package -DskipTests ──► Gera JAR      │  │
│  │                                           │            │  │
│  └───────────────────────────────────────────┼───────────┘  │
│                                              │               │
│                                              │ ifala-api.jar │
│                                              ▼               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ESTÁGIO 2: RUNTIME                                   │  │
│  │  FROM bellsoft/liberica-runtime-container:jre-25      │  │
│  │                                                        │  │
│  │  ENV SPRING_PROFILES_ACTIVE=prod                      │  │
│  │  COPY --from=builder /app/target/*.jar ifala-api.jar │  │
│  │                                                        │  │
│  │  ENTRYPOINT ["java", "-Xms512m", "-Xmx2g",           │  │
│  │              "-XX:+UseG1GC", "-jar", "ifala-api.jar"] │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  Resultado: Imagem ~300MB (vs ~800MB sem multi-stage)      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 Fluxo de Dados - Monitoramento

```
┌─────────────────────────────────────────────────────────────────┐
│                        COLETA DE DADOS                          │
│                                                                  │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐    │
│  │  Backend     │     │  PostgreSQL  │     │  Containers  │    │
│  │  (Actuator)  │     │  (Exporter)  │     │  (Docker)    │    │
│  └──────┬───────┘     └──────┬───────┘     └──────┬───────┘    │
│         │ métricas           │ métricas           │ logs        │
│         │ :8080/actuator     │ :9187              │             │
│         │                    │                    │             │
│         ▼                    ▼                    ▼             │
│  ┌────────────────────────────────────────────────────────┐    │
│  │                    Prometheus                          │    │
│  │  - Coleta métricas a cada 15s                         │    │
│  │  - Armazena por 30 dias                               │    │
│  │  - Query com PromQL                                   │    │
│  └────────────────────┬───────────────────────────────────┘    │
│                       │                                         │
│                       │                    ┌──────────────┐    │
│                       │                    │  Promtail    │    │
│                       │                    │  (Collector) │    │
│                       │                    └──────┬───────┘    │
│                       │                           │ push        │
│                       │                           ▼             │
│                       │                    ┌──────────────┐    │
│                       │                    │     Loki     │    │
│                       │                    │  (Log Store) │    │
│                       │                    └──────┬───────┘    │
│                       │                           │             │
│                       ▼                           ▼             │
│  ┌────────────────────────────────────────────────────────┐    │
│  │                      Grafana                           │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐  │    │
│  │  │  Dashboard   │  │  Dashboard   │  │  Dashboard │  │    │
│  │  │  Spring Boot │  │  PostgreSQL  │  │    Logs    │  │    │
│  │  └──────────────┘  └──────────────┘  └────────────┘  │    │
│  │                                                        │    │
│  │  Acesso: http://localhost:8081                        │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔐 Fluxo de Variáveis de Ambiente

```
┌─────────────────────────────────────────────────────────────┐
│  1. GERAÇÃO                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  generate-credentials.ps1                             │  │
│  │  ├─ JWT_SECRET (32 chars)                             │  │
│  │  ├─ POSTGRES_PASSWORD (24 chars)                      │  │
│  │  ├─ KEYCLOAK_ADMIN_PASSWORD (20 chars)                │  │
│  │  └─ GF_SECURITY_ADMIN_PASSWORD (20 chars)             │  │
│  └────────────────────────┬──────────────────────────────┘  │
│                           │                                 │
│                           ▼                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  2. ARMAZENAMENTO                                     │  │
│  │  .env (NÃO versionado no Git)                         │  │
│  │  ├─ POSTGRES_PASSWORD=xY9zAb3cDeF7gH1jKlMn            │  │
│  │  ├─ JWT_SECRET=aB3dEf7gH9jKlMnPqRsTuVwXyZ...          │  │
│  │  └─ ... (outras variáveis)                            │  │
│  └────────────────────────┬──────────────────────────────┘  │
│                           │                                 │
│                           ▼                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  3. REFERÊNCIA                                        │  │
│  │  docker-compose-prd.yml                               │  │
│  │  environment:                                         │  │
│  │    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}            │  │
│  │    JWT_SECRET: ${JWT_SECRET}                          │  │
│  └────────────────────────┬──────────────────────────────┘  │
│                           │                                 │
│                           ▼                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  4. INJEÇÃO EM RUNTIME                                │  │
│  │  Container Backend                                    │  │
│  │  ├─ application-prod.properties                       │  │
│  │  │  spring.datasource.password=${...PASSWORD}         │  │
│  │  └─ Valores substituídos automaticamente              │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Fluxo de Deploy

```
┌─────────────────────────────────────────────────────────────┐
│  PASSO 1: PREPARAÇÃO                                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  .\generate-credentials.ps1                           │  │
│  │  .\validate-prd.ps1                                   │  │
│  └───────────────────────┬───────────────────────────────┘  │
│                          │                                  │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  PASSO 2: BUILD DAS IMAGENS                           │  │
│  │  docker-compose -f docker-compose-prd.yml build       │  │
│  │                                                        │  │
│  │  ┌──────────────┐  ┌──────────────┐                  │  │
│  │  │  Frontend    │  │  Backend     │                  │  │
│  │  │  Multi-stage │  │  Multi-stage │                  │  │
│  │  │  Build       │  │  Build       │                  │  │
│  │  └──────┬───────┘  └──────┬───────┘                  │  │
│  │         │                 │                           │  │
│  │         ▼                 ▼                           │  │
│  │  Imagem NGINX      Imagem Spring Boot                │  │
│  │  (~50MB)           (~300MB)                           │  │
│  └───────────────────────┬───────────────────────────────┘  │
│                          │                                  │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  PASSO 3: INICIAR CONTAINERS                          │  │
│  │  docker-compose -f docker-compose-prd.yml up -d       │  │
│  │                                                        │  │
│  │  1. PostgreSQL (aguarda health check)                 │  │
│  │  2. Keycloak (aguarda PostgreSQL)                     │  │
│  │  3. Backend (aguarda PostgreSQL + Keycloak)           │  │
│  │  4. Frontend (aguarda Backend)                        │  │
│  │  5. Prometheus, Grafana, Loki, Promtail               │  │
│  └───────────────────────┬───────────────────────────────┘  │
│                          │                                  │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  PASSO 4: VERIFICAÇÃO                                 │  │
│  │  .\deploy-prd.ps1 -Action status                      │  │
│  │  .\deploy-prd.ps1 -Action logs                        │  │
│  │                                                        │  │
│  │  ✓ Todos containers running                           │  │
│  │  ✓ Health checks OK                                   │  │
│  │  ✓ Sem erros nos logs                                 │  │
│  └───────────────────────┬───────────────────────────────┘  │
│                          │                                  │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ✅ DEPLOY COMPLETO                                   │  │
│  │  http://localhost:8080 ← Frontend                     │  │
│  │  http://localhost:8081 ← Grafana                      │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Estrutura de Volumes

```
Docker Volumes (Persistência de Dados)
│
├─ pgdata_prd (PostgreSQL)
│  └─ /var/lib/postgresql/data
│     ├─ base/
│     ├─ global/
│     ├─ pg_wal/
│     └─ ... (dados do banco)
│
├─ keycloak_data_prd (Keycloak)
│  └─ /opt/keycloak/data
│     ├─ h2/
│     └─ ... (configurações, realms, usuários)
│
├─ grafana_data_prd (Grafana)
│  └─ /var/lib/grafana
│     ├─ dashboards/
│     ├─ plugins/
│     └─ ... (configurações, dashboards customizados)
│
├─ loki_data_prd (Loki)
│  └─ /loki
│     ├─ chunks/
│     ├─ index/
│     └─ ... (logs armazenados)
│
├─ promtail_positions_prd (Promtail)
│  └─ /var/lib/promtail
│     └─ positions.yaml (posições de leitura de logs)
│
└─ prometheus_data_prd (Prometheus)
   └─ /prometheus
      ├─ chunks_head/
      ├─ wal/
      └─ ... (métricas históricas)
```

---

## 🎯 Matriz de Decisão - Quando Usar Cada Arquivo

```
┌────────────────────────────────────────────────────────────┐
│  SITUAÇÃO                  │  ARQUIVO A CONSULTAR          │
├────────────────────────────┼───────────────────────────────┤
│  Primeiro deploy           │  QUICK-START.md               │
│  Entender sistema completo │  PRODUCTION.md                │
│  Antes de cada deploy      │  DEPLOY-CHECKLIST.md          │
│  Configurar NGINX          │  NGINX-CONFIG.md              │
│  Usar scripts              │  SCRIPTS-README.md            │
│  Visão geral do projeto    │  SUMMARY-PRODUCTION.md        │
│  Encontrar documentação    │  INDEX-PRODUCTION.md          │
│  Ver status da task        │  TASK-COMPLETED.md            │
│  Entender arquitetura      │  DIAGRAMS.md (este arquivo)   │
└────────────────────────────────────────────────────────────┘
```

---

**Este arquivo contém todos os diagramas visuais da arquitetura de produção.**  
**Use-o como referência rápida para entender o sistema.**
