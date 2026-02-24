FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy jar
COPY target/innovation-0.0.1-SNAPSHOT.jar app.jar

# Copy SSL folder
COPY ssl /ssl

EXPOSE 7080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=classpath:/,file:/app/application-docker.properties"]
