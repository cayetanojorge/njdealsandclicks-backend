# Etapa 1: Build con Gradle (usa immagine con Java 21)
FROM gradle:8.5.0-jdk21-alpine AS builder

# Copia il progetto
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# Costruisci l'applicazione Spring Boot (file .jar)
RUN gradle bootJar --no-daemon

# Etapa 2: Immagine leggera per esecuzione
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp

# Copia il .jar costruito
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Espone la porta 8080 (puoi cambiarla in fly.toml se necessario)
EXPOSE 8080

# Esegui l'app
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.address=${SERVER_ADDRESS}", "-jar", "/app.jar"]
