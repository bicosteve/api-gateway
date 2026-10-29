# syntax=docker/dockerfile:1

# Stage 1: Build the application with Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# Stage 2: Minimal runtime image
# Supports either local standalone builds (copying from build stage)
# or CI builds where pre-built target/*.jar is provided.
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S usr \
    && adduser -S usr -G usr

ARG JAR_FILE=app.jar
COPY --from=build /app/target/*.jar /app/app.jar

USER usr:usr

EXPOSE 5001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
