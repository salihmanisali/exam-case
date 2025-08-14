# Stage 1: Build the application
FROM maven:3.8-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping tests for faster build in Docker
RUN mvn clean install -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-focal AS runner

# Set the working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/exam-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
