FROM openjdk:8-jdk-alpine
COPY ./target/config-0.0.1-SNAPSHOT.jar config.jar

ENTRYPOINT ["java", "-jar", "config.jar"]
EXPOSE 8888
