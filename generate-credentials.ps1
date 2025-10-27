# ========================================
# Gerador de Credenciais Seguras - IFala
# ========================================

function Generate-SecurePassword {
    param(
        [int]$Length = 32
    )
    
    $bytes = New-Object byte[] $Length
    $rng = New-Object Security.Cryptography.RNGCryptoServiceProvider
    $rng.GetBytes($bytes)
    $password = [Convert]::ToBase64String($bytes)
    
    return $password
}

function Generate-JWTSecret {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "Gerador de JWT Secret" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
    
    $secret = Generate-SecurePassword -Length 32
    
    Write-Host "JWT Secret gerado:" -ForegroundColor Green
    Write-Host $secret -ForegroundColor Yellow
    Write-Host "`nCopie e cole no arquivo .env na variável JWT_SECRET`n" -ForegroundColor White
}

function Generate-DatabasePassword {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "Gerador de Senha do Banco de Dados" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
    
    $password = Generate-SecurePassword -Length 24
    
    Write-Host "Senha do PostgreSQL gerada:" -ForegroundColor Green
    Write-Host $password -ForegroundColor Yellow
    Write-Host "`nCopie e cole no arquivo .env nas variáveis:" -ForegroundColor White
    Write-Host "  - POSTGRES_PASSWORD" -ForegroundColor Gray
    Write-Host "  - SPRING_DATASOURCE_PASSWORD`n" -ForegroundColor Gray
}

function Generate-AllCredentials {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "Gerador de Todas as Credenciais" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
    
    $jwtSecret = Generate-SecurePassword -Length 32
    $dbPassword = Generate-SecurePassword -Length 24
    $keycloakPassword = Generate-SecurePassword -Length 20
    $grafanaPassword = Generate-SecurePassword -Length 20
    
    Write-Host "=== JWT Secret ===" -ForegroundColor Green
    Write-Host $jwtSecret -ForegroundColor Yellow
    Write-Host ""
    
    Write-Host "=== PostgreSQL Password ===" -ForegroundColor Green
    Write-Host $dbPassword -ForegroundColor Yellow
    Write-Host ""
    
    Write-Host "=== Keycloak Admin Password ===" -ForegroundColor Green
    Write-Host $keycloakPassword -ForegroundColor Yellow
    Write-Host ""
    
    Write-Host "=== Grafana Admin Password ===" -ForegroundColor Green
    Write-Host $grafanaPassword -ForegroundColor Yellow
    Write-Host ""
    
    Write-Host "`nCopie as credenciais acima para o arquivo .env`n" -ForegroundColor White
    
    # Perguntar se deseja salvar em arquivo
    $save = Read-Host "Deseja salvar em um arquivo? (s/n)"
    if ($save -eq 's' -or $save -eq 'S') {
        $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
        $filename = "credentials_$timestamp.txt"
        
        $content = @"
========================================
Credenciais Geradas - IFala
Data: $(Get-Date -Format "dd/MM/yyyy HH:mm:ss")
========================================

JWT_SECRET=$jwtSecret

POSTGRES_PASSWORD=$dbPassword
SPRING_DATASOURCE_PASSWORD=$dbPassword

KEYCLOAK_ADMIN_PASSWORD=$keycloakPassword

GF_SECURITY_ADMIN_PASSWORD=$grafanaPassword

========================================
ATENCAO: Mantenha este arquivo seguro!
NAO compartilhe estas credenciais.
Depois de copiar para o .env, DELETE este arquivo!
========================================
"@
        
        $content | Out-File -FilePath $filename -Encoding UTF8
        Write-Host "Credenciais salvas em: $filename" -ForegroundColor Green
        Write-Host "LEMBRE-SE DE DELETAR ESTE ARQUIVO apos copiar para o .env!" -ForegroundColor Red
    }
}

function Show-Menu {
    Clear-Host
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "  Gerador de Credenciais Seguras" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
    
    Write-Host "1. Gerar JWT Secret"
    Write-Host "2. Gerar Senha do Banco de Dados"
    Write-Host "3. Gerar TODAS as credenciais"
    Write-Host "0. Sair`n"

    $choice = Read-Host "Escolha uma opcao"

    switch ($choice) {
        '1' { 
            Generate-JWTSecret
            Write-Host "Pressione qualquer tecla para continuar..."
            $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
            Show-Menu
        }
        '2' { 
            Generate-DatabasePassword
            Write-Host "Pressione qualquer tecla para continuar..."
            $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
            Show-Menu
        }
        '3' { 
            Generate-AllCredentials
            Write-Host "Pressione qualquer tecla para continuar..."
            $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
            Show-Menu
        }
        '0' { 
            Write-Host "`nSaindo..." -ForegroundColor Cyan
            exit 
        }
        default { 
            Write-Host "`nOpcao inválida!" -ForegroundColor Red
            Start-Sleep -Seconds 1
            Show-Menu
        }
    }
}

# Main
Show-Menu
