FROM openjdk:8-jdk-alpine
COPY ./target/manager-0.0.1-SNAPSHOT.jar manager.jar

ENTRYPOINT ["java", "-jar", "manager.jar"]
EXPOSE 8085
