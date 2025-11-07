# TUI - Terminal User Interface (tui.sh)

## Visao Geral

O **tui.sh** e um script Bash interativo que gerencia o deploy e manutencao da aplicacao IFala em ambiente de producao. Ele fornece um menu colorido e intuitivo com opcoes para build, deploy, backup e monitoramento.

**Versão OTIMIZADA** com proteção de volumes externos e menu reestruturado em dois níveis.

## Caracteristicas

- Interface interativa com menu numerado (dois níveis)
- Mensagens coloridas (verde, vermelho, amarelo, azul)
- Confirmacoes antes de operacoes destrutivas
- Verificacao automatica de pre-requisitos
- **Protecao de volumes externos** (PostgreSQL e Keycloak)
- **Volumes criados automaticamente** se não existirem
- Feedback de progresso em tempo real
- Logs estruturados e faceis de ler
- **Opcoes perigosas removidas** (limpeza de volumes)

---

## Menu Inicial

```
========================================
     IFala - Deploy em Producao
========================================

MENU INICIAL
----------------------------------------
1) Realizar Deploy
2) Acessar menu detalhado
0) Sair
----------------------------------------

Escolha uma opcao:
```

---

## Menu Detalhado

```
========================================
     IFala - Deploy em Producao
========================================

MENU DETALHADO
----------------------------------------
1) Reiniciar Servicos
2) Reconstruir Servicos
3) Backup do banco de dados
4) Ver status dos servicos
5) Ver logs em tempo real
0) Voltar para o menu inicial
----------------------------------------

Escolha uma opcao:
```

---

## Opcoes do Menu Inicial

### Opcao 1: Realizar Deploy

**O que faz:**
- Atualiza codigo do repositorio (git pull)
- Cria backup de seguranca do banco
- Reconstroi todas as imagens Docker
- Reinicia servicos com codigo atualizado
- **Preserva todos os dados** (volumes externos protegidos)

**Passos executados:**

#### [1/4] Atualizando codigo do repositorio
```bash
git pull origin main
```
- Se falhar, pergunta se quer continuar mesmo assim

#### [2/4] Criando backup de seguranca
```bash
docker exec ifala-db-prd pg_dump -U postgres -d ifala > backups/backup_update_YYYYMMDD_HHMMSS.sql
```
- Cria diretorio `./backups` se nao existir
- Arquivo: `backup_update_20251107_143025.sql`
- Se PostgreSQL nao estiver rodando, pula backup com aviso

#### [3/4] Reconstruindo imagens
```bash
docker compose -f docker-compose-prd.yml down
docker compose -f docker-compose-prd.yml build --no-cache
```
- Para servicos primeiro
- Reconstroi frontend e backend com codigo atualizado

#### [4/4] Reiniciando servicos
```bash
docker compose -f docker-compose-prd.yml up -d
```
- Inicia containers com novas imagens
- Dados preservados (volumes externos protegidos)

**Confirmacao inicial:**
```
[AVISO] ATENCAO: Esta operacao vai:
  1. Fazer git pull origin main (atualizar codigo)
  2. Criar backup de seguranca do banco de dados
  3. Fazer rebuild das imagens Docker
  4. Reiniciar servicos

[INFO] Seus dados serao preservados (volumes externos protegidos)!

[?] Deseja realmente atualizar o sistema? [s/N]
```

**Tempo estimado:** 5-15 minutos

**Quando usar:**
- Atualizar aplicacao em producao
- Deploy de nova versao
- Codigo foi alterado no repositorio
- **Opcao principal recomendada para deploys**

---

### Opcao 2: Acessar menu detalhado

**O que faz:**
- Abre submenu com operacoes avancadas
- Permite acesso a funcoes especificas
- Menu secundario para manutencao

**Quando usar:**
- Precisa de operacoes especificas
- Manutencao e monitoramento
- Backup manual
- Visualizar logs

---

### Opcao 0: Sair

**O que faz:**
- Encerra o script
- Volta ao terminal normal

---

## Opcoes do Menu Detalhado

### Opcao 1: Reiniciar Servicos

**O que faz:**
- Para todos os containers
- Reinicia todos os servicos
- **Preserva volumes e dados**

