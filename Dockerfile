## Stage 1 : build avec Maven incluant Java 21
#FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
#WORKDIR /app
#
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## Stage 2 : runtime Java 21
#FROM eclipse-temurin:21-jdk
#WORKDIR /app
#
#COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8088
#ENTRYPOINT ["java","-jar","app.jar"]


# Stage 1 : build avec Maven et Java 21
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 : runtime Java 21
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Exposer le port Render par défaut
EXPOSE 8080

# Lancer l'application avec le port dynamique Render
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
