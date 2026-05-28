FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/problem-service-impl-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
