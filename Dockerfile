FROM openjdk:8-jdk-alpine
MAINTAINER david.martinez.dev@gmail.com
VOLUME /tmp
EXPOSE 8080
ADD target/task-manager-0.0.1-SNAPSHOT.jar task-manager.jar
ENTRYPOINT ["java","-Dspring.profiles.active=postgreSQL","-jar","/task-manager.jar"]