**Confirmacao:**
```
[?] Deseja reiniciar todos os servicos? [s/N]
```

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml down
docker compose -f docker-compose-prd.yml up -d
```

**Passos executados:**
1. Confirmacao do usuario
2. Para servicos (down)
3. Inicia servicos novamente (up -d)
4. Aguarda containers ficarem saudaveis
5. Mostra status dos servicos
6. Exibe URLs de acesso

**Tempo estimado:** 30-60 segundos

**Quando usar:**
- Servicos travados ou com problemas
- Limpar memoria/cache dos containers
- Aplicar configuracoes do .env
- **Nao reconstroi imagens** (rapido)

---

### Opcao 2: Reconstruir Servicos

**O que faz:**
- Para todos os containers
- Reconstroi todas as imagens do zero (`--no-cache`)
- Reinicia servicos com novas imagens
- **Preserva todos os dados** (volumes externos protegidos)

**Confirmacao:**
```
[AVISO] ATENCAO: Esta operacao vai:
  1. Parar todos os containers
  2. Reconstruir imagens Docker (pode demorar varios minutos)
  3. Reiniciar servicos

[INFO] Seus dados serao preservados (volumes externos protegidos)!

[?] Deseja realmente reconstruir os servicos? [s/N]
```

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml down
docker compose -f docker-compose-prd.yml build --no-cache
docker compose -f docker-compose-prd.yml up -d
```

**Passos executados:**
1. Confirmacao do usuario
2. Para servicos (down)
3. Reconstroi imagens (build --no-cache)
4. Inicia servicos (up -d)
5. Aguarda containers ficarem saudaveis
6. Mostra status dos servicos
7. Exibe URLs de acesso

**Tempo estimado:** 5-15 minutos

**Quando usar:**
- Alterou codigo do backend (Java/Spring)
- Alterou codigo do frontend (React)
- Mudou Dockerfile.prd
- Precisa reconstruir sem atualizar repositorio
- **Nao faz git pull** (apenas reconstroi)

---

### Opcao 3: Backup do banco de dados

**O que faz:**
- Cria backup do PostgreSQL usando pg_dump
- Salva em `./backups/backup_YYYYMMDD_HHMMSS.sql`
- Mostra tamanho do arquivo criado

**Passos executados:**
1. Verificar se container `ifala-db-prd` esta rodando
2. Criar diretorio `./backups` se nao existir
3. Executar pg_dump:
```bash
docker exec ifala-db-prd pg_dump -U postgres -d ifala > backups/backup_20251107_143025.sql
```
4. Mostrar resultado:
```
[OK] Backup criado com sucesso!

  Arquivo: ./backups/backup_20251107_143025.sql
  Tamanho: 2.5M
```

**Quando usar:**
- Antes de operacoes arriscadas
- Backup preventivo periodico
- Antes de atualizar sistema
- Antes de testar migrations

**Restaurar backup:**
```bash
docker exec -i ifala-db-prd psql -U postgres -d ifala < backups/backup_20251107_143025.sql
```

---

### Opcao 4: Ver status dos servicos

**O que faz:**
- Mostra tabela com todos os containers
- Status (Up, Down, Restarting)
- Healthcheck (healthy, unhealthy, starting)
- Portas expostas

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml ps
```

**Exemplo de saida:**
```
STATUS DOS SERVICOS
----------------------------------------

NAME                 STATUS                    PORTS
nginx-gateway-prd    Up 5 minutes (healthy)    0.0.0.0:80->80/tcp
ifala-backend-prd    Up 5 minutes (healthy)    
ifala-frontend-prd   Up 5 minutes              
ifala-db-prd         Up 5 minutes (healthy)    
keycloak-prd         Up 5 minutes              
prometheus-prd       Up 5 minutes              
grafana-prd          Up 5 minutes              
loki-prd             Up 5 minutes              
promtail-prd         Up 5 minutes              
```

**Quando usar:**
- Verificar se containers estao rodando
- Ver quanto tempo estao UP
- Debugar problemas
- Verificar healthchecks

---

### Opcao 5: Ver logs em tempo real

**O que faz:**
- Mostra logs de **todos os containers** ao vivo
- Logs coloridos por servico
- Atualiza em tempo real (streaming)

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml logs -f
```

