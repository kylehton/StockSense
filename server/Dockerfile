# ---- STAGE 1: Build the application ----
    FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder

    # Set the working directory for the build
    WORKDIR /build
    
    # Copy your backend source code into the image
    COPY . .
    
    # Run the Maven build to create the jar
    RUN mvn clean package -DskipTests
    
    # ---- STAGE 2: Run the application ----
    FROM eclipse-temurin:21-jdk
    
    WORKDIR /app
    
    # Copy the built jar from the previous stage
    COPY --from=builder /build/target/*.jar app.jar
    
    EXPOSE 8080
    
    ENTRYPOINT ["java", "-jar", "app.jar"]
    