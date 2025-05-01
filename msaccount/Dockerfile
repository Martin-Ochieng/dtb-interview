# syntax=docker/dockerfile:1.3

# ================================
# Stage 1: Build with Maven
# ================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy only pom.xml to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies (using cache mount)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the JAR file
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# ================================
# Stage 2: Run with JRE only
# ================================
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy only the generated JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the app as a non-root user
USER spring

# Expose the Spring Boot port
EXPOSE 8082

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