**Exemplo de saida:**
```
[INFO] Exibindo logs (Ctrl+C para sair)...

nginx-gateway-prd  | 192.168.1.10 - - [07/Nov/2025:14:30:15] "GET /api/users HTTP/1.1" 200
ifala-backend-prd  | 2025-11-07 14:30:15 INFO  - Processing request...
ifala-db-prd       | 2025-11-07 14:30:15 UTC LOG:  statement: SELECT * FROM users
grafana-prd        | logger=context userId=1 t=2025-11-07T14:30:15.123 level=info
```

**Como usar:**
- Escolhe opcao 5
- Logs aparecem continuamente
- **Ctrl+C** para sair e voltar ao menu

**Quando usar:**
- Debugar erros
- Ver requisicoes chegando
- Monitorar comportamento
- Acompanhar startup

---

### Opcao 0: Voltar para o menu inicial

**O que faz:**
- Retorna ao menu inicial
- Nao encerra o script

---

## Protecao de Volumes Externos

### O que são volumes externos?

Volumes externos sao volumes Docker criados manualmente e configurados como `external: true` no `docker-compose-prd.yml`. Isso significa que:

- **NAO sao apagados** com `docker compose down -v`
- **Persistem** mesmo apos remover todos os containers
- **Precisam ser apagados manualmente** com `docker volume rm`

### Volumes protegidos no IFala

O script `tui.sh` protege automaticamente dois volumes criticos:

1. **pgdata_prd** - Dados do PostgreSQL (banco de dados)
2. **keycloak_data_prd** - Configuracoes do Keycloak (autenticacao)

### Criacao automatica

Na verificacao de pre-requisitos, o script cria esses volumes se nao existirem:

```bash
# Volume do PostgreSQL (CRITICO - protegido contra 'docker compose down -v')
if ! docker volume inspect pgdata_prd &> /dev/null; then
    print_warning "Volume 'pgdata_prd' nao existe. Criando..."
    docker volume create pgdata_prd
    print_success "Volume 'pgdata_prd' criado!"
else
    print_success "Volume 'pgdata_prd' encontrado"
fi

# Volume do Keycloak (CRITICO - protegido contra 'docker compose down -v')
if ! docker volume inspect keycloak_data_prd &> /dev/null; then
    print_warning "Volume 'keycloak_data_prd' nao existe. Criando..."
    docker volume create keycloak_data_prd
    print_success "Volume 'keycloak_data_prd' criado!"
else
    print_success "Volume 'keycloak_data_prd' encontrado"
fi
```

### Configuracao no docker-compose-prd.yml

```yaml
volumes:
  pgdata_prd:
    external: true  # Volume criado manualmente - protegido contra 'docker compose down -v'
  keycloak_data_prd:
    external: true  # Volume criado manualmente - protegido contra 'docker compose down -v'
  grafana_data_prd:
    driver: local   # Volumes internos (podem ser apagados com down -v)
  loki_data_prd:
    driver: local
  promtail_positions_prd:
    driver: local
  prometheus_data_prd:
    driver: local
```

### Teste de protecao

Para testar se os volumes estao protegidos, use o script PowerShell:

```powershell
.\scripts\test-volume-protection.ps1
```

Este script:
1. Verifica se volumes externos existem
2. Cria arquivo de teste no volume
3. Executa `docker compose down -v` (comando perigoso)
4. Verifica se arquivo ainda existe
5. Confirma protecao dos volumes

### Vantagens da protecao

- **Seguranca**: Impossivel apagar dados acidentalmente
- **Confiabilidade**: Dados preservados em qualquer operacao
- **Simplicidade**: Sem comandos perigosos no menu
- **Producao**: Adequado para ambiente de producao

---

## Mudancas da Versao Anterior

### Opcoes REMOVIDAS (perigosas)

Estas opcoes foram **removidas** por serem muito perigosas:

- ❌ **Opcao 3**: Deploy com limpeza de volumes
- ❌ **Opcao 4**: Deploy completo (rebuild + limpeza)
- ❌ **Opcao 9**: Parar todos os servicos

### Opcoes RENOMEADAS (clareza)

- ✅ **"Deploy completo (rapido)"** → **"Reiniciar Servicos"** (menu detalhado)
- ✅ **"Deploy com rebuild de imagens"** → **"Reconstruir Servicos"** (menu detalhado)
- ✅ **"Atualizar sistema"** → **"Realizar Deploy"** (menu inicial)

### Funcionalidades ADICIONADAS

