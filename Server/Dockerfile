FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/Server-1.0.0.jar app.jar
ADD entrypoint.sh entrypoint.sh

EXPOSE 9090 9000

ENTRYPOINT ["sh", "/app/entrypoint.sh"]