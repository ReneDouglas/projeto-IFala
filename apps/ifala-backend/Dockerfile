# ---- ESTÁGIO 1: Build Otimizado ----
# Usar uma imagem que já vem com Maven e JDK 25
FROM maven:3.9.11-amazoncorretto-25-alpine AS builder
WORKDIR /app

# Copiar os arquivos do projeto
COPY pom.xml .
COPY src ./src
COPY checkstyle.xml .

# --mount=type=cache,target=/root/.m2 diz ao Docker para usar um cache persistente
# para as dependências do Maven, tornando builds futuros muito mais rápidos.
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


# ---- ESTÁGIO 2: Runtime ----
FROM bellsoft/liberica-runtime-container:jre-25-cds-slim-musl
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_TOOL_OPTIONS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"

EXPOSE 8080 5005

# Copia apenas o artefato final do estágio de build
COPY --from=builder /app/target/ifala-0.0.1-SNAPSHOT.jar ifala-api.jar

ENTRYPOINT ["java", "-jar", "ifala-api.jar"]