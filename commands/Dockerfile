FROM openjdk:8-jdk-alpine
COPY ./target/commands-0.0.1-SNAPSHOT.jar commands.jar

ENTRYPOINT ["java", "-jar", "commands.jar"]
EXPOSE 8080
