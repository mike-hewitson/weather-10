FROM openjdk:8-alpine

COPY target/uberjar/weather-10.jar /weather-10/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/weather-10/app.jar"]
