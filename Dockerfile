FROM eclipse-temurin:21-jdk
WORKDIR .
COPY build/libs/ChatAiBot-0.0.1-SNAPSHOT.jar chat_bot.jar
ENTRYPOINT ["java", "-jar", "chat_bot.jar"]
