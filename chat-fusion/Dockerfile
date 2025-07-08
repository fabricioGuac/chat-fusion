# Java Developer Kit image to compile the project in
FROM eclipse-temurin:21-jdk-alpine AS builder

# Sets the working directory
WORKDIR /app

# Copies the project files into the container
COPY . .

# Grants permissions and builds the project (skipping tests)
RUN chmod +x mvnw && ./mvnw clean install -DskipTests

# Java Runtime Enviroment to run the compiled project in (now no name)
FROM eclipse-temurin:21-jre-alpine

# Sets the working directory (again?)
WORKDIR /app

# Copies the built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Exposes the app port
EXPOSE 8080

# Runs the app (command-line instructions used to execute a Java application)
ENTRYPOINT [ "java", "-jar", "app.jar" ]