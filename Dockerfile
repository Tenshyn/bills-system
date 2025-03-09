# Etapa de Build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o pom.xml e baixa dependências para otimizar cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Compila o projeto sem rodar testes
RUN mvn package -DskipTests

# Etapa de Produção (imagem menor)
FROM eclipse-temurin:17-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o JAR gerado da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta do Spring Boot
EXPOSE 8080

# Define o comando de execução
CMD ["java", "-jar", "app.jar"]
