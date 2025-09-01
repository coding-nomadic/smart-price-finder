#
# Build stage
#
FROM maven:3.9.2-jdk-21 AS build
WORKDIR /app

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
