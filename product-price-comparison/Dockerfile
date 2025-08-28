# Use a Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar built by Maven/Gradle
COPY target/product-price-comparison-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Set entry point
ENTRYPOINT ["java","-jar","/app/app.jar"]
