FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/notification-service-impl-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]