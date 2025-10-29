#!/bin/bash

# ========================================
# Script de Deploy INTERATIVO - Producao
# ========================================
# Terminal interativo com menu, confirmacoes e avisos

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
GRAY='\033[0;90m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Variaveis de controle
BUILD=false
CLEAN=false
LOGS=false
COMPOSE_FILE="docker-compose-prd.yml"

# Funcoes auxiliares
print_header() {
    clear
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}     IFala - Deploy em Producao${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo ""
}

print_separator() {
    echo -e "${GRAY}----------------------------------------${NC}"
}

print_success() {
    echo -e "${GREEN}[OK] $1${NC}"
}

print_error() {
    echo -e "${RED}[ERRO] $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[AVISO] $1${NC}"
}

print_info() {
    echo -e "${BLUE}[INFO] $1${NC}"
}

press_enter() {
    echo ""
    read -p "Pressione ENTER para continuar..."
}

confirm() {
    local message=$1
    local default=${2:-n}
    
    if [ "$default" = "y" ]; then
        local prompt="[S/n]"
    else
        local prompt="[s/N]"
    fi
    
    echo -e "${YELLOW}[?] $message $prompt${NC}"
    read -r resposta
    
    resposta=${resposta:-$default}
    
    if [[ "$resposta" =~ ^[Ss]$ ]]; then
        return 0
    else
        return 1
    fi
}

show_menu() {
    print_header
    echo -e "${BOLD}MENU PRINCIPAL${NC}"
    print_separator
    echo -e "${CYAN}1)${NC} Deploy completo (rapido)"
    echo -e "${CYAN}2)${NC} Deploy com rebuild de imagens"
    echo -e "${CYAN}3)${NC} Deploy com limpeza de volumes"
    echo -e "${CYAN}4)${NC} Deploy completo (rebuild + limpeza)"
    echo -e "${CYAN}5)${NC} Atualizar sistema (git pull + backup + rebuild)"
    echo -e "${CYAN}6)${NC} Backup do banco de dados"
    echo -e "${CYAN}7)${NC} Ver status dos servicos"
    echo -e "${CYAN}8)${NC} Ver logs em tempo real"
    echo -e "${CYAN}9)${NC} Parar todos os servicos"
    echo -e "${CYAN}0)${NC} Sair"
    print_separator
    echo ""
}

check_prerequisites() {
    print_info "Verificando prerequisitos..."
    echo ""
    
    # Verificar Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker nao encontrado! Instale o Docker primeiro."
        exit 1
    fi
    print_success "Docker instalado"
    
    # Verificar Docker Compose
    if ! docker compose version &> /dev/null; then
        print_error "Docker Compose nao encontrado!"
        exit 1
    fi
    print_success "Docker Compose instalado"
    
    # Verificar arquivo docker-compose-prd.yml
    if [ ! -f "$COMPOSE_FILE" ]; then
        print_error "Arquivo $COMPOSE_FILE nao encontrado!"
        exit 1
    fi
    print_success "Arquivo $COMPOSE_FILE encontrado"
    
    # Verificar arquivo .env
    if [ ! -f ".env" ]; then
        print_warning "Arquivo .env nao encontrado!"
        echo ""
        if confirm "Deseja continuar mesmo assim?"; then
            print_info "Continuando sem arquivo .env..."
        else
            print_info "Deploy cancelado."
            exit 0
        fi
    else
        print_success "Arquivo .env encontrado"
    fi
    
    echo ""
    print_success "Todos os prerequisitos verificados!"
    echo ""
}

clean_volumes() {
    print_separator
    echo -e "${BOLD}LIMPEZA DE VOLUMES${NC}"
    print_separator
    echo ""
    
    print_warning "ATENCAO: Esta operacao vai:"
    echo "  - Parar todos os containers"
    echo "  - Remover todos os volumes"
    echo "  - APAGAR TODOS OS DADOS do banco de dados"
    echo "  - APAGAR configuracoes do Keycloak"
    echo "  - APAGAR dados do Grafana e Prometheus"
    echo ""
    
    if confirm "Tem certeza que deseja continuar?"; then
        print_info "Limpando containers e volumes..."
        docker compose -f "$COMPOSE_FILE" down -v 2>&1 | while read line; do
            echo -e "${GRAY}  $line${NC}"
        done
        print_success "Limpeza concluida!"
    else
        print_info "Limpeza cancelada."
        return 1
    fi
    echo ""
}

build_images() {
    print_separator
    echo -e "${BOLD}BUILD DE IMAGENS${NC}"
    print_separator
    echo ""
    
    print_info "Esta operacao pode demorar varios minutos..."
    echo ""
    
    if confirm "Deseja fazer rebuild das imagens?" "y"; then
        print_info "Construindo imagens Docker..."
        docker compose -f "$COMPOSE_FILE" build --no-cache 2>&1 | while read line; do
            echo -e "${GRAY}  $line${NC}"
        done
        print_success "Build concluido!"
    else
        print_info "Build cancelado. Usando imagens existentes."
        return 1
    fi
    echo ""
}

