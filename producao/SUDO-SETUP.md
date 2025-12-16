# Configuração de Sudo para Deploy em Produção

## Visão Geral

O script `tui.sh` foi adaptado para funcionar em servidores Linux de produção onde os comandos Docker requerem privilégios de superusuário (sudo).

## Funcionalidades

### 1. Senha Sudo via Arquivo .env

A senha sudo pode ser configurada no arquivo `.env` para automação completa:

```bash
# ----------------------------------------
# Servidor Linux - Sudo Password
# ----------------------------------------
SUDO_PASSWORD=sua_senha_sudo_aqui
```

### 2. Execução Automática com Sudo

Todos os comandos Docker são executados automaticamente com `sudo`:
- `docker compose`
- `docker volume`
- `docker exec`
- `docker ps`

### 3. Fallback Interativo

Se a senha não estiver configurada no `.env`:
- O sistema solicitará a senha sudo **uma única vez** no início
- A sessão sudo permanecerá ativa durante a execução do script
- Não será necessário digitar a senha múltiplas vezes

## Configuração

### Passo 1: Editar o arquivo .env

Adicione sua senha sudo ao arquivo `.env`:

```bash
nano .env
```

Adicione a linha:
```bash
SUDO_PASSWORD=SuaSenhaSuperSegura123
```

**⚠️ IMPORTANTE DE SEGURANÇA:**
- O arquivo `.env` já está no `.gitignore` (não será versionado)
- Certifique-se de que o arquivo tem permissões restritas:
  ```bash
  chmod 600 .env
  ```

### Passo 2: Testar o Script

Execute o script normalmente:

```bash
./tui.sh
```

Se a senha estiver correta no `.env`, nenhuma solicitação de senha aparecerá.

## Como Funciona

### Função run_docker()

```bash
run_docker() {
    if [ -n "$SUDO_PASSWORD" ]; then
        # Usar senha do .env
        echo "$SUDO_PASSWORD" | sudo -S "$@" 2>/dev/null || sudo "$@"
    else
        # Pedir senha interativamente na primeira vez
        sudo "$@"
    fi
}
```

**Explicação:**
1. Verifica se `$SUDO_PASSWORD` está definida
2. Se sim: Usa `echo "$SUDO_PASSWORD" | sudo -S` para passar a senha
3. Se falhar ou não houver senha: Usa `sudo` interativo normal
4. A flag `-S` faz o sudo ler a senha do stdin

### Carregamento da Senha

```bash
# Carregar senha sudo do .env (se existir)
if [ -f ".env" ]; then
    export $(grep -v '^#' .env | grep SUDO_PASSWORD | xargs)
fi
```

**Explicação:**
1. Verifica se `.env` existe
2. Extrai apenas a linha `SUDO_PASSWORD`
3. Exporta como variável de ambiente
4. Ignora comentários (`grep -v '^#'`)

## Exemplos de Uso

### Com Senha Configurada (.env)

```bash
$ ./tui.sh

========================================
     IFala - Deploy em Producao
========================================

[INFO] Verificando prerequisitos...

[OK] Docker instalado
[OK] Docker Compose instalado
[OK] Arquivo docker-compose-prd.yml encontrado
[OK] Arquivo .env encontrado
[INFO] Verificando volumes externos...
[OK] Volume 'pgdata_prd' encontrado
[OK] Volume 'provas_data_prd' encontrado

[OK] Todos os prerequisitos verificados!
```

**Sem solicitação de senha!** ✅

### Sem Senha Configurada (.env)

```bash
$ ./tui.sh

========================================
     IFala - Deploy em Producao
========================================

[INFO] Verificando prerequisitos...

[sudo] senha para usuario: ████████

[OK] Docker instalado
[OK] Docker Compose instalado
...
```

**Senha solicitada apenas uma vez** no início.

## Segurança

### Boas Práticas

1. **Permissões do arquivo .env:**
   ```bash
   chmod 600 .env
   chown usuario:usuario .env
   ```

2. **Não versionar o .env:**
   - Já está no `.gitignore`
   - Nunca faça commit do arquivo `.env` com senha

3. **Usar senha forte:**
   - Mínimo 12 caracteres
   - Letras maiúsculas, minúsculas, números e símbolos

4. **Alternativa mais segura (sem senha no .env):**
   - Configurar sudo sem senha para comandos Docker específicos
   - Editar `/etc/sudoers.d/docker`:
     ```bash
     usuario ALL=(ALL) NOPASSWD: /usr/bin/docker, /usr/bin/docker-compose, /usr/local/bin/docker-compose
     ```

### Configuração Sudo Sem Senha (Recomendado para Produção)

Para maior segurança, configure o sudo sem necessidade de senha:

```bash
# Criar arquivo de configuração
sudo visudo -f /etc/sudoers.d/docker-nopasswd

# Adicionar linha (substitua 'usuario' pelo seu usuário):
usuario ALL=(ALL) NOPASSWD: /usr/bin/docker, /usr/bin/docker-compose, /usr/local/bin/docker-compose
```

**Vantagens:**
- ✅ Não precisa armazenar senha no `.env`
- ✅ Mais seguro
- ✅ Específico para comandos Docker apenas
- ✅ Sem solicitação de senha

**Com essa configuração:**
- Remova a linha `SUDO_PASSWORD` do `.env`
- O script funcionará automaticamente
- Mais seguro para ambientes de produção

## Comandos Docker Afetados

Todos os comandos Docker no script agora usam `run_docker`:

### Verificação de Volumes
```bash
run_docker docker volume inspect pgdata_prd
run_docker docker volume create pgdata_prd
```

### Docker Compose
```bash
run_docker docker compose -f docker-compose-prd.yml up -d
run_docker docker compose -f docker-compose-prd.yml down
run_docker docker compose -f docker-compose-prd.yml build
run_docker docker compose -f docker-compose-prd.yml ps
run_docker docker compose -f docker-compose-prd.yml logs -f
```

### Containers
```bash
run_docker docker ps --filter name=ifala-db-prd
run_docker docker exec ifala-db-prd pg_dump -U postgres -d ifala
```

## Troubleshooting

### Erro: "senha incorreta"

**Problema:** Senha no `.env` está errada

**Solução:**
```bash
nano .env
# Corrigir SUDO_PASSWORD=senha_correta
```

### Erro: "sudo: command not found"

**Problema:** Usuário não tem sudo instalado

**Solução:**
```bash
# Logar como root
su -

# Instalar sudo
apt-get install sudo    # Debian/Ubuntu
yum install sudo        # CentOS/RHEL

# Adicionar usuário ao grupo sudo
usermod -aG sudo usuario    # Debian/Ubuntu
usermod -aG wheel usuario   # CentOS/RHEL
```

### Senha solicitada mesmo com .env configurado

**Problema:** Variável de ambiente não foi carregada

**Solução:**
```bash
# Verificar se a senha está no .env
grep SUDO_PASSWORD .env

# Verificar se há espaços extras ou caracteres especiais
cat -A .env | grep SUDO_PASSWORD

# Formato correto:
SUDO_PASSWORD=senha_sem_espacos
```

### Script para em "Verificando prerequisitos"

**Problema:** Sudo timeout ou senha bloqueada

**Solução:**
```bash
# Testar sudo manualmente
sudo docker ps

# Verificar log do sistema
sudo tail -f /var/log/auth.log    # Debian/Ubuntu
sudo tail -f /var/log/secure      # CentOS/RHEL
```

## Testes

### Testar Função run_docker

Crie script de teste:

```bash
#!/bin/bash

# Carregar .env
if [ -f ".env" ]; then
    export $(grep -v '^#' .env | grep SUDO_PASSWORD | xargs)
fi

# Função run_docker
run_docker() {
    if [ -n "$SUDO_PASSWORD" ]; then
        echo "$SUDO_PASSWORD" | sudo -S "$@" 2>/dev/null || sudo "$@"
    else
        sudo "$@"
    fi
}

# Testar
echo "Testando docker ps:"
run_docker docker ps

echo ""
echo "Testando docker volume ls:"
run_docker docker volume ls

echo ""
echo "Teste concluído!"
```

Execute:
```bash
chmod +x test-sudo.sh
./test-sudo.sh
```

## Comparação: Antes vs Depois

### Antes (sem sudo)
```bash
docker compose -f docker-compose-prd.yml up -d
# Erro: permission denied
```

### Depois (com sudo automático)
```bash
run_docker docker compose -f docker-compose-prd.yml up -d
# ✅ Funciona com senha do .env ou solicitação interativa
```

## Logs de Auditoria

Os comandos sudo são registrados automaticamente:

```bash
# Ver logs de sudo
sudo grep sudo /var/log/auth.log    # Debian/Ubuntu
sudo grep sudo /var/log/secure      # CentOS/RHEL

# Exemplo de log:
# Dec 16 10:30:15 server sudo: usuario : TTY=pts/0 ; PWD=/home/usuario/projeto-IFala ; USER=root ; COMMAND=/usr/bin/docker compose -f docker-compose-prd.yml up -d
```

## Referências

- [Sudo Manual](https://www.sudo.ws/docs/man/sudo.man/)
- [Docker Post-installation steps for Linux](https://docs.docker.com/engine/install/linux-postinstall/)
- [Sudoers Manual](https://www.sudo.ws/docs/man/sudoers.man/)
