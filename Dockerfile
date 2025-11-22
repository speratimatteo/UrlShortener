# --------------------------------------------------------------------------------------
# Stage 1: Build
# --------------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia pom.xml e scarica dipendenze (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia il codice sorgente
COPY src /app/src

# Compila l'applicazione senza test
RUN mvn clean package -DskipTests

# --------------------------------------------------------------------------------------
# Stage 2: Runtime
# --------------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Copia il JAR dal builder
COPY --from=builder /app/target/*.jar app.jar

# Esponi porta Spring Boot
EXPOSE 8080

# Profilo dev di default
ENV SPRING_PROFILES_ACTIVE=dev

# Variabili opzionali (puoi cambiare facilmente senza rebuild)
ENV APP_BASE_URL=http://localhost:8080/

# Comando di avvio
ENTRYPOINT ["java", "-jar", "app.jar"]