start_services() {
    print_separator
    echo -e "${BOLD}INICIANDO SERVICOS${NC}"
    print_separator
    echo ""
    
    print_info "Subindo containers..."
    docker compose -f "$COMPOSE_FILE" up -d
    print_success "Servicos iniciados!"
    echo ""
}

wait_healthy() {
    print_separator
    echo -e "${BOLD}AGUARDANDO SERVICOS${NC}"
    print_separator
    echo ""
    
    print_info "Aguardando servicos ficarem saudaveis..."
    echo ""
    
    sleep 5
    
    maxAttempts=30
    attempt=0
    
    while [ $attempt -lt $maxAttempts ]; do
        attempt=$((attempt + 1))
        echo -ne "${GRAY}  Verificando... tentativa $attempt/$maxAttempts\r${NC}"
        
        # Verificar containers em execucao
        running=$(docker compose -f "$COMPOSE_FILE" ps --filter "status=running" --format json 2>/dev/null | wc -l)
        
        if [ "$running" -gt 0 ]; then
            echo -e "\n"
            print_success "Servicos estao em execucao!"
            break
        fi
        
        sleep 2
    done
    
    echo ""
}

show_status() {
    print_separator
    echo -e "${BOLD}STATUS DOS SERVICOS${NC}"
    print_separator
    echo ""
    
    docker compose -f "$COMPOSE_FILE" ps
    echo ""
}

show_access_info() {
    print_separator
    echo -e "${BOLD}APLICACAO DISPONIVEL${NC}"
    print_separator
    echo ""
    echo -e "${GREEN}  Aplicacao principal:${NC} http://localhost"
    echo -e "${GREEN}  API Backend:        ${NC} http://localhost/api"
    echo -e "${GREEN}  Grafana Dashboard:  ${NC} http://localhost/grafana"
    echo ""
    print_separator
    echo ""
}

show_logs() {
    print_separator
    echo -e "${BOLD}LOGS EM TEMPO REAL${NC}"
    print_separator
    echo ""
    
    print_info "Exibindo logs (Ctrl+C para sair)..."
    echo ""
    sleep 2
    
    docker compose -f "$COMPOSE_FILE" logs -f
}

stop_services() {
    print_separator
    echo -e "${BOLD}PARAR SERVICOS${NC}"
    print_separator
    echo ""
    
    if confirm "Deseja parar todos os servicos?"; then
        print_info "Parando servicos..."
        docker compose -f "$COMPOSE_FILE" down
        print_success "Servicos parados!"
    else
        print_info "Operacao cancelada."
    fi
    echo ""
    press_enter
}

backup_database() {
    print_separator
    echo -e "${BOLD}BACKUP DO BANCO DE DADOS${NC}"
    print_separator
    echo ""
    
    # Verificar se o container do postgres esta rodando
    if ! docker ps --filter name=ifala-db-prd --filter status=running --format "{{.Names}}" | grep -q "ifala-db-prd"; then
        print_error "Container do PostgreSQL nao esta rodando!"
        echo ""
        press_enter
        return 1
    fi
    
    # Criar diretorio de backups se nao existir
    BACKUP_DIR="./backups"
    mkdir -p "$BACKUP_DIR"
    
    # Nome do arquivo de backup com timestamp
    BACKUP_FILE="$BACKUP_DIR/backup_$(date +%Y%m%d_%H%M%S).sql"
    
    print_info "Criando backup do banco de dados..."
    echo ""
    
    # Executar pg_dump dentro do container
    if docker exec ifala-db-prd pg_dump -U postgres -d ifala > "$BACKUP_FILE" 2>&1; then
        print_success "Backup criado com sucesso!"
        echo ""
        echo -e "${GREEN}  Arquivo:${NC} $BACKUP_FILE"
        echo -e "${GREEN}  Tamanho:${NC} $(du -h "$BACKUP_FILE" | cut -f1)"
        echo ""
    else
        print_error "Falha ao criar backup!"
        echo ""
        rm -f "$BACKUP_FILE" 2>/dev/null
    fi
    
    press_enter
}

