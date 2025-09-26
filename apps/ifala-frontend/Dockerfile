# Estágio 1: Definir a imagem base
FROM node:22-slim

# Definir o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar os arquivos de gerenciamento de pacotes
COPY package.json ./
COPY package-lock.json ./

# Instalar as dependências
RUN npm install

# Expor a porta que o Vite utiliza
EXPOSE 5173

# Comando para iniciar o servidor de desenvolvimento do Vite
CMD ["npm", "run", "dev"]