FROM openjdk:8-jdk-alpine
COPY ./target/logger-0.0.1-SNAPSHOT.jar logger.jar

ENTRYPOINT ["java", "-jar", "logger.jar"]
EXPOSE 8060
