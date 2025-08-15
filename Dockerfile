FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew
RUN ./gradlew clean bootJar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./app.jar

ENV GOOGLE_APPLICATION_CREDENTIALS="/etc/secrets/gcp-credentials.json"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]