FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/spring-boot-mailing-system-0.0.1-SNAPSHOT.jar spring-boot-mailing-system.jar

ENTRYPOINT ["java","-jar","spring-boot-mailing-system.jar"]