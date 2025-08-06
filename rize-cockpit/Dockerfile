FROM amazoncorretto:21.0.8
WORKDIR /app
COPY target/*.jar rize-cockpit.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rize-cockpit.jar"]