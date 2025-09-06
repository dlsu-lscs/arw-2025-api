# Stage 1: Build the application
FROM maven:3.8.8-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker layer caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make sure mvnw is executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the application, skipping tests
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final, lean image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install wget for healthcheck
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Create a non-root user and group
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Copy the executable JAR from the build stage and set ownership
COPY --from=build --chown=spring:spring /app/target/arw-0.0.1-SNAPSHOT.jar /app/app.jar

# Switch to the non-root user
USER spring

# Expose the application port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]