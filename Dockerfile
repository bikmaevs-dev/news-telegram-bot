FROM openjdk: 11
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
CMD java, -jar, target/news-telegram-bot-1.0-SNAPSHOT.jar