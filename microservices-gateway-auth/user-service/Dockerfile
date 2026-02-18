FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY user-service/pom.xml ./user-service/
COPY user-service/src ./user-service/src
RUN mvn clean package -DskipTests -f user-service/pom.xml

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
