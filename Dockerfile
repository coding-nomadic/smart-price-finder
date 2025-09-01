#
# Build stage
#
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Install Maven manually (since official Eclipse Temurin image doesn’t include Maven)
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the Spring Boot JAR
RUN mvn clean package -DskipTests

#
# Runtime stage
#
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/product-price-comparison-0.0.1-SNAPSHOT.jar app.jar

# Expose port for the application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
