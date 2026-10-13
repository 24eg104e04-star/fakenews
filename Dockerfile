# ===================================================================
# Dockerfile for Fake News Detection System (Production Multi-Stage)
# ===================================================================

# Stage 1: Build JAR using Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and dependencies first for efficient caching
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

# Copy backend source code and static resources
COPY backend/src ./src

# Package production executable JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Production Runtime Image (Lightweight Alpine JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root system user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/target/fake-news-detection-backend-*.jar app.jar

# Expose default HTTP port
EXPOSE 8080

# Environment variables with production defaults
ENV PORT=8080 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Run the standalone application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
