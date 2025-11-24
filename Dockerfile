FROM eclipse-temurin:21-jdk
WORKDIR .
COPY build/libs/ChatAiBot-0.0.1-SNAPSHOT.jar chat_bot.jar
COPY src/main/resources/security/vault.jks /chat-ai-bot/vault.jks
ENTRYPOINT ["java", "-jar", "chat_bot.jar"]
