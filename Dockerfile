# =========================================================================
# BUILD STAGE: Compiles the application and creates the layered JAR
# =========================================================================
# Use a base image with Maven and JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
COPY .mvn/ .mvn/

# Download dependencies (this layer is cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src/ ./src/

# Package the application, skipping tests. This creates the layered JAR.
RUN mvn package -DskipTests

# =========================================================================
# FINAL STAGE: Creates the final, lightweight production image
# =========================================================================
# Use a minimal JRE base image for a smaller and more secure final image
FROM eclipse-temurin:21-jre-jammy

# Create a non-root user and group for security
RUN addgroup --system spring && adduser --system --ingroup spring springuser
USER springuser

# Set the working directory
WORKDIR /app

# Copy the layered JAR components from the build stage
# This order is intentional to maximize Docker cache usage
COPY --from=build /app/target/BOOT-INF/layers/dependencies/ ./
COPY --from=build /app/target/BOOT-INF/layers/spring-boot-loader/ ./
COPY --from=build /app/target/BOOT-INF/layers/snapshot-dependencies/ ./
COPY --from=build /app/target/BOOT-INF/layers/application/ ./

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application
# JarLauncher is used to launch the application from the layered structure
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
