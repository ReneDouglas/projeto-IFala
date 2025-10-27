# ========================================
# Script de Deploy em Producao - IFala
# ========================================
# Este script facilita o deploy da aplicacao em producao

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('start', 'stop', 'restart', 'logs', 'status', 'build', 'clean')]
    [string]$Action = 'start',
    
    [Parameter(Mandatory=$false)]
    [string]$Service = ''
)

$ComposeFile = "docker-compose-prd.yml"
$EnvFile = ".env"

# Cores para output
function Write-Success { Write-Host $args -ForegroundColor Green }
function Write-Info { Write-Host $args -ForegroundColor Cyan }
function Write-Warning { Write-Host $args -ForegroundColor Yellow }
function Write-Error { Write-Host $args -ForegroundColor Red }

# Verificar se docker esta instalado
function Test-Docker {
    try {
        docker --version | Out-Null
        docker-compose --version | Out-Null
        return $true
    } catch {
        Write-Error "Docker ou Docker Compose nao encontrado!"
        Write-Error "Instale o Docker Desktop: https://www.docker.com/products/docker-desktop"
        return $false
    }
}

# Verificar se arquivo .env existe
function Test-EnvFile {
    if (-not (Test-Path $EnvFile)) {
        Write-Warning "Arquivo .env nao encontrado!"
        Write-Info "Criando a partir do .env.example..."
        
        if (Test-Path ".env.example") {
            Copy-Item ".env.example" $EnvFile
            Write-Success "Arquivo .env criado!"
            Write-Warning "IMPORTANTE: Edite o arquivo .env com suas credenciais antes de continuar!"
            Write-Info "Pressione qualquer tecla para abrir o arquivo .env..."
            $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
            notepad $EnvFile
            return $false
        } else {
            Write-Error "Arquivo .env.example nao encontrado!"
            return $false
        }
    }
    return $true
}

# Executar ações
function Invoke-Action {
    param([string]$ActionName, [string]$ServiceName)
    
    Write-Info "`n========================================`n"
    
    switch ($ActionName) {
        'start' {
            Write-Info "Iniciando servicos em producao..."
            if ($ServiceName) {
                docker-compose -f $ComposeFile up -d $ServiceName
            } else {
                docker-compose -f $ComposeFile up -d
            }
            Write-Success "`nServicos iniciados com sucesso!"
            Write-Info "`nAcesse:"
            Write-Info "  Frontend: http://localhost:8080"
            Write-Info "  Grafana:  http://localhost:8081"
            Write-Info "  Keycloak: http://localhost:9090"
        }
        
        'stop' {
            Write-Info "Parando servicos..."
            if ($ServiceName) {
                docker-compose -f $ComposeFile stop $ServiceName
            } else {
                docker-compose -f $ComposeFile down
            }
            Write-Success "Servicos parados!"
        }
        
        'restart' {
            Write-Info "Reiniciando servicos..."
            if ($ServiceName) {
                docker-compose -f $ComposeFile restart $ServiceName
            } else {
                docker-compose -f $ComposeFile restart
            }
            Write-Success "Servicos reiniciados!"
        }
        
        'logs' {
            Write-Info "Exibindo logs (Ctrl+C para sair)..."
            if ($ServiceName) {
                docker-compose -f $ComposeFile logs -f $ServiceName
            } else {
                docker-compose -f $ComposeFile logs -f
            }
        }
        
        'status' {
            Write-Info "Status dos servicos:"
            docker-compose -f $ComposeFile ps
        }
        
        'build' {
            Write-Info "Buildando imagens..."
            if ($ServiceName) {
                docker-compose -f $ComposeFile build $ServiceName
            } else {
                docker-compose -f $ComposeFile build
            }
            Write-Success "Build concluido!"
        }
        
        'clean' {
            Write-Warning "ATENCAO: Esta acao ira remover TODOS os containers, volumes e dados!"
            $confirm = Read-Host "Tem certeza? (sim/nao)"
            if ($confirm -eq 'sim') {
                Write-Info "Removendo containers, volumes e imagens..."
                docker-compose -f $ComposeFile down -v --rmi all
                Write-Success "Limpeza concluida!"
            } else {
                Write-Info "Operacao cancelada."
            }
        }
        
        default {
            Write-Error "Acao desconhecida: $ActionName"
        }
    }
}

# Menu interativo
function Show-Menu {
    Clear-Host
    Write-Info "========================================`n"
    Write-Info "  IFala - Gerenciador de Producao`n"
    Write-Info "========================================`n"
    Write-Host "1. Iniciar todos os servicos"
    Write-Host "2. Parar todos os servicos"
    Write-Host "3. Reiniciar todos os servicos"
    Write-Host "4. Ver logs"
    Write-Host "5. Ver status"
    Write-Host "6. Build de imagens"
    Write-Host "7. Limpeza completa (CUIDADO!)"
    Write-Host "0. Sair`n"

    $choice = Read-Host "Escolha uma opcao"

    switch ($choice) {
        '1' { Invoke-Action 'start' '' }
        '2' { Invoke-Action 'stop' '' }
        '3' { Invoke-Action 'restart' '' }
        '4' { Invoke-Action 'logs' '' }
        '5' { Invoke-Action 'status' '' }
        '6' { Invoke-Action 'build' '' }
        '7' { Invoke-Action 'clean' '' }
        '0' { Write-Info "Saindo..."; exit }
        default { Write-Warning "Opcao invalida!" }
    }
    
    Write-Host "`nPressione qualquer tecla para continuar..."
    $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
    Show-Menu
}

# Main
Write-Info "IFala - Deploy em Producao`n"

if (-not (Test-Docker)) {
    exit 1
}

if (-not (Test-EnvFile)) {
    exit 1
}

# Se foi passado argumento via linha de comando, executa diretamente
if ($PSBoundParameters.ContainsKey('Action')) {
    Invoke-Action $Action $Service
} else {
    # Caso contrário, mostra menu interativo
    Show-Menu
}