update_system() {
    print_separator
    echo -e "${BOLD}ATUALIZAR SISTEMA${NC}"
    print_separator
    echo ""
    
    print_warning "ATENCAO: Esta operacao vai:"
    echo "  1. Fazer git pull origin main (atualizar codigo)"
    echo "  2. Criar backup de seguranca do banco de dados"
    echo "  3. Fazer rebuild das imagens Docker"
    echo "  4. Reiniciar servicos (SEM apagar volumes)"
    echo ""
    print_info "Seus dados serao preservados!"
    echo ""
    
    if ! confirm "Deseja realmente atualizar o sistema?" "n"; then
        print_info "Atualizacao cancelada."
        echo ""
        press_enter
        return 1
    fi
    
    # Passo 1: Git Pull
    print_separator
    echo -e "${BOLD}[1/4] Atualizando codigo do repositorio${NC}"
    print_separator
    echo ""
    
    print_info "Executando git pull origin main..."
    echo ""
    
    if git pull origin main 2>&1 | while read line; do
        echo -e "${GRAY}  $line${NC}"
    done; then
        print_success "Codigo atualizado!"
    else
        print_error "Falha ao atualizar codigo!"
        echo ""
        if ! confirm "Deseja continuar mesmo assim?"; then
            print_info "Atualizacao cancelada."
            echo ""
            press_enter
            return 1
        fi
    fi
    echo ""
    
    # Passo 2: Backup do banco
    print_separator
    echo -e "${BOLD}[2/4] Criando backup de seguranca${NC}"
    print_separator
    echo ""
    
    # Verificar se o container do postgres esta rodando
    if docker ps --filter name=ifala-db-prd --filter status=running --format "{{.Names}}" | grep -q "ifala-db-prd"; then
        BACKUP_DIR="./backups"
        mkdir -p "$BACKUP_DIR"
        BACKUP_FILE="$BACKUP_DIR/backup_update_$(date +%Y%m%d_%H%M%S).sql"
        
        print_info "Criando backup do banco..."
        
        if docker exec ifala-db-prd pg_dump -U postgres -d ifala > "$BACKUP_FILE" 2>&1; then
            print_success "Backup criado: $BACKUP_FILE"
        else
            print_warning "Nao foi possivel criar backup, mas continuando..."
            rm -f "$BACKUP_FILE" 2>/dev/null
        fi
    else
        print_warning "PostgreSQL nao esta rodando. Pulando backup..."
    fi
    echo ""
    
    # Passo 3: Rebuild
    print_separator
    echo -e "${BOLD}[3/4] Reconstruindo imagens${NC}"
    print_separator
    echo ""
    
    print_info "Construindo novas imagens Docker..."
    docker compose -f "$COMPOSE_FILE" build --no-cache 2>&1 | while read line; do
        echo -e "${GRAY}  $line${NC}"
    done
    print_success "Imagens reconstruidas!"
    echo ""
    
    # Passo 4: Restart
    print_separator
    echo -e "${BOLD}[4/4] Reiniciando servicos${NC}"
    print_separator
    echo ""
    
    print_info "Reiniciando containers..."
    docker compose -f "$COMPOSE_FILE" up -d
    print_success "Servicos reiniciados!"
    echo ""
    
    # Aguardar e mostrar status
    wait_healthy
    show_status
    show_access_info
    
    print_success "Sistema atualizado com sucesso!"
    echo ""
    press_enter
}

custom_deploy() {
    print_header
    echo -e "${BOLD}CONFIGURACAO PERSONALIZADA${NC}"
    print_separator
    echo ""
    
    if confirm "Deseja limpar volumes antes do deploy?"; then
        CLEAN=true
    fi
    
    if confirm "Deseja fazer rebuild das imagens?"; then
        BUILD=true
    fi
    
    if confirm "Deseja ver logs apos o deploy?"; then
        LOGS=true
    fi
    
    echo ""
    print_info "Configuracao personalizada definida!"
    press_enter
    
    execute_deploy
}

execute_deploy() {
    print_header
    
    # Verificar prerequisitos
    check_prerequisites
    
    # Limpeza
    if [ "$CLEAN" = true ]; then
        clean_volumes || return 1
    fi
    
    # Build
    if [ "$BUILD" = true ]; then
        build_images || return 1
    fi
    
    # Iniciar servicos
    start_services
    
    # Aguardar
    wait_healthy
    
    # Status
    show_status
    
    # Informacoes de acesso
    show_access_info
    
    print_success "Deploy concluido com sucesso!"
    echo ""
    
    # Logs
    if [ "$LOGS" = true ]; then
        show_logs
    else
        press_enter
    fi
    
    # Resetar flags
    BUILD=false
    CLEAN=false
    LOGS=false
}

# Menu principal
main() {
    while true; do
        show_menu
        read -p "Escolha uma opcao: " opcao
        
        case $opcao in
            1)
                print_header
                print_info "Iniciando deploy rapido..."
                echo ""
                BUILD=false
                CLEAN=false
                LOGS=false
                execute_deploy
                ;;
            2)
                print_header
                print_info "Iniciando deploy com rebuild..."
                echo ""
                BUILD=true
                CLEAN=false
                LOGS=false
                execute_deploy
                ;;
            3)
                print_header
                print_info "Iniciando deploy com limpeza..."
                echo ""
                BUILD=false
                CLEAN=true
                LOGS=false
                execute_deploy
                ;;
            4)
                print_header
                print_info "Iniciando deploy completo..."
                echo ""
                BUILD=true
                CLEAN=true
                LOGS=false
                execute_deploy
                ;;
            5)
                print_header
                update_system
                ;;
            6)
                print_header
                backup_database
                ;;
            7)
                print_header
                show_status
                press_enter
                ;;
            8)
                print_header
                show_logs
                ;;
            9)
                print_header
                stop_services
                ;;
            0)
                print_header
                print_info "Encerrando..."
                echo ""
                exit 0
                ;;
            *)
                print_header
                print_error "Opcao invalida!"
                echo ""
                press_enter
                ;;
        esac
    done
}

# Iniciar aplicacao
main

