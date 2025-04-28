# Dockerfile (for your Java API only)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
# ================================
# Stage 1: Build with Maven
# ================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom and download deps (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the source code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests


# ================================
# Stage 2: Run
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar from the previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
