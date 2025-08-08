# Backend Dockerfile
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /workspace/app

# Copy pom.xml and download dependencies
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY backend/src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 -S studysync && adduser -u 1001 -S studysync -G studysync

WORKDIR /app

# Copy JAR from build stage
COPY --from=build --chown=studysync:studysync /workspace/app/target/*.jar app.jar

# Switch to non-root user
USER studysync:studysync

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

