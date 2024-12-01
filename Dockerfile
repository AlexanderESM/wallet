FROM openjdk:17-jdk-slim
COPY out/artifacts/wallet_jar/wallet.jar wallet-app.jar
ENTRYPOINT ["java", "-jar", "/wallet-app.jar"]
