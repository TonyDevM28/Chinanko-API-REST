# --- Etapa 1: Compilar ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecutar ---
FROM eclipse-temurin:21-jdk
WORKDIR /app

# CORRECCIÓN: Volvemos a buscar .jar (que es lo que genera Spring Boot estándar)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