- ✅ **Menu em dois niveis** (inicial + detalhado)
- ✅ **Protecao de volumes externos** (pgdata_prd, keycloak_data_prd)
- ✅ **Criacao automatica de volumes** (se nao existirem)
- ✅ **Mensagens mais claras** sobre preservacao de dados

### Variaveis de Controle

Antes:
```bash
BUILD=false
CLEAN=false  # REMOVIDA
LOGS=false
```

Agora:
```bash
BUILD=false
LOGS=false
```

---

## Funcoes Internas

**O que faz:**
- Cria backup do PostgreSQL usando pg_dump
- Salva em `./backups/backup_YYYYMMDD_HHMMSS.sql`
- Mostra tamanho do arquivo criado

**Passos executados:**
1. Verificar se container `ifala-db-prd` esta rodando
2. Criar diretorio `./backups` se nao existir
3. Executar pg_dump:
```bash
docker exec ifala-db-prd pg_dump -U postgres -d ifala > backups/backup_20251029_143025.sql
```
4. Mostrar resultado:
```
[OK] Backup criado com sucesso!

  Arquivo: ./backups/backup_20251029_143025.sql
  Tamanho: 2.5M
```

**Quando usar:**
- Antes de operacoes arriscadas
- Backup preventivo periodico
- Antes de atualizar sistema
- Antes de testar migrations

**Restaurar backup:**
```bash
docker exec -i ifala-db-prd psql -U postgres -d ifala < backups/backup_20251029_143025.sql
```

---

### Opcao 7: Ver status dos servicos

**O que faz:**
- Mostra tabela com todos os containers
- Status (Up, Down, Restarting)
- Healthcheck (healthy, unhealthy, starting)
- Portas expostas

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml ps
```

**Exemplo de saida:**
```
STATUS DOS SERVICOS
----------------------------------------

NAME                 STATUS                    PORTS
nginx-gateway-prd    Up 5 minutes (healthy)    0.0.0.0:80->80/tcp
ifala-backend-prd    Up 5 minutes (healthy)    
ifala-frontend-prd   Up 5 minutes              
ifala-db-prd         Up 5 minutes (healthy)    
keycloak-prd         Up 5 minutes              
prometheus-prd       Up 5 minutes              
grafana-prd          Up 5 minutes              
loki-prd             Up 5 minutes              
promtail-prd         Up 5 minutes              
```

**Quando usar:**
- Verificar se containers estao rodando
- Ver quanto tempo estao UP
- Debugar problemas
- Verificar healthchecks

---

### Opcao 8: Ver logs em tempo real

**O que faz:**
- Mostra logs de **todos os containers** ao vivo
- Logs coloridos por servico
- Atualiza em tempo real (streaming)

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml logs -f
```

**Exemplo de saida:**
```
[INFO] Exibindo logs (Ctrl+C para sair)...

nginx-gateway-prd  | 192.168.1.10 - - [29/Oct/2025:14:30:15] "GET /api/users HTTP/1.1" 200
ifala-backend-prd  | 2025-10-29 14:30:15 INFO  - Processing request...
ifala-db-prd       | 2025-10-29 14:30:15 UTC LOG:  statement: SELECT * FROM users
grafana-prd        | logger=context userId=1 t=2025-10-29T14:30:15.123 level=info
```

**Como usar:**
- Escolhe opcao 8
- Logs aparecem continuamente
- **Ctrl+C** para sair e voltar ao menu

**Quando usar:**
- Debugar erros
- Ver requisicoes chegando
- Monitorar comportamento
- Acompanhar startup

---

### Opcao 9: Parar todos os servicos

**O que faz:**
- Para todos os containers
- **NAO apaga** volumes (dados preservados)
- Containers removidos da memoria

**Confirmacao:**
```
[?] Deseja parar todos os servicos? [s/N]
```

**Comando executado:**
```bash
docker compose -f docker-compose-prd.yml down
```

**Diferenca importante:**
- `down` → Para containers, preserva volumes
- `down -v` → Para containers E apaga volumes (opcao 3)

**Quando usar:**
- Pausar desenvolvimento
- Liberar recursos (RAM/CPU)
- Fim do dia de trabalho
- Antes de manutencao do servidor

---

### Opcao 0: Sair

**O que faz:**
- Encerra o script
- Volta ao terminal normal

---

## Funcoes Internas

### check_prerequisites()

Verifica antes de qualquer deploy:

