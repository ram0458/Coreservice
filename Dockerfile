FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD
MAINTAINER Phaneendra Satyavolu
COPY pom.xml /app/
COPY src /app/src
RUN mvn -f /app/pom.xml clean package -e -DskipTests

FROM openjdk:11-jre-slim
COPY --from=MAVEN_BUILD /app/target/convert-rite-api-0.0.1-SNAPSHOT.jar /app/
RUN apt-get update
RUN apt-get install net-tools
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "app/convert-rite-api-0.0.1-SNAPSHOT.jar"]
EXPOSE 9000

