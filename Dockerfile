# Use uma imagem base oficial do Java 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo .jar construído pelo Gradle para dentro do contêiner
COPY build/libs/*.jar app.jar

# Expõe a porta 8080 do contêiner para a rede
EXPOSE 8080

# Comando para executar a aplicação quando o contêiner iniciar
# As variáveis de ambiente serão lidas do arquivo .env no servidor
ENTRYPOINT ["java", "-jar", "app.jar"]