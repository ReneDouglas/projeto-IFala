# Multi-stage build para otimizar o tamanho da imagem final

# Estágio 1: Build da aplicação
FROM node:22-alpine AS builder

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de dependências
COPY package*.json ./

# Instalar dependências (incluindo devDependencies para o build)
RUN npm ci --only=production=false

# Copiar código fonte
COPY . .

# Build da aplicação para produção
RUN npm run build

# Estágio 2: Servidor de produção
FROM node:22-alpine AS production

# Instalar serve globalmente para servir arquivos estáticos
RUN npm install -g serve

# Definir diretório de trabalho
WORKDIR /app

# Copiar apenas os arquivos buildados do estágio anterior
COPY --from=builder /app/dist ./dist

# Expor porta (Railway usa a variável PORT)
EXPOSE $PORT

# Comando para iniciar o servidor usando a porta do Railway
CMD ["sh", "-c", "serve -s dist -l ${PORT:-3000}"]