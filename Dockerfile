FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11
WORKDIR /app
COPY --from=builder /app/target/news-telegram-bot-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]