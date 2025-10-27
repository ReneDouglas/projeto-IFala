# 🛠️ Scripts de Produção - IFala

Este diretório contém scripts PowerShell para facilitar o gerenciamento do ambiente de produção.

## 📜 Scripts Disponíveis

### 1. `generate-credentials.ps1`
**Gera credenciais seguras criptograficamente**

```powershell
.\generate-credentials.ps1
```

#### Funcionalidades:
- Gera JWT Secret (32 caracteres)
- Gera senha do PostgreSQL (24 caracteres)
- Gera senha do Keycloak (20 caracteres)
- Gera senha do Grafana (20 caracteres)
- Opção de salvar em arquivo
- Menu interativo

#### Uso:
1. Execute o script
2. Escolha a opção desejada
3. Copie as credenciais geradas
4. Cole no arquivo `.env`

#### Exemplo de saída:
```
=== JWT Secret ===
aB3dEf7gH9jKlMnPqRsTuVwXyZ1234567890AbCd

=== PostgreSQL Password ===
xY9zAb3cDeF7gH1jKlMn

=== Keycloak Admin Password ===
pQ2rStUvWxYz4AbC

=== Grafana Admin Password ===
dEfGhIjKlMnOpQrS
```

---

### 2. `validate-prd.ps1`
**Valida a configuração antes do deploy**

```powershell
.\validate-prd.ps1
```

#### O que é validado:
- ✓ Docker e Docker Compose instalados
- ✓ Arquivos essenciais existem
- ✓ Arquivo `.env` existe e está preenchido
- ✓ Variáveis obrigatórias no `.env`
- ✓ JWT_SECRET tem comprimento adequado
- ✓ `.env` está no `.gitignore`
- ✓ Configuração do backend usa variáveis de ambiente
- ✓ Sem senhas hardcoded
- ✓ NGINX configurado corretamente
- ✓ Docker está rodando
- ✓ Espaço em disco suficiente

#### Códigos de saída:
- `0`: Tudo OK ou apenas avisos
- `1`: Erros encontrados (não prosseguir)

#### Exemplo de uso em pipeline:
```powershell
.\validate-prd.ps1
if ($LASTEXITCODE -ne 0) {
    Write-Error "Validação falhou!"
    exit 1
}
```

---

### 3. `deploy-prd.ps1`
**Gerencia o deploy em produção**

```powershell
# Modo interativo (menu)
.\deploy-prd.ps1

# Modo linha de comando
.\deploy-prd.ps1 -Action start
.\deploy-prd.ps1 -Action stop
.\deploy-prd.ps1 -Action restart
.\deploy-prd.ps1 -Action logs
.\deploy-prd.ps1 -Action status
.\deploy-prd.ps1 -Action build
.\deploy-prd.ps1 -Action clean

# Com serviço específico
.\deploy-prd.ps1 -Action restart -Service ifala-backend
.\deploy-prd.ps1 -Action logs -Service ifala-frontend
```

#### Ações disponíveis:

**start** - Inicia todos os serviços
```powershell
.\deploy-prd.ps1 -Action start
```

**stop** - Para todos os serviços
```powershell
.\deploy-prd.ps1 -Action stop
```

**restart** - Reinicia serviços
```powershell
.\deploy-prd.ps1 -Action restart
# ou específico
.\deploy-prd.ps1 -Action restart -Service ifala-backend
```

**logs** - Exibe logs em tempo real
```powershell
.\deploy-prd.ps1 -Action logs
# ou específico
.\deploy-prd.ps1 -Action logs -Service ifala-frontend
```

**status** - Mostra status dos containers
```powershell
.\deploy-prd.ps1 -Action status
```

**build** - Reconstrói as imagens
```powershell
.\deploy-prd.ps1 -Action build
# ou específico
.\deploy-prd.ps1 -Action build -Service ifala-backend
```

**clean** - Remove TUDO (containers, volumes, imagens)
```powershell
.\deploy-prd.ps1 -Action clean
# ⚠️ CUIDADO: Apaga dados do banco!
```

#### Funcionalidades:
- ✓ Validação automática do Docker
- ✓ Criação automática do `.env` se não existir
- ✓ Menu interativo colorido
- ✓ Mensagens de status claras
- ✓ Confirmação para ações destrutivas

---

## 🚀 Fluxo de Trabalho Recomendado

### 1. Primeira Configuração
```powershell
# Gerar credenciais
.\generate-credentials.ps1
# Escolha opção 3 (Gerar TODAS as credenciais)

# Criar e editar .env
Copy-Item .env.example .env
notepad .env
# Cole as credenciais geradas

# Validar
.\validate-prd.ps1

# Deploy
.\deploy-prd.ps1 -Action start
```

