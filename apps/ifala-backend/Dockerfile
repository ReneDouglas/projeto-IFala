# ---- ESTÁGIO 1: Build Otimizado ----
# Usar uma imagem que já vem com Maven e JDK 25
FROM maven:3.9.11-amazoncorretto-25-alpine AS builder
WORKDIR /app

# Copiar os arquivos do projeto
COPY pom.xml .
COPY checkstyle.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# ---- ESTÁGIO 2: Runtime ---- Java 25 com CDS
FROM bellsoft/liberica-runtime-container:jre-25-cds-slim-musl
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=staging

EXPOSE 8080

# Copia apenas o artefato final do estágio de build
COPY --from=builder /app/target/ifala-0.0.1-SNAPSHOT.jar ifala-api.jar

ENTRYPOINT ["java", "-Xms256m", "-Xmx1g", "-jar", "ifala-api.jar"]