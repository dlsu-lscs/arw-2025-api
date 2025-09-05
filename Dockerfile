# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

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

# Copy the executable JAR from the build stage
COPY --from=build /app/target/arw-2025-api-*.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Set the entrypoint to run the application
# The production profile should be activated via Docker run command or Compose file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
