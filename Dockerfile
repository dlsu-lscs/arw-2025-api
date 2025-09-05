# Stage 1: Build the application
FROM maven:3.8.8-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the application, skipping tests
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final, lean image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the executable JAR from the build stage
COPY --from=build /app/target/arw-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set the entrypoint to run the application
# The production profile should be activated via Docker run command or Compose file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

