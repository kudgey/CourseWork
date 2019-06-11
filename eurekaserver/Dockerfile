FROM openjdk:8-jdk-alpine
COPY ./target/eurekaserver-0.0.1-SNAPSHOT.jar serv.jar

ENTRYPOINT ["java", "-jar", "serv.jar"]
EXPOSE 8761
