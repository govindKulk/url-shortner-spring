FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/service-discovery-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"] 