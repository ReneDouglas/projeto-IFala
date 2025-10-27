# 🚀 Guia Rápido de Deploy em Produção

## ⏱️ 5 Minutos para Deploy

### Passo 1: Gerar Credenciais (1 min)
```powershell
.\generate-credentials.ps1
```
- Escolha opção **3** (Gerar TODAS as credenciais)
- Anote ou salve as credenciais geradas

### Passo 2: Configurar .env (2 min)
```powershell
# Copiar template
Copy-Item .env.example .env

# Editar arquivo (abrirá no Notepad)
notepad .env
```
Cole as credenciais geradas no passo anterior:
- `POSTGRES_PASSWORD=...`
- `SPRING_DATASOURCE_PASSWORD=...` (mesma do PostgreSQL)
- `JWT_SECRET=...`
- `KEYCLOAK_ADMIN_PASSWORD=...`
- `GF_SECURITY_ADMIN_PASSWORD=...`

Salve e feche.

### Passo 3: Validar Configuração (30 seg)
```powershell
.\validate-prd.ps1
```
✅ Se tudo OK, prossiga  
❌ Se houver erros, corrija antes de continuar

### Passo 4: Deploy! (1-2 min)
```powershell
.\deploy-prd.ps1 start
# ou
docker-compose -f docker-compose-prd.yml up -d --build
```

### Passo 5: Verificar (30 seg)
```powershell
# Ver status
docker-compose -f docker-compose-prd.yml ps

# Ver logs
docker-compose -f docker-compose-prd.yml logs -f
```

Aguarde todos os serviços ficarem `healthy` (pode levar 1-2 minutos).

## 🌐 Acessar Aplicação

Abra no navegador:
- **Frontend**: http://localhost:8080
- **Grafana**: http://localhost:8081

## 📊 Fluxograma Visual

```
┌─────────────────────────────────────────────────────────┐
│  1. Gerar Credenciais                                   │
│     .\generate-credentials.ps1                          │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│  2. Configurar .env                                     │
│     Copy-Item .env.example .env                         │
│     notepad .env                                        │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│  3. Validar                                             │
│     .\validate-prd.ps1                                  │
└────────────────┬────────────────────────────────────────┘
                 │
                 ├─── ❌ Erros? ──→ Corrigir e voltar
                 │
                 ▼ ✅ OK
┌─────────────────────────────────────────────────────────┐
│  4. Deploy                                              │
│     .\deploy-prd.ps1 start                              │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│  5. Verificar                                           │
│     - Status: docker-compose -f ... ps                  │
│     - Logs: docker-compose -f ... logs -f               │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│  ✅ SUCESSO!                                            │
│     http://localhost:8080 (Frontend)                    │
│     http://localhost:8081 (Grafana)                     │
└─────────────────────────────────────────────────────────┘
```

## 🏗️ Arquitetura de Produção

```
                    ┌─────────────────┐
                    │   Navegador     │
                    └────────┬────────┘
                             │
                    http://localhost:8080
                             │
                             ▼
            ┌────────────────────────────────┐
            │  NGINX (Container - porta 80)  │
            │  └─ Arquivos React (build)     │
            └────────┬───────────────┬───────┘
                     │               │
         Rotas React │               │ /api/*
         (/, /login) │               │
                     │               ▼
                     │    ┌──────────────────────┐
                     │    │  Backend Container   │
                     │    │  Spring Boot (8080)  │
                     │    │  Profile: prod       │
                     │    └──────────┬───────────┘
                     │               │
                     │               ▼
                     │    ┌──────────────────────┐
                     │    │  PostgreSQL (5432)   │
                     │    │  Volume: pgdata_prd  │
                     │    └──────────────────────┘
                     │
                     ▼
         index.html servido
```

## 📦 Containers em Produção

