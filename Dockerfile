# Etapa de build (usando Maven com JDK 17)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/avaliappgti
RUN mvn clean package -DskipTests

# Etapa de execução (runtime)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/avaliappgti/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]