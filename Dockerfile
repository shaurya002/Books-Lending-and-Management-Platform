FROM eclipse-temurin:21-jre
WORKDIR /application
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
