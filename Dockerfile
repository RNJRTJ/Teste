FROM maven:3.8-openjdk-17 as build

COPY . .

RUN mvn clean package

FROM ibm-semeru-runtimes:open-17-jre-centos7

EXPOSE 8080

COPY --from=build /target/deploy_render-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]