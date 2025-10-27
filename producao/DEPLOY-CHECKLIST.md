# ✅ Checklist de Deploy em Produção - IFala

Use este checklist antes de fazer deploy em produção.

## 📋 Pré-Deploy

### Configuração de Ambiente

- [ ] Arquivo `.env` criado a partir de `.env.example`
- [ ] Todas as senhas no `.env` foram alteradas para senhas fortes e únicas
- [ ] `JWT_SECRET` gerado com pelo menos 32 caracteres (use: `openssl rand -base64 32`)
- [ ] Credenciais do PostgreSQL definidas (`POSTGRES_USER`, `POSTGRES_PASSWORD`)
- [ ] Credenciais do Keycloak definidas (`KEYCLOAK_ADMIN`, `KEYCLOAK_ADMIN_PASSWORD`)
- [ ] Credenciais do Grafana definidas (`GF_SECURITY_ADMIN_USER`, `GF_SECURITY_ADMIN_PASSWORD`)
- [ ] Arquivo `.env` está no `.gitignore` (NÃO versionar!)

### Arquivos Docker

- [ ] `apps/ifala-frontend/Dockerfile.prd` criado
- [ ] `apps/ifala-frontend/nginx.conf` configurado
- [ ] `apps/ifala-backend/Dockerfile.prd` criado
- [ ] `docker-compose-prd.yml` configurado
- [ ] Profile do Spring configurado como `prod` no docker-compose

### Backend

- [ ] `application-prod.properties` configurado com variáveis de ambiente
- [ ] Migrações do Flyway testadas
- [ ] Logs configurados apropriadamente
- [ ] Portas de debug DESABILITADAS (não expor porta 5005)
- [ ] Virtual Threads habilitados
- [ ] Configurações de memória da JVM apropriadas

### Frontend

- [ ] Build de produção testado localmente (`npm run build`)
- [ ] Variáveis de ambiente de produção configuradas (se aplicável)
- [ ] NGINX configurado para proxy reverso do backend
- [ ] Rotas do React Router funcionando (try_files configurado)
- [ ] Assets estáticos com cache habilitado

### Banco de Dados

- [ ] Script de inicialização `init.sql` revisado
- [ ] Backup do banco existente realizado (se aplicável)
- [ ] Configurações de performance do PostgreSQL ajustadas
- [ ] Health check configurado

### Segurança

- [ ] Cookies configurados como `http-only` e `secure`
- [ ] Headers de segurança no NGINX (X-Frame-Options, X-Content-Type-Options, etc.)
- [ ] Senhas não estão hardcoded no código
- [ ] JWT Secret único e seguro
- [ ] CORS configurado corretamente

### Monitoramento

- [ ] Prometheus configurado e coletando métricas
- [ ] Grafana acessível na porta 8081
- [ ] Dashboards importados e funcionando
- [ ] Loki recebendo logs
- [ ] Promtail configurado para coletar logs dos containers
- [ ] Alertas configurados (se aplicável)

## 🚀 Deploy

### Build

- [ ] `docker-compose -f docker-compose-prd.yml build` executado com sucesso
- [ ] Todas as imagens foram criadas sem erros
- [ ] Tamanho das imagens é razoável (não excessivo)

### Inicialização

- [ ] `docker-compose -f docker-compose-prd.yml up -d` executado
- [ ] Todos os containers iniciaram corretamente
- [ ] Health checks passando
- [ ] Logs não mostram erros críticos

### Verificação de Serviços

- [ ] PostgreSQL está rodando e aceitando conexões
- [ ] Backend está respondendo em http://localhost:8080/api
- [ ] Frontend está acessível em http://localhost:8080
- [ ] NGINX está servindo arquivos estáticos
- [ ] Proxy reverso do NGINX funcionando (/api)
- [ ] Keycloak está acessível
- [ ] Grafana está acessível em http://localhost:8081
- [ ] Prometheus está coletando métricas

### Testes Funcionais

- [ ] Login funciona corretamente
- [ ] Criação de denúncia funciona
- [ ] Navegação entre páginas funciona
- [ ] API endpoints respondem corretamente
- [ ] Autenticação/Autorização funcionando
- [ ] Logs sendo gravados corretamente

## 📊 Pós-Deploy

### Monitoramento

- [ ] Verificar dashboards do Grafana
- [ ] Conferir métricas do Prometheus
- [ ] Revisar logs no Loki
- [ ] Verificar uso de CPU/Memória dos containers
- [ ] Confirmar que não há memory leaks

### Performance

- [ ] Tempo de resposta da API aceitável
- [ ] Frontend carrega rapidamente
- [ ] Assets estáticos com cache funcionando
- [ ] Compressão Gzip ativa
- [ ] Queries do banco otimizadas

### Backup

- [ ] Volume do PostgreSQL está persistindo dados
- [ ] Backup automático configurado (se aplicável)
- [ ] Procedimento de restore testado

## 🐛 Troubleshooting

### Se algo der errado:

```powershell
# Ver logs de todos os serviços
docker-compose -f docker-compose-prd.yml logs -f

# Ver logs de um serviço específico
docker-compose -f docker-compose-prd.yml logs -f ifala-backend

# Verificar status
docker-compose -f docker-compose-prd.yml ps

# Reiniciar um serviço
docker-compose -f docker-compose-prd.yml restart ifala-backend

# Rebuild e restart
docker-compose -f docker-compose-prd.yml up -d --build ifala-backend
```

## 📝 Documentação

- [ ] `PRODUCTION.md` atualizado
- [ ] Equipe informada sobre o deploy
- [ ] Credenciais compartilhadas de forma segura
- [ ] Runbook de operação criado

## ⚠️ Notas Importantes

### NÃO FAZER em Produção:

- ❌ Não expor porta de debug (5005)
- ❌ Não usar senhas padrão
- ❌ Não versionar o arquivo `.env`
- ❌ Não desabilitar HTTPS em produção real (só localhost)
- ❌ Não usar `restart: no` nos serviços críticos

### FAZER em Produção:

- ✅ Usar `restart: always` ou `restart: unless-stopped`
- ✅ Configurar health checks
- ✅ Usar volumes nomeados para persistência
- ✅ Limitar recursos dos containers (se necessário)
- ✅ Configurar logs rotation
- ✅ Manter backups regulares

## 🔄 Rollback

Se precisar voltar para versão anterior:

```powershell
# 1. Parar containers
docker-compose -f docker-compose-prd.yml down

# 2. Fazer checkout da versão anterior
git checkout <commit-hash>

# 3. Rebuild e restart
docker-compose -f docker-compose-prd.yml up -d --build
```

---

**Data do Deploy:** ___/___/______  
**Responsável:** _________________  
**Versão:** _____________________