| Container | Imagem | Porta | Restart | Health |
|-----------|--------|-------|---------|--------|
| **ifala-frontend-prd** | nginx:alpine | 8080→80 | always | ✓ |
| **ifala-backend-prd** | custom (JRE 25) | - | always | ✓ |
| **ifala-db-prd** | postgres:16 | - | always | ✓ |
| **keycloak-prd** | keycloak:25 | 9090 | always | - |
| **prometheus-prd** | prometheus | - | always | - |
| **grafana-prd** | grafana | 8081→3000 | always | - |
| **loki-prd** | loki | - | always | - |
| **promtail-prd** | promtail | - | always | - |

## 🔧 Comandos Úteis

### Ver logs em tempo real
```powershell
docker-compose -f docker-compose-prd.yml logs -f ifala-backend
docker-compose -f docker-compose-prd.yml logs -f ifala-frontend
```

### Reiniciar um serviço
```powershell
docker-compose -f docker-compose-prd.yml restart ifala-backend
```

### Rebuild de um serviço
```powershell
docker-compose -f docker-compose-prd.yml up -d --build ifala-frontend
```

### Parar tudo
```powershell
docker-compose -f docker-compose-prd.yml down
```

### Ver uso de recursos
```powershell
docker stats
```

## 🐛 Troubleshooting Rápido

### Frontend não carrega (404)
```powershell
# Verificar se NGINX está OK
docker exec -it ifala-frontend-prd nginx -t

# Ver logs
docker logs ifala-frontend-prd
```

### API não responde (502)
```powershell
# Verificar se backend está rodando
docker ps | findstr backend

# Ver logs do backend
docker logs ifala-backend-prd

# Testar diretamente
curl http://localhost:8080/api/health
```

### Banco não conecta
```powershell
# Verificar se PostgreSQL está healthy
docker-compose -f docker-compose-prd.yml ps postgres

# Ver logs do banco
docker logs ifala-db-prd
```

### Container não inicia
```powershell
# Ver motivo
docker-compose -f docker-compose-prd.yml ps
docker logs <nome-do-container>

# Verificar .env
cat .env | findstr PASSWORD
```

## 📋 Checklist Pré-Deploy

- [ ] Docker Desktop está rodando
- [ ] Arquivo `.env` criado e preenchido
- [ ] Senhas alteradas (não usar valores de exemplo)
- [ ] JWT_SECRET tem pelo menos 32 caracteres
- [ ] Validação passou sem erros (`.\validate-prd.ps1`)
- [ ] Pelo menos 10GB de espaço em disco
- [ ] Portas 8080 e 8081 disponíveis

## 🎯 Próximos Passos Após Deploy

1. **Testar funcionalidades críticas**
   - Login
   - Criação de denúncia
   - Navegação

2. **Configurar Grafana**
   - Acesse http://localhost:8081
   - Login com credenciais do .env
   - Verifique dashboards

3. **Monitorar logs**
   - Verifique se não há erros críticos
   - Configure alertas se necessário

4. **Backup**
   - Configure backup do volume PostgreSQL
   - Documente procedimento de restore

## 📚 Documentação Completa

Para informações detalhadas, consulte:
- **PRODUCTION.md** - Guia completo de produção
- **DEPLOY-CHECKLIST.md** - Checklist detalhado
- **NGINX-CONFIG.md** - Configuração do NGINX
- **SUMMARY-PRODUCTION.md** - Resumo de tudo que foi criado

## ⚠️ IMPORTANTE

### O que NÃO está configurado (por design da task)
- ❌ HTTPS/SSL (apenas localhost)
- ❌ IP público (apenas localhost)
- ❌ Certificados SSL
- ❌ Domain name

### Para produção REAL, você precisará:
- ✅ Configurar HTTPS
- ✅ Configurar domínio
- ✅ Obter certificado SSL
- ✅ Configurar firewall
- ✅ Implementar rate limiting
- ✅ Configurar backups automáticos
- ✅ Configurar CI/CD

---

**Boa sorte com o deploy! 🚀**
