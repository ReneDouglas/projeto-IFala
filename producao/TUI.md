# TUI - Terminal User Interface (tui.sh)

## Visao Geral

O **tui.sh** e um script Bash interativo que gerencia o deploy e manutencao da aplicacao IFala em ambiente de producao. Ele fornece um menu colorido e intuitivo com opcoes para build, deploy, backup e monitoramento.

## Caracteristicas

- Interface interativa com menu numerado
- Mensagens coloridas (verde, vermelho, amarelo, azul)
- Confirmacoes antes de operacoes destrutivas
- Verificacao automatica de pre-requisitos
- Feedback de progresso em tempo real
- Logs estruturados e faceis de ler

---

## Menu Principal

```
========================================
     IFala - Deploy em Producao
========================================

MENU PRINCIPAL
----------------------------------------
1) Deploy completo (rapido)
2) Deploy com rebuild de imagens
3) Deploy com limpeza de volumes
4) Deploy completo (rebuild + limpeza)
5) Atualizar sistema (git pull + backup + rebuild)
6) Backup do banco de dados
7) Ver status dos servicos
8) Ver logs em tempo real
9) Parar todos os servicos
0) Sair
----------------------------------------

Escolha uma opcao:
```

---

## Opcoes Detalhadas

### Opcao 1: Deploy completo (rapido)

**O que faz:**
- Usa imagens Docker ja existentes (nao reconstroi)
- NAO apaga volumes/dados
- Apenas sobe os containers

**Flags internas:**
```bash
BUILD=false
CLEAN=false
LOGS=false
```

**Passos executados:**
1. Verificar pre-requisitos (Docker, docker-compose, arquivos)
2. `docker compose -f docker-compose-prd.yml up -d`
3. Aguardar containers ficarem saudaveis (ate 30 tentativas)
4. Mostrar status dos containers
5. Exibir URLs de acesso

**Tempo estimado:** 10-30 segundos

**Quando usar:**
- Desenvolvimento continuo
- Codigo nao mudou
- Quer subir rapidamente sem perder dados

---

### Opcao 2: Deploy com rebuild de imagens

**O que faz:**
- Reconstroi todas as imagens do zero (`--no-cache`)
- Mantem volumes/dados existentes
- Sobe containers com novas imagens

**Flags internas:**
```bash
BUILD=true
CLEAN=false
LOGS=false
```

**Passos executados:**
1. Verificar pre-requisitos
2. Pergunta confirmacao para rebuild
3. `docker compose build --no-cache`
4. `docker compose up -d`
5. Aguardar saude dos containers
6. Mostrar status e URLs

**Tempo estimado:** 5-15 minutos

**Quando usar:**
- Alterou codigo do backend (Java/Spring)
- Alterou codigo do frontend (React)
- Mudou Dockerfile.prd
- Dados do banco devem ser preservados

---

### Opcao 3: Deploy com limpeza de volumes

**O que faz:**
- Usa imagens existentes
- **APAGA TUDO**: banco, Keycloak, Grafana, Prometheus
- `docker compose down -v`
- Sobe containers com dados zerados

**Flags internas:**
```bash
BUILD=false
CLEAN=true
LOGS=false
```

**Avisos mostrados:**
```
[AVISO] ATENCAO: Esta operacao vai:
  - Parar todos os containers
  - Remover todos os volumes
  - APAGAR TODOS OS DADOS do banco de dados
  - APAGAR configuracoes do Keycloak
  - APAGAR dados do Grafana e Prometheus

[?] Tem certeza que deseja continuar? [s/N]
```

**Passos executados:**
1. Verificar pre-requisitos
2. Mostrar aviso critico
3. Pedir confirmacao explicita
4. `docker compose down -v`
5. `docker compose up -d`
6. Banco executa `init.sql` novamente
7. Mostrar status e URLs

**Quando usar:**
- Banco de dados corrompido
- Quer comecar do zero (fresh start)
- Testando migrations/init.sql
- **CUIDADO**: Perde todos os dados!

---

### Opcao 4: Deploy completo (rebuild + limpeza)

**O que faz:**
- Limpa todos os volumes (perde dados)
- Reconstroi todas as imagens
- Deploy 100% do zero

**Flags internas:**
```bash
BUILD=true
CLEAN=true
LOGS=false
```

**Passos executados:**
1. Verificar pre-requisitos
2. Aviso de limpeza + confirmacao
3. `docker compose down -v`
4. Confirmacao de rebuild
5. `docker compose build --no-cache`
6. `docker compose up -d`
7. Mostrar status e URLs

**Tempo estimado:** 5-20 minutos

