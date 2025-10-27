# Validador de Configuracao de Producao
# Este script valida se tudo esta configurado corretamente antes do deploy

function Write-Success { Write-Host $args -ForegroundColor Green }
function Write-Info { Write-Host $args -ForegroundColor Cyan }
function Write-Warning { Write-Host $args -ForegroundColor Yellow }
function Write-Error { Write-Host $args -ForegroundColor Red }

$ErrorCount = 0
$WarningCount = 0

Write-Info "`n========================================`n"
Write-Info "  Validador de Configuracao de Producao`n"
Write-Info "========================================`n"

# 1. Verificar Docker
Write-Info "[1/10] Verificando Docker..."
try {
    $dockerVersion = docker --version 2>$null
    if ($dockerVersion) {
        Write-Success "OK Docker instalado: $dockerVersion"
    } else {
        Write-Error "ERRO Docker nao encontrado!"
        $ErrorCount++
    }
    
    $composeVersion = docker-compose --version 2>$null
    if ($composeVersion) {
        Write-Success "OK Docker Compose instalado: $composeVersion"
    } else {
        Write-Error "ERRO Docker Compose nao encontrado!"
        $ErrorCount++
    }
} catch {
    Write-Error "ERRO ao verificar Docker"
    $ErrorCount++
}

# 2. Verificar arquivos essenciais
Write-Info "`n[2/10] Verificando arquivos essenciais..."
$requiredFiles = @(
    "docker-compose-prd.yml",
    "apps\ifala-frontend\Dockerfile.prd",
    "apps\ifala-frontend\nginx.conf",
    "apps\ifala-backend\Dockerfile.prd",
    ".env.example"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Success "OK $file existe"
    } else {
        Write-Error "ERRO $file NAO encontrado!"
        $ErrorCount++
    }
}

# 3. Verificar arquivo .env
Write-Info "`n[3/10] Verificando arquivo .env..."
if (Test-Path ".env") {
    Write-Success "OK Arquivo .env existe"
    
    $envContent = Get-Content ".env" -Raw
    $requiredVars = @(
        "POSTGRES_PASSWORD",
        "SPRING_DATASOURCE_PASSWORD",
        "JWT_SECRET",
        "KEYCLOAK_ADMIN_PASSWORD",
        "GF_SECURITY_ADMIN_PASSWORD"
    )
    
    foreach ($var in $requiredVars) {
        if ($envContent -match "$var=\w+") {
            if ($envContent -match "$var=(sua_senha|SuaSenha|senha|password|123)") {
                Write-Warning "AVISO $var parece estar com valor padrao"
                $WarningCount++
            } else {
                Write-Success "  OK $var configurado"
            }
        } else {
            Write-Error "  ERRO $var nao encontrado ou vazio!"
            $ErrorCount++
        }
    }
    
    if ($envContent -match "JWT_SECRET=(.+)") {
        $jwtSecret = $Matches[1].Trim()
        if ($jwtSecret.Length -ge 32) {
            Write-Success "  OK JWT_SECRET tem comprimento adequado"
        } else {
            Write-Warning "  AVISO JWT_SECRET deve ter pelo menos 32 caracteres"
            $WarningCount++
        }
    }
} else {
    Write-Error "ERRO Arquivo .env NAO encontrado!"
    Write-Warning "Execute: Copy-Item .env.example .env"
    $ErrorCount++
}

# 4. Verificar .gitignore
Write-Info "`n[4/10] Verificando .gitignore..."
if (Test-Path ".gitignore") {
    $gitignoreContent = Get-Content ".gitignore" -Raw
    if ($gitignoreContent -match "\.env") {
        Write-Success "OK .env esta no .gitignore"
    } else {
        Write-Error "ERRO .env NAO esta no .gitignore"
        $ErrorCount++
    }
} else {
    Write-Warning "AVISO .gitignore nao encontrado"
    $WarningCount++
}

