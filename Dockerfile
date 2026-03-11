FROM maven:3.9.12-amazoncorretto-25 AS build
LABEL authors="ASKekishev"
WORKDIR /app
COPY . .

RUN mvn clean package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/Converter-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]