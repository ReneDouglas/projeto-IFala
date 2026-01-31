# Configurações do VS Code - Projeto IFala

Esta pasta contém configurações pré-definidas para facilitar o desenvolvimento
do projeto IFala no Visual Studio Code.

## 📁 Arquivos

### `extensions.json`

Lista de extensões recomendadas para o projeto. Ao abrir o workspace, o VS Code
sugerirá automaticamente a instalação dessas extensões.

**Como usar:**

- Abra o projeto no VS Code
- Clique em "Install All" na notificação que aparecer
- Ou vá em Extensions (Ctrl+Shift+X) → "Show Recommended Extensions"

### `settings.json`

Configurações específicas do workspace que sobrescrevem as configurações globais
do VS Code.

**Configurações incluídas:**

- ✅ Formatação automática com Prettier ao salvar
- ✅ Checkstyle para Java com Google Style Guide
- ✅ Auto-save ao mudar de arquivo
- ✅ Formatadores específicos por linguagem (Java, TypeScript, JSON, CSS, etc)
- ✅ Compilação automática do Java
- ✅ Análise de null do Java

### `launch.json`

Configurações de debug/launch para o projeto.

**Configurações disponíveis:**

1. **Spring Boot-IfalaApplication**
   - Executa a aplicação Spring Boot localmente
   - Usa o arquivo `.env` para variáveis de ambiente
   - Ideal para desenvolvimento sem Docker

2. **Attach to Docker Container**
   - Conecta ao container Docker do backend
   - Porta: 5005 (configurada no docker-compose.yml)
   - Ideal para debug com Docker

**Como usar:**

1. Inicie a aplicação (localmente ou via Docker)
2. Vá para "Run and Debug" (Ctrl+Shift+D)
3. Selecione a configuração desejada
4. Clique no botão verde de play
5. Adicione breakpoints no código e faça requisições

## 🚀 Primeiros Passos

### 1. Instalar Extensões Recomendadas

```bash
# Abra a paleta de comandos (Ctrl+Shift+P)
# Digite: "Extensions: Show Recommended Extensions"
# Clique em "Install" nas extensões recomendadas
```

### 2. Verificar Configurações

As configurações serão aplicadas automaticamente ao abrir o workspace.

### 3. Testar Debug

- Inicie o backend: `docker compose up ifala-backend`
- No VS Code, vá para Run and Debug
- Selecione "Attach to Docker Container"
- Adicione um breakpoint e teste

## 🔧 Personalizações

Se você precisar personalizar alguma configuração:

1. **Não modifique** diretamente os arquivos desta pasta
2. Use as configurações do usuário (User Settings)
3. Ou crie um arquivo `settings.local.json` (não versionado)

## 📚 Documentação Adicional

- [VS Code Java Documentation](https://code.visualstudio.com/docs/java/java-tutorial)
- [VS Code Debugging](https://code.visualstudio.com/docs/editor/debugging)
- [Prettier Configuration](https://prettier.io/docs/en/configuration.html)
- [ESLint Configuration](https://eslint.org/docs/user-guide/configuring/)
