# $\Large \textsf{\color{green}{IF}\color{white}{ala}}$

![Status](https://img.shields.io/badge/status-consolidado-brightgreen)
![Licença](https://img.shields.io/badge/license-MIT-blue)
![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)
![React](https://img.shields.io/badge/React-19-blue)
![TypeScript](https://img.shields.io/badge/TypeScript-5.8-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

## 📑 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Garantias de Segurança](#-garantias-de-segurança)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Ambientes](#-ambientes)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Stack Tecnológica](#-stack-tecnológica)
- [Configuração do Ambiente](#-configuração-do-ambiente-de-desenvolvimento)
- [Executando com Docker](#-executando-com-docker)
- [Guia de Desenvolvimento](#-guia-de-desenvolvimento)
- [Documentação Adicional](#-documentação-adicional)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)

---

# 📖 Sobre o Projeto

O **IFala** é um sistema de denúncias anônimas desenvolvido para instituições
federais de ensino, permitindo que alunos relatem ocorrências de forma segura e
protegida. A plataforma garante total anonimato aos denunciantes enquanto
oferece ferramentas eficientes para o acompanhamento e tratamento das denúncias
pelos gestores institucionais.

**Sua voz importa. Sua identidade está protegida.**

# 🔒 Garantias de Segurança

- **Anonimato total garantido** - Nenhum dado pessoal é coletado
- **Comunicação criptografada** - Todas as interações são protegidas
- **Acompanhamento seguro via token** - Sistema de protocolo único para cada
  denúncia
- **Privacidade por design** - Arquitetado para proteger a identidade dos
  usuários

⚠️ **Aviso Importante:** Este canal é destinado a denúncias sérias e legítimas.
O uso inadequado, incluindo trotes ou falsas denúncias, pode constituir crime
conforme a legislação brasileira (Art. 340 do Código Penal - Comunicação falsa
de crime).

## ✨ Funcionalidades Principais

O sistema foi projetado para atender às necessidades dos principais perfis de
usuários:

### Denunciante Anônimo

- ✅ Submissão de denúncias de forma completamente anônima
- ✅ Upload de provas (documentos, imagens, áudios)
- ✅ Acompanhamento do status via token único
- ✅ Comunicação protegida com os gestores
- ✅ Proteção contra bots com Google reCAPTCHA v3

### Gestor Institucional

- ✅ Dashboard com denúncias pendentes de análise
- ✅ Sistema de triagem e categorização de denúncias
- ✅ Fixação de denúncias prioritárias
- ✅ Visualização e download de provas anexadas
- ✅ Sistema de notificações por e-mail
- ✅ Geração de relatórios em PDF
- ✅ Acompanhamento do histórico de denúncias

### Administrador do Sistema

- ✅ Gerenciamento de usuários gestores
- ✅ Configuração de categorias de denúncias
- ✅ Monitoramento de métricas e logs (Grafana/Prometheus)
- ✅ Gestão de notificações do sistema

## 🌐 Ambientes

### Desenvolvimento

Execute localmente com Docker Compose seguindo as instruções da seção
"Executando com Docker"

### Produção

O sistema está preparado para deploy com:

- Configurações otimizadas no [docker-compose-prd.yml](docker-compose-prd.yml)
- Nginx como reverse proxy (configuração em
  [nginx/nginx.conf](nginx/nginx.conf))
- SSL/TLS configurável (documentação em
  [docs/SSL_CONFIGURATION.md](docs/SSL_CONFIGURATION.md))
- Monitoramento completo com stack Prometheus/Grafana/Loki
- Scripts de inicialização do banco em [scripts/postgres/](scripts/postgres/)

Para mais detalhes sobre deploy em produção, consulte a documentação em
[producao/](producao/).

# 📂 Estrutura do Projeto

O projeto segue uma arquitetura **monorepo** com separação clara entre frontend
e backend.

## Estrutura Geral

```plaintext
/
├── 📁 apps/
│   ├── 📁 ifala-backend/       # API Spring Boot
│   └── 📁 ifala-frontend/      # Aplicação React
├── 📁 docs/                    # Documentação adicional
│   └── SSL_CONFIGURATION.md
├── 📁 monitoring/              # Stack de observabilidade
│   ├── grafana/
│   ├── loki/
│   ├── prometheus/
│   └── promtail/
├── 📁 nginx/                   # Configuração do proxy reverso
├── 📁 producao/                # Documentação de produção
├── 📁 scripts/                 # Scripts auxiliares
│   └── postgres/
├── 📄 .env                     # Variáveis de ambiente
├── 📄 docker-compose.yml       # Ambiente de desenvolvimento
├── 📄 docker-compose-prd.yml   # Ambiente de produção
└── 📄 README.md
```

## Backend (Feature-Based Structure)

```plaintext
apps/ifala-backend/
├── 📄 Dockerfile               # Container para desenvolvimento
├── 📄 Dockerfile.prd           # Container otimizado para produção
├── 📄 pom.xml                  # Dependências Maven
└── 📁 src/
    ├── 📁 main/
    │   ├── 📁 java/br/edu/ifpi/ifala/
    │   │   ├── 📁 acompanhamento/        # Feature: Acompanhamento de denúncias
    │   │   ├── 📁 autenticacao/          # Feature: Login/Logout
    │   │   ├── 📁 denuncia/              # Feature: Gestão de denúncias
    │   │   ├── 📁 denunciaFixada/        # Feature: Denúncias fixadas
    │   │   ├── 📁 notificacao/           # Feature: Sistema de notificações
    │   │   ├── 📁 prova/                 # Feature: Upload e gestão de provas
    │   │   ├── 📁 config/                # Configurações globais (CORS, etc)
    │   │   ├── 📁 security/              # JWT, filtros, autenticação
    │   │   ├── 📁 shared/                # Exceções e utilitários
    │   │   └── 📁 utils/
    │   └── 📁 resources/
    │       ├── 📄 application.properties          # Configuração base
    │       ├── 📄 application-docker.properties   # Perfil Docker
    │       ├── 📄 application-prod.properties     # Perfil produção
    │       ├── 📄 application-staging.properties  # Perfil staging
    │       └── 📄 logback-spring.xml              # Configuração de logs
    └── 📁 test/                # Testes automatizados
```

### Padrão de Organização por Feature

Cada módulo funcional contém:

- `*Controller.java` - Endpoints REST
- `*Service.java` - Interface de serviço
- `*ServiceImpl.java` - Implementação da lógica de negócio
- `*Repository.java` - Acesso ao banco de dados (JPA)
- `*Entity.java` - Entidades JPA
- `dto/` - Data Transfer Objects

## Frontend (Component-Based Structure)

```plaintext
apps/ifala-frontend/
├── 📄 Dockerfile               # Container para desenvolvimento
├── 📄 Dockerfile.prd           # Container otimizado para produção
├── 📄 package.json
├── 📄 vite.config.ts
├── 📄 tsconfig.json
├── 📁 public/                  # Assets estáticos
└── 📁 src/
    ├── 📁 assets/              # Imagens, fontes, ícones
    ├── 📁 components/          # Componentes reutilizáveis
    │   ├── FileUpload.tsx
    │   ├── Header.tsx
    │   ├── Sidebar.tsx
    │   ├── ProtectedRoute.tsx
    │   └── ProvasModal.tsx
    ├── 📁 contexts/            # Context API do React
    │   ├── AuthContext.tsx
    │   └── AuthContextTypes.ts
    ├── 📁 hooks/               # Custom hooks
    │   └── useAuth.ts
    ├── 📁 pages/               # Páginas da aplicação
    │   ├── Acompanhamento/
    │   ├── Denuncia/
    │   └── DenunciaList/
    ├── 📁 services/            # Comunicação com API
    ├── 📁 styles/              # Estilos globais
    ├── 📁 types/               # TypeScript types/interfaces
    ├── 📁 utils/               # Funções auxiliares
    ├── 📄 App.tsx
    └── 📄 main.tsx
```

# 🚀 Stack Tecnológica

O projeto utiliza tecnologias modernas para garantir desempenho, escalabilidade
e segurança.

## Backend

- **Java 25** com **Spring Boot 3.5.5**
- **Spring Security** com autenticação JWT
- **Spring Data JPA** para persistência de dados
- **PostgreSQL 16** como banco de dados
- **Spring Mail** para envio de notificações
- **Spring Actuator** com métricas Prometheus
- **MapStruct 1.6.3** para mapeamento de DTOs
- **Google reCAPTCHA v3** para proteção contra bots

## Frontend

- **React 19** com **TypeScript 5.8**
- **Vite 7** como build tool
- **Material-UI (MUI) 7** para componentes
- **React Router v7** para navegação
- **Axios** para requisições HTTP
- **React PDF Renderer** para geração de relatórios

## Infraestrutura e Observabilidade

- **Docker** e **Docker Compose** para containerização
- **PostgreSQL 16** com otimizações de performance
- **Prometheus** para coleta de métricas
- **Grafana** para visualização de métricas e dashboards
- **Loki** para agregação de logs
- **Promtail** para coleta de logs dos containers
- **Nginx** como reverse proxy em produção

## Requisitos de Desenvolvimento

### Node.js e NPM

- **Versão:** Node.js 22.x
- **Gerenciador recomendado:** NVM (Node Version Manager)

#### Instalação (Linux/macOS)

```bash
# Instala o NVM
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash

# Recarregue o terminal e instale o Node.js
nvm install 22
nvm use 22
```

#### Instalação (Windows)

Baixe e instale o
[nvm-windows](https://github.com/coreybutler/nvm-windows/releases). Depois:

```bash
nvm install 22
nvm use 22
```

#### Verificar instalação

```bash
node -v  # Deve retornar v22.x.x
npm -v   # Deve retornar 10.x.x
```

### Java (JDK)

- **Versão:** JDK 25
- **Distribuições:** OpenJDK, Eclipse Temurin, Amazon Corretto ou similar

#### Instalação (Linux/macOS/Windows com WSL)

Recomendamos usar o [SDKMAN!](https://sdkman.io/usage):

```bash
# Instala o SDKMAN!
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Instala o Java 25
sdk install java 25-amzn
```

#### Instalação Manual (Windows)

Baixe o [Eclipse Temurin](https://adoptium.net/pt-BR) e configure as variáveis
de ambiente.

#### Verificação

```bash
java -version  # Deve exibir a versão 25.x.x
```

### PostgreSQL

- **Versão:** 16.0
- **Nota:** Não é necessário instalar localmente, pois é executado via Docker.

#### Conectar ao container no DBeaver

1. Inicie o container: `docker compose up postgres`
2. No DBeaver, configure:
   - **Host:** localhost
   - **Porta:** 5432
   - **Database:** ifala
   - **Usuário:** postgres
   - **Senha:** postgres

### Docker

Necessário para executar a aplicação completa com todos os serviços.

#### Instalação

Baixe o [Docker Desktop](https://docs.docker.com/get-started/get-docker) para
seu sistema operacional.

#### Verificação

```bash
docker --version
docker compose version
```

# 📥 Configuração do Ambiente de Desenvolvimento

## 1. Clonar o Repositório

```bash
git clone git@github.com:ReneDouglas/projeto-IFala.git
cd projeto-IFala
```

## 2. Configurar Variáveis de Ambiente

O arquivo `.env` contém todas as variáveis necessárias. Certifique-se de que ele
existe na raiz do projeto.

**Principais variáveis:**

- `JWT_SECRET` - Chave secreta para geração de tokens JWT
- `RECAPTCHA_SECRET_KEY` - Chave secreta do Google reCAPTCHA
- `POSTGRES_*` - Credenciais do banco de dados
- `SPRING_MAIL_*` - Configurações do servidor de e-mail
- `GRAFANA_*` - Credenciais das ferramentas

## 3. Instalar Dependências do Frontend

```bash
cd apps/ifala-frontend
npm ci  # Instala exatamente as versões do package-lock.json
```

## 4. Dependências do Backend

As dependências do Maven são gerenciadas automaticamente pelo Docker e pelo
próprio Maven ao executar o build.

# 🐳 Executando com Docker

O projeto utiliza Docker Compose para orquestrar todos os serviços.

## Ambiente de Desenvolvimento

### Iniciar todos os serviços

```bash
docker compose up --build -d
```

### Parar os serviços

```bash
docker compose down
```

### Ver logs em tempo real

```bash
# Todos os serviços
docker compose logs -f

# Serviço específico
docker compose logs -f ifala-backend
docker compose logs -f ifala-frontend
```

### Rebuild após mudanças de dependências

```bash
docker compose down
docker compose up --build -d
```

## Ambiente de Produção

Para executar com as configurações otimizadas para produção:

```bash
docker compose -f docker-compose-prd.yml up --build -d
```

## Acessar os Serviços

| Serviço        | URL                   | Descrição                      |
| -------------- | --------------------- | ------------------------------ |
| **Frontend**   | http://localhost:5173 | Interface React com Hot Reload |
| **Backend**    | http://localhost:8080 | API Spring Boot                |
| **Grafana**    | http://localhost:3000 | Dashboard de métricas e logs   |
| **Prometheus** | http://localhost:9091 | Servidor de métricas           |
| **PostgreSQL** | localhost:5432        | Banco de dados                 |

### Credenciais Padrão

**Grafana:**

- Usuário: `admin`
- Senha: Definida em `GF_SECURITY_ADMIN_PASSWORD` no `.env`

**PostgreSQL:**

- Usuário: `postgres`
- Senha: `postgres`
- Database: `ifala`

# 💻 Guia de Desenvolvimento

## Workflow com Git

### Branches Principais

- `main` - Código em produção (protegida)
- `development` - Branch de desenvolvimento (protegida)

### Criando uma Nova Feature

Sempre crie branches a partir da `development`:

```bash
# Atualiza a branch development
git switch development
git pull origin development

# Cria e muda para a nova branch
git switch -c feature/task-{id}-{descricao-curta}

# Exemplo:
git switch -c feature/task-42-adiciona-validacao-email
```

**Padrão de nomenclatura:**

- `feature/task-{id}-{descrição}` - Para novas funcionalidades
- `fix/task-{id}-{descrição}` - Para correções de bugs
- `docs/task-{id}-{descrição}` - Para atualizações de documentação

## Conventional Commits

Usamos o padrão **Conventional Commits** para mensagens claras e consistentes:

### Formato

```
<tipo>: <mensagem curta e descritiva>
```

### Tipos Comuns

| Tipo       | Descrição                          | Exemplo                                                |
| ---------- | ---------------------------------- | ------------------------------------------------------ |
| `feat`     | Nova funcionalidade                | `feat: adiciona validação de email na denúncia`        |
| `fix`      | Correção de bug                    | `fix: corrige erro no upload de arquivo`               |
| `docs`     | Documentação                       | `docs: atualiza README com instruções de deploy`       |
| `style`    | Formatação (sem mudança de lógica) | `style: formata código com Prettier`                   |
| `refactor` | Refatoração                        | `refactor: simplifica lógica de autenticação`          |
| `test`     | Testes                             | `test: adiciona testes unitários para DenunciaService` |
| `chore`    | Manutenção                         | `chore: atualiza dependências do Maven`                |
| `perf`     | Melhoria de performance            | `perf: otimiza query de listagem de denúncias`         |

### Exemplos de Bons Commits

```bash
git commit -m "feat: implementa filtro por categoria na lista de denúncias"
git commit -m "fix: corrige validação do token JWT expirado"
git commit -m "docs: adiciona documentação da API de denúncias"
git commit -m "refactor: move lógica de email para serviço dedicado"
```

## Enviando Código para o Repositório

```bash
# Adiciona os arquivos modificados
git add .

# Faz o commit seguindo Conventional Commits
git commit -m "feat: adiciona componente de notificações"

# Envia para o repositório remoto
git push origin feature/task-42-adiciona-validacao-email
```

## Pull Requests

### Criando um PR

1. Após o `git push`, acesse o repositório no GitHub
2. Clique em **"Compare & pull request"**
3. Configure o PR:
   - **Base:** `development`
   - **Compare:** Sua branch (`feature/...`)
4. Preencha:
   - **Título:** Descrição clara (ex: "Feature: Implementa notificações por
     e-mail")
   - **Descrição:** O que foi feito, por que foi feito, referência à task (#42)
5. Clique em **"Create pull request"**

### Revisão de Código

- Aguarde revisão de pelo menos um membro da equipe
- Responda aos comentários
- Faça alterações solicitadas em novos commits na mesma branch
- O PR será mesclado após aprovação

## Debug do Backend no VS Code

### Configuração

1. Certifique-se de que o arquivo `.vscode/launch.json` existe na raiz do
   projeto
2. Se não existir, crie com o seguinte conteúdo:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Attach to Docker Container",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005,
      "projectName": "ifala"
    }
  ]
}
```

### Como Debugar

1. Inicie o backend: `docker compose up ifala-backend`
2. No VS Code, vá para **Run and Debug** (Ctrl+Shift+D)
3. Selecione **"Attach to Docker Container"**
4. Clique no botão verde de play
5. Adicione breakpoints no código Java
6. Faça requisições à API e o debug irá pausar nos breakpoints

## Observabilidade e Monitoramento

### Grafana Dashboards

Acesse http://localhost:3000 para visualizar:

- **Métricas do Spring Boot** - CPU, memória, threads, requests HTTP
- **Métricas do PostgreSQL** - Conexões, queries, performance
- **Logs agregados** - Via Loki/Promtail

### Prometheus

Acesse http://localhost:9091 para queries diretas de métricas.

### Spring Actuator

Endpoints de métricas disponíveis em:

- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/metrics
- http://localhost:8080/actuator/prometheus

## Boas Práticas

### Backend (Spring Boot)

- Use DTOs para comunicação entre camadas
- Implemente validações com Bean Validation (`@Valid`, `@NotNull`, etc)
- Documente endpoints complexos
- Escreva testes unitários para serviços
- Use transações (`@Transactional`) adequadamente

### Frontend (React)

- Componentes pequenos e reutilizáveis
- Use TypeScript para type safety
- Implemente loading states e error handling
- Siga os padrões do ESLint configurado
- Use hooks customizados para lógica compartilhada

### Geral

- Não commite arquivos `.env` ou credenciais
- Mantenha dependências atualizadas
- Escreva código limpo e legível
- Documente decisões importantes
- Teste localmente antes de fazer push

---

# 📚 Documentação Adicional

- [Configuração SSL/TLS](docs/SSL_CONFIGURATION.md) - Instruções para HTTPS em
  produção
- [Nginx](producao/NGINX.md) - Configuração do proxy reverso
- [TUI](producao/TUI.md) - Interface de terminal para gestão

# 🤝 Contribuindo

1. Fork o projeto
2. Crie sua branch (`git switch -c feature/nova-funcionalidade`)
3. Commit suas mudanças seguindo Conventional Commits
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

# 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais
detalhes.

# 👥 Equipe

Desenvolvido por estudantes e professores do **Instituto Federal do Piauí -
Campus Corrente**.

**Contato:** ifala.cacor@ifpi.edu.br

---

**IFala** - _Sua voz importa. Sua identidade está protegida._
