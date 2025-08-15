# ----- ESTÁGIO 1: CONSTRUÇÃO (BUILD) -----
# Usamos uma imagem com Gradle e Java 17 já instalados.
FROM gradle:7.6.1-jdk17 AS build

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia os arquivos de build do Gradle para aproveitar o cache do Docker.
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# Copia o código-fonte da sua aplicação.
COPY src ./src

# Torna o script Gradle executável (importante para sistemas Unix).
RUN chmod +x gradlew

# Executa o build da aplicação e gera o JAR.
RUN ./gradlew clean bootJar

# ----- ESTÁGIO 2: IMAGEM FINAL DE EXECUÇÃO -----
# Usamos uma imagem leve que contém apenas o ambiente de execução do Java (JRE).
FROM eclipse-temurin:17-jre-alpine

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o arquivo JAR do estágio de build.
COPY --from=build /app/build/libs/*.jar ./app.jar

# Expõe a porta que sua aplicação usa (padrão do Spring Boot é 8080).
EXPOSE 8080

# Define o comando para executar a aplicação.
ENTRYPOINT ["java", "-jar", "app.jar"]