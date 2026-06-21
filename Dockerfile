# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S usr && adduser -S usr -G usr

COPY --from=build /app/target/*.jar app.jar

USER usr:usr

EXPOSE 5001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
