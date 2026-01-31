# ===== STAGE 1: Build app =====
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy source code
COPY . .

# Build project
RUN mvn clean package -DskipTests

# ===== STAGE 2: Run app =====
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy jar from stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