# 5. Verificar backend
Write-Info "`n[5/10] Verificando application-prod.properties..."
$prodPropsFile = "apps\ifala-backend\src\main\resources\application-prod.properties"
if (Test-Path $prodPropsFile) {
    $propsContent = Get-Content $prodPropsFile -Raw
    
    if ($propsContent -match '\$\{SPRING_DATASOURCE_') {
        Write-Success "OK Usando variaveis de ambiente"
    } else {
        Write-Warning "AVISO Datasource pode nao estar usando variaveis"
        $WarningCount++
    }
} else {
    Write-Error "ERRO application-prod.properties nao encontrado!"
    $ErrorCount++
}

# 6. Verificar Dockerfile frontend
Write-Info "`n[6/10] Verificando Dockerfile.prd do frontend..."
$frontendDockerfile = "apps\ifala-frontend\Dockerfile.prd"
if (Test-Path $frontendDockerfile) {
    $dockerContent = Get-Content $frontendDockerfile -Raw
    
    if ($dockerContent -match "nginx") {
        Write-Success "OK Usando NGINX"
    } else {
        Write-Error "ERRO NGINX nao encontrado!"
        $ErrorCount++
    }
}

# 7. Verificar nginx.conf
Write-Info "`n[7/10] Verificando nginx.conf..."
$nginxConf = "apps\ifala-frontend\nginx.conf"
if (Test-Path $nginxConf) {
    $nginxContent = Get-Content $nginxConf -Raw
    
    if ($nginxContent -match "location /api") {
        Write-Success "OK Proxy reverso configurado"
    } else {
        Write-Warning "AVISO Proxy reverso pode nao estar configurado"
        $WarningCount++
    }
}

# 8. Verificar docker-compose-prd.yml
Write-Info "`n[8/10] Verificando docker-compose-prd.yml..."
if (Test-Path "docker-compose-prd.yml") {
    $composeContent = Get-Content "docker-compose-prd.yml" -Raw
    
    if ($composeContent -match '\$\{') {
        Write-Success "OK Usando variaveis de ambiente"
    } else {
        Write-Error "ERRO Variaveis de ambiente nao detectadas!"
        $ErrorCount++
    }
    
    if ($composeContent -match "SPRING_PROFILES_ACTIVE.*prod") {
        Write-Success "OK Profile prod configurado"
    } else {
        Write-Error "ERRO Profile prod nao configurado!"
        $ErrorCount++
    }
}

# 9. Verificar Docker rodando
Write-Info "`n[9/10] Verificando se Docker esta rodando..."
try {
    docker ps 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "OK Docker esta rodando"
    } else {
        Write-Error "ERRO Docker nao esta rodando!"
        $ErrorCount++
    }
} catch {
    Write-Error "ERRO Docker nao esta rodando!"
    $ErrorCount++
}

# 10. Verificar espaco em disco
Write-Info "`n[10/10] Verificando espaco em disco..."
$drive = Get-PSDrive -Name C
$freeSpaceGB = [math]::Round($drive.Free / 1GB, 2)
if ($freeSpaceGB -gt 10) {
    Write-Success "OK Espaco suficiente: $freeSpaceGB GB"
} else {
    Write-Warning "AVISO Espaco baixo: $freeSpaceGB GB"
    $WarningCount++
}

# Sumario
Write-Info "`n========================================`n"
Write-Info "           SUMARIO DA VALIDACAO`n"
Write-Info "========================================`n"

if ($ErrorCount -eq 0 -and $WarningCount -eq 0) {
    Write-Success "Tudo OK! Pronto para deploy!`n"
    Write-Info "Execute: .\deploy-prd.ps1 start`n"
    exit 0
} elseif ($ErrorCount -eq 0) {
    Write-Warning "$WarningCount aviso(s) encontrado(s)`n"
    Write-Info "Voce pode prosseguir`n"
    exit 0
} else {
    Write-Error "$ErrorCount erro(s) encontrado(s)"
    if ($WarningCount -gt 0) {
        Write-Warning "$WarningCount aviso(s)"
    }
    Write-Info "`nCorrija os erros antes de fazer deploy!`n"
    exit 1
}