```bash
# Verificar Docker
if ! command -v docker &> /dev/null; then
    print_error "Docker nao encontrado!"
    exit 1
fi

# Verificar Docker Compose
if ! docker compose version &> /dev/null; then
    print_error "Docker Compose nao encontrado!"
    exit 1
fi

# Verificar docker-compose-prd.yml
if [ ! -f "$COMPOSE_FILE" ]; then
    print_error "Arquivo docker-compose-prd.yml nao encontrado!"
    exit 1
fi

# Verificar .env (avisa mas nao bloqueia)
if [ ! -f ".env" ]; then
    print_warning "Arquivo .env nao encontrado!"
    if confirm "Deseja continuar mesmo assim?"; then
        # continua
    fi
fi

# Criar volumes externos se nao existirem (NOVO!)
print_info "Verificando volumes externos..."

if ! docker volume inspect pgdata_prd &> /dev/null; then
    docker volume create pgdata_prd
fi

if ! docker volume inspect keycloak_data_prd &> /dev/null; then
    docker volume create keycloak_data_prd
fi
```

---

### wait_healthy()

Aguarda containers ficarem prontos:

```bash
maxAttempts=30
attempt=0

while [ $attempt -lt $maxAttempts ]; do
    attempt=$((attempt + 1))
    echo -ne "Verificando... tentativa $attempt/$maxAttempts\r"
    
    running=$(docker compose ps --filter "status=running" --format json | wc -l)
    
    if [ "$running" -gt 0 ]; then
        echo "Servicos estao em execucao!"
        break
    fi
    
    sleep 2
done
```

- Tenta ate 30 vezes
- Verifica a cada 2 segundos
- Mostra progresso em tempo real

---

### confirm()

Funcao para pedir confirmacao do usuario:

```bash
confirm "Deseja continuar?" "n"
```

Parametros:
- `$1`: Mensagem
- `$2`: Default (y ou n)

Retorno:
- `0` (true): Usuario respondeu Sim
- `1` (false): Usuario respondeu Nao

Exemplo:
```bash
if confirm "Deseja apagar tudo?"; then
    # usuario respondeu sim
else
    # usuario respondeu nao
fi
```

---

### print_* (Funcoes de Output)

```bash
print_success "Operacao concluida!"   # Verde [OK]
print_error "Falha ao conectar!"      # Vermelho [ERRO]
print_warning "Atencao: dados serao apagados!"  # Amarelo [AVISO]
print_info "Processando..."           # Azul [INFO]
```

Cores:
- Verde: Sucesso, operacao OK
- Vermelho: Erro critico
- Amarelo: Aviso, confirmacao necessaria
- Azul: Informacao, progresso
- Cinza: Output de comandos Docker

---

## Sistema de Cores

### Variaveis de Cor

```bash
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
GRAY='\033[0;90m'
BOLD='\033[1m'
NC='\033[0m'  # No Color (reset)
```

### Uso

```bash
echo -e "${GREEN}Sucesso!${NC}"
echo -e "${RED}Erro!${NC}"
echo -e "${BOLD}Titulo${NC}"
```

---

## Variaveis de Controle

```bash
BUILD=false      # Se deve rebuildar imagens
LOGS=false       # Se deve mostrar logs apos deploy
COMPOSE_FILE="docker-compose-prd.yml"  # Arquivo compose
```

**Nota**: A variavel `CLEAN` foi **removida** na versao otimizada.

---

## Fluxo de Execucao

### Exemplo: Usuario escolhe "Realizar Deploy" (menu inicial)

```
1. main() exibe menu inicial
2. Usuario digita "1"
3. Case 1 executado:
   - update_system()

4. update_system():
   a. Mostra confirmacao
   b. git pull origin main
   c. Backup do banco (se possivel)
   d. docker compose down
   e. docker compose build --no-cache
   f. docker compose up -d
   g. wait_healthy()
   h. show_status()
   i. show_access_info()
   j. press_enter()

5. Volta para main() (while loop)
6. Menu inicial exibido novamente
```

### Exemplo: Usuario escolhe "Reconstruir Servicos" (menu detalhado)

```
1. main() → opcao 2 → detailed_menu()
2. Usuario digita "2"
3. Case 2 executado:
   - rebuild_services()

4. rebuild_services():
   a. Mostra confirmacao
   b. docker compose down
   c. docker compose build --no-cache
   d. docker compose up -d
   e. wait_healthy()
   f. show_status()
   g. show_access_info()
   h. press_enter()

5. Volta para detailed_menu() (while loop)
6. Menu detalhado exibido novamente
```

