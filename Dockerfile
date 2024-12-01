FROM openjdk:17-jdk-slim
COPY target/wallet-app.jar wallet-app.jar
ENTRYPOINT ["java", "-jar", "/wallet-app.jar"]
