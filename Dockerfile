# syntax=docker/dockerfile:1

# Production runtime image packaging the verified JAR artifact from the build step
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S usr \
    && adduser -S usr -G usr

COPY target/*.jar /app/app.jar

USER usr:usr

EXPOSE 5001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