**Quando usar:**
- Mudou codigo E quer dados limpos
- Deployment em ambiente novo
- Testes de integracao do zero
- Release/producao (deploy limpo)

---

### Opcao 5: Atualizar sistema (git pull + backup + rebuild)

**O que faz:**
- Atualiza codigo do repositorio (git pull)
- Cria backup de seguranca do banco
- Reconstroi imagens Docker
- Reinicia servicos SEM apagar volumes

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
- Arquivo: `backup_update_20251029_143025.sql`
- Se PostgreSQL nao estiver rodando, pula backup com aviso

#### [3/4] Reconstruindo imagens
```bash
docker compose build --no-cache
```
- Reconstroi frontend e backend com codigo atualizado

#### [4/4] Reiniciando servicos
```bash
docker compose up -d
```
- Reinicia containers com novas imagens
- Dados preservados (nao usa `-v`)

**Confirmacao inicial:**
```
[AVISO] ATENCAO: Esta operacao vai:
  1. Fazer git pull origin main (atualizar codigo)
  2. Criar backup de seguranca do banco de dados
  3. Fazer rebuild das imagens Docker
  4. Reiniciar servicos (SEM apagar volumes)

[INFO] Seus dados serao preservados!

[?] Deseja realmente atualizar o sistema? [s/N]
```

**Quando usar:**
- Atualizar aplicacao em producao
- Deploy de nova versao
- Manter dados mas atualizar codigo

---

### Opcao 6: Backup do banco de dados

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
CLEAN=false      # Se deve apagar volumes
LOGS=false       # Se deve mostrar logs apos deploy
COMPOSE_FILE="docker-compose-prd.yml"  # Arquivo compose
```

Essas flags sao setadas pelas opcoes do menu e usadas pela funcao `execute_deploy()`.

---

## Fluxo de Execucao

### Exemplo: Usuario escolhe opcao 2 (Deploy com rebuild)

```
1. main() exibe menu
2. Usuario digita "2"
3. Case 2 executado:
   - BUILD=true
   - CLEAN=false
   - LOGS=false
   - execute_deploy()

4. execute_deploy():
   a. check_prerequisites()
   b. if CLEAN=true → clean_volumes() [PULA]
   c. if BUILD=true → build_images() [EXECUTA]
   d. start_services()
   e. wait_healthy()
   f. show_status()
   g. show_access_info()
   h. if LOGS=true → show_logs() [PULA]
   i. press_enter()

5. Volta para main() (while loop)
6. Menu exibido novamente
```

---

## Estrutura de Diretorios

```
projeto-IFala/
├── tui.sh                      # Script principal
├── docker-compose-prd.yml      # Compose de producao
├── .env                        # Variaveis de ambiente
├── backups/                    # Criado automaticamente
│   ├── backup_20251029_143025.sql
│   ├── backup_update_20251029_150130.sql
│   └── ...
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

1. **Deploy rapido** (opcao 1) - Na maioria das vezes
2. **Rebuild** (opcao 2) - Quando altera codigo
3. **Ver logs** (opcao 8) - Para debugar

### Para Producao

1. **Backup** (opcao 6) - Antes de qualquer mudanca
2. **Atualizar sistema** (opcao 5) - Para novos deploys
3. **Ver status** (opcao 7) - Monitoramento periodico

### Antes de Operacoes Destrutivas

1. **Backup** (opcao 6) - Sempre
2. **Git commit** - Garantir codigo versionado
3. **Confirmar duas vezes** - Ler avisos com atencao

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
**Solucao**: Subir containers primeiro (opcao 1)

### Git pull falha

**Erro**: `[ERRO] Falha ao atualizar codigo!`
**Solucao**: 
```bash
git status  # verificar estado
git stash   # guardar mudancas locais
git pull origin main
```

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

### 2. Restaurar backup

Nova opcao para restaurar backups:
```
10) Restaurar backup do banco
```

### 3. Logs de servico especifico

```
Ver logs de qual servico?
1) Backend
2) Frontend
3) NGINX
4) PostgreSQL
5) Todos
```

### 4. Healthcheck detalhado

Mostrar saude individual de cada servico:
```
nginx-gateway-prd    [OK] healthy
ifala-backend-prd    [OK] healthy
ifala-db-prd         [WARN] starting...
```

### 5. Rollback automatico

Opcao para voltar para versao anterior em caso de falha

---

## Referencias

- [Bash Scripting Guide](https://www.gnu.org/software/bash/manual/)
- [Docker Compose CLI](https://docs.docker.com/compose/reference/)
- [ANSI Color Codes](https://en.wikipedia.org/wiki/ANSI_escape_code)
