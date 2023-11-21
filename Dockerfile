FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install maven:3.8-openjdk-17 -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install 

FROM maven:3.8-openjdk-17-slim

EXPOSE 8080

COPY --from=build /target/deploy_render-1.0.0.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]