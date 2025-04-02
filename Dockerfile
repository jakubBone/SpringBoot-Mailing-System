FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/version-info-service-0.0.1-SNAPSHOT.jar version-info-service.jar

ENTRYPOINT ["java","-jar","/app.jar"]