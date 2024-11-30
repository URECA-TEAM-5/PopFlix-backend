FROM openjdk:17-jdk

WORKDIR /app

COPY ./build/libs/POPFLIX-backend-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]