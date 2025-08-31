#generate build
FROM maven:3-eclipse-temurin-11-alpine as build
WORKDIR/app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true


#dockirize
FROM eclipse-temurin:23.0.2_7-jdk-ubi9-minimal
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar ./demo.jar
EXPOSE 8080
ENTRYPOINT ["java" , "-jar" , "demo.jar"]