---

## Estrutura de Diretorios

```
projeto-IFala/
├── tui.sh                      # Script principal (OTIMIZADO)
├── docker-compose-prd.yml      # Compose de producao (volumes externos)
├── .env                        # Variaveis de ambiente
├── backups/                    # Criado automaticamente
│   ├── backup_20251107_143025.sql
│   ├── backup_update_20251107_150130.sql
│   └── ...
├── scripts/
│   └── test-volume-protection.ps1  # Teste de protecao de volumes
├── nginx/
│   └── nginx.conf
└── apps/
    ├── ifala-backend/
    │   └── Dockerfile.prd
    └── ifala-frontend/
        └── Dockerfile.prd
```

---

## Uso

### 1. Tornar executavel

```bash
chmod +x tui.sh
```

### 2. Executar

```bash
./tui.sh
```

ou

```bash
bash tui.sh
```

### 3. Navegar no menu

- Digite o numero da opcao desejada
- Pressione ENTER
- Responda confirmacoes com `s` (sim) ou `n` (nao)

---

## Boas Praticas

### Para Desenvolvimento

1. **Reiniciar Servicos** (menu detalhado → opcao 1) - Para problemas rapidos
2. **Reconstruir Servicos** (menu detalhado → opcao 2) - Quando altera codigo
3. **Ver logs** (menu detalhado → opcao 5) - Para debugar

### Para Producao

1. **Backup** (menu detalhado → opcao 3) - Antes de qualquer mudanca
2. **Realizar Deploy** (menu inicial → opcao 1) - Para novos deploys
3. **Ver status** (menu detalhado → opcao 4) - Monitoramento periodico

### Antes de Operacoes Importantes

1. **Backup** (menu detalhado → opcao 3) - Sempre
2. **Git commit** - Garantir codigo versionado
3. **Confirmar duas vezes** - Ler avisos com atencao
4. **Testar protecao de volumes** - `.\scripts\test-volume-protection.ps1`

---

## Troubleshooting

### Script nao executa

**Erro**: `Permission denied`
```bash
chmod +x tui.sh
```

### Docker nao encontrado

**Erro**: `[ERRO] Docker nao encontrado!`
**Solucao**: Instalar Docker Desktop ou Docker Engine

### Arquivo .env nao encontrado

**Aviso**: `[AVISO] Arquivo .env nao encontrado!`
**Solucao**: Criar arquivo `.env` com variaveis necessarias:
```bash
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=ifala
# ...
```

### Container do banco nao esta rodando (backup)

**Erro**: `[ERRO] Container do PostgreSQL nao esta rodando!`
**Solucao**: Subir containers primeiro (menu inicial → opcao 1)

### Git pull falha

**Erro**: `[ERRO] Falha ao atualizar codigo!`
**Solucao**: 
```bash
git status  # verificar estado
git stash   # guardar mudancas locais
git pull origin main
```

### Volumes nao foram criados automaticamente

**Sintoma**: Mensagens de erro sobre volumes nao encontrados
**Solucao**: Criar volumes manualmente:
```bash
docker volume create pgdata_prd
docker volume create keycloak_data_prd
```

### Dados foram apagados acidentalmente

**Sintoma**: Banco de dados vazio apos restart
**Causa provavel**: Volumes nao estavam configurados como externos
**Solucao**: 
1. Restaurar do backup mais recente:
```bash
docker exec -i ifala-db-prd psql -U postgres -d ifala < backups/backup_YYYYMMDD_HHMMSS.sql
```
2. Verificar configuracao em `docker-compose-prd.yml`:
```yaml
volumes:
  pgdata_prd:
    external: true  # Deve estar assim
```

### Testar protecao de volumes

**Como verificar**: Executar script de teste:
```powershell
.\scripts\test-volume-protection.ps1
```

Este script confirma que os volumes externos estao protegidos contra `docker compose down -v`.

---

## Melhorias Futuras

### 1. Selecao de servicos individuais

Permitir rebuild apenas do backend ou frontend:
```
Qual servico rebuildar?
1) Backend
2) Frontend
3) Ambos
```

### 2. Logs de servico especifico

```
Ver logs de qual servico?
1) Backend
2) Frontend
3) NGINX
4) PostgreSQL
5) Todos
```

