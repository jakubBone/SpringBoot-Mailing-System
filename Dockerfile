FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# COPY . .
# RUN mvn clean package -DskipTests

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
# COPY .env .env
# COPY src/main/resources/application.properties /app/application.properties

EXPOSE 8080

# ENTRYPOINT ["java", "-jar", "app.jar"]

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]