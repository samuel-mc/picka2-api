FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Cache de dependencias (layer estable)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Build
COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