### 2. Atualizações de Código
```powershell
# Pull do código
git pull origin main

# Rebuild e restart
.\deploy-prd.ps1 -Action build
.\deploy-prd.ps1 -Action start

# Ver logs
.\deploy-prd.ps1 -Action logs
```

### 3. Troubleshooting
```powershell
# Ver status
.\deploy-prd.ps1 -Action status

# Ver logs de um serviço específico
.\deploy-prd.ps1 -Action logs -Service ifala-backend

# Reiniciar serviço problemático
.\deploy-prd.ps1 -Action restart -Service ifala-backend
```

### 4. Manutenção
```powershell
# Parar tudo para manutenção
.\deploy-prd.ps1 -Action stop

# Fazer backup (manual)
# ...

# Reiniciar
.\deploy-prd.ps1 -Action start
```

---

## 💡 Dicas e Boas Práticas

### Antes de Executar Qualquer Script

1. **Abra PowerShell como Administrador** (recomendado)
2. **Navegue até o diretório raiz do projeto**
   ```powershell
   cd C:\caminho\para\projeto-IFala
   ```
3. **Habilite execução de scripts** (se necessário)
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

### Política de Execução do PowerShell

Se você receber erro de execução:
```
.\script.ps1 : File cannot be loaded because running scripts is disabled on this system.
```

Solução:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Salvando Saída dos Scripts

```powershell
# Salvar logs
.\deploy-prd.ps1 -Action logs | Out-File -FilePath "logs.txt"

# Salvar validação
.\validate-prd.ps1 | Out-File -FilePath "validation-report.txt"
```

### Execução Agendada

Para executar scripts automaticamente (ex: backup):
```powershell
# Criar tarefa agendada no Windows
$action = New-ScheduledTaskAction -Execute 'PowerShell.exe' `
    -Argument '-File "C:\caminho\para\deploy-prd.ps1" -Action status'
$trigger = New-ScheduledTaskTrigger -Daily -At 9am
Register-ScheduledTask -Action $action -Trigger $trigger `
    -TaskName "IFala - Status Diário"
```

---

## 🔒 Segurança dos Scripts

### O que os scripts NÃO fazem:
- ❌ Não enviam dados para servidores externos
- ❌ Não modificam arquivos do sistema
- ❌ Não coletam informações pessoais
- ❌ Não fazem deploy sem confirmação (modo interativo)

### O que os scripts fazem:
- ✅ Leem arquivos locais do projeto
- ✅ Executam comandos Docker localmente
- ✅ Geram senhas aleatórias (sem armazenar)
- ✅ Validam configurações
- ✅ Fornecem feedback visual

### Revisão de Código

Todos os scripts são open source e podem ser revisados:
- `generate-credentials.ps1` - 150 linhas
- `validate-prd.ps1` - 250 linhas
- `deploy-prd.ps1` - 200 linhas

**Recomendação**: Leia os scripts antes de executá-los!

---

## 🐛 Troubleshooting

### Script não executa

**Problema**: `.\script.ps1 : File cannot be loaded`

**Solução**:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Docker não encontrado

**Problema**: `Docker não encontrado!`

**Solução**:
1. Instale o Docker Desktop
2. Inicie o Docker Desktop
3. Execute o script novamente

### .env não criado automaticamente

**Problema**: Script pede para criar `.env` mas não cria

**Solução**:
```powershell
Copy-Item .env.example .env
notepad .env
```

### Permissões negadas

**Problema**: `Access Denied` ao executar script

**Solução**:
1. Abra PowerShell como Administrador
2. Execute o script novamente

---

## 📋 Checklist de Uso

Antes de usar os scripts:
- [ ] PowerShell instalado (vem com Windows)
- [ ] Docker Desktop instalado
- [ ] Docker está rodando
- [ ] Política de execução configurada
- [ ] Navegou até o diretório do projeto

Primeira vez:
- [ ] Execute `.\generate-credentials.ps1`
- [ ] Crie `.env` e preencha credenciais
- [ ] Execute `.\validate-prd.ps1`
- [ ] Execute `.\deploy-prd.ps1 -Action start`

Uso diário:
- [ ] `.\deploy-prd.ps1 -Action status` - Ver status
- [ ] `.\deploy-prd.ps1 -Action logs` - Ver logs
- [ ] `.\deploy-prd.ps1 -Action restart` - Reiniciar

---

## 🆘 Ajuda

Para mais informações:
- **QUICK-START.md** - Guia rápido de 5 minutos
- **PRODUCTION.md** - Documentação completa de produção
- **DEPLOY-CHECKLIST.md** - Checklist detalhado

---

**Scripts mantidos por**: Equipe IFala  
**Última atualização**: 2025  
**Versão**: 1.0.0