### 3. Healthcheck detalhado

Mostrar saude individual de cada servico:
```
nginx-gateway-prd    [OK] healthy
ifala-backend-prd    [OK] healthy
ifala-db-prd         [WARN] starting...
```

### 4. Gerenciamento de backups

Nova opcao para listar e restaurar backups:
```
1) Criar novo backup
2) Listar backups existentes
3) Restaurar backup especifico
```

### 5. Rollback automatico

Opcao para voltar para versao anterior em caso de falha:
```
1) Rollback para commit anterior
2) Rollback para backup especifico
```

### 6. Limpeza seletiva

Permitir limpar apenas volumes especificos (com confirmacao tripla):
```
AVISO CRITICO: Apagar volumes e IRREVERSIVEL!
Qual volume limpar?
1) Grafana (dados de dashboards)
2) Prometheus (metricas)
3) Loki (logs)
4) Cancelar
```

**Nota**: PostgreSQL e Keycloak nunca aparecem nesta lista (protegidos)

---

## Scripts Relacionados

### test-volume-protection.ps1

**Localizacao**: `scripts/test-volume-protection.ps1`

**Proposito**: Testar se volumes externos estao realmente protegidos

**Uso**:
```powershell
.\scripts\test-volume-protection.ps1
```

**O que faz**:
1. Verifica se volumes `pgdata_prd` e `keycloak_data_prd` existem
2. Cria arquivo de teste no volume pgdata_prd
3. Executa `docker compose down -v` (comando perigoso)
4. Verifica se arquivo ainda existe
5. Confirma que volumes NAO foram apagados

**Resultado esperado**:
```
[OK] SUCESSO! Arquivo de teste ainda existe!
[OK] Os volumes externos estao PROTEGIDOS contra 'docker compose down -v'!
```

---

## Comparacao: Versao Antiga vs Otimizada

| Aspecto | Versao Antiga | Versao Otimizada |
|---------|---------------|------------------|
| **Menu** | 1 nivel (10 opcoes) | 2 niveis (3 + 6 opcoes) |
| **Volumes** | Podem ser apagados | Protegidos (externos) |
| **Opcoes perigosas** | 2 opcoes (limpeza) | 0 opcoes (removidas) |
| **Deploy principal** | Opcao 5 | Opcao 1 (menu inicial) |
| **Clareza** | "Deploy completo (rapido)" | "Reiniciar Servicos" |
| **Seguranca** | Media (requer atencao) | Alta (protegido por design) |
| **Criacao de volumes** | Manual | Automatica |
| **Teste de protecao** | Nao existe | Script PowerShell |
| **Parar servicos** | Opcao separada (9) | Incluido em "Reiniciar" |

---

## Comandos Uteis

### Gerenciar volumes manualmente

```bash
# Listar todos os volumes
docker volume ls

# Inspecionar volume especifico
docker volume inspect pgdata_prd

# Ver tamanho do volume
docker system df -v

# Criar volume externo
docker volume create pgdata_prd

# Remover volume (CUIDADO!)
docker volume rm pgdata_prd
```

### Verificar configuracao do docker-compose

```bash
# Validar sintaxe
docker compose -f docker-compose-prd.yml config

# Listar servicos
docker compose -f docker-compose-prd.yml ps

# Ver volumes usados
docker compose -f docker-compose-prd.yml config --volumes
```

### Backup e restauracao manual

```bash
# Backup
docker exec ifala-db-prd pg_dump -U postgres -d ifala > backup.sql

# Restaurar
docker exec -i ifala-db-prd psql -U postgres -d ifala < backup.sql

# Backup comprimido
docker exec ifala-db-prd pg_dump -U postgres -d ifala | gzip > backup.sql.gz

# Restaurar comprimido
gunzip -c backup.sql.gz | docker exec -i ifala-db-prd psql -U postgres -d ifala
```

---

## Referencias

- [Bash Scripting Guide](https://www.gnu.org/software/bash/manual/)
- [Docker Compose CLI](https://docs.docker.com/compose/reference/)
- [Docker Volumes](https://docs.docker.com/storage/volumes/)
- [ANSI Color Codes](https://en.wikipedia.org/wiki/ANSI_escape_code)
- [PostgreSQL Backup & Restore](https://www.postgresql.org/docs/current/backup.html)
