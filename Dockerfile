FROM eclipse-temurin:21-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the already-built Spring Boot jar
COPY target/innovation-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 7080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
