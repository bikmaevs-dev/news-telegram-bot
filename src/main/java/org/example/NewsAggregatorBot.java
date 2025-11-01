package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class NewsAggregatorBot extends TelegramLongPollingBot{

    @Override
    public String getBotUsername() {
        return "NewsBot";
    }

    @Override
    public String getBotToken() {
        return "8483866878:AAF_emLj7zEf3A3i93lLmEcM5ZIrlCG0HsI";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendWelcomeMessage(chatId);
                    break;
                case "/news":
                    sendLatestNews(chatId);
                    break;
                case "/help":
                    sendHelpMessage(chatId);
                    break;
                default:
                    sendUnknownCommand(chatId);
                    break;
            }
        }
    }
    private void sendWelcomeMessage(long chatId) {
        String text = "Привет! Я бот-агрегатор новостей. \n\n" +
                "Доступные команды:\n" +
                "/news - Получить 10 свежих новостей\n" +
                "/help - Помощь\n\n" +
                "Нажми /news, чтобы увидеть последние новости!";
        sendMessage(chatId, text);
    }

    private void sendHelpMessage(long chatId){
        String text = "Помощь по боту:\n\n" +
                "/news - Получить 10 последних новостей с Lenta.ru\n" +
                "/start - Перезапустить бота\n" +
                "/help - Показать это сообщение";
        sendMessage(chatId, text);
    }

    private void sendUnknownCommand(long chatId){
        sendMessage(chatId, "Неизвестная команда. Используйте /help для списка команд.");
    }

    private void sendLatestNews(long chatId){
        sendMessage(chatId, "Загружаю свежие новости...");

        List<NewsArticle> articles = NewsParser.parseLentaRu();

        if (articles.isEmpty()){
            sendMessage(chatId, "Не удалось загрузить новости. Попробуйте позже.");
            return;
        }

        sendMessage(chatId, "**Последние новости с Lenta.ru:**\n");

        for (int i = 0; i < articles.size(); i++){
            NewsArticle article = articles.get(i);
            String newsText = "**" + (i + 1) + ".** " + article.getTitle() + "\n" + "URL: " + article.getUrl() + "\n";
            sendMessage(chatId, newsText);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        sendMessage(chatId, "Готово! Используйте /news для обновления.");
    }

    private void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        message.setParseMode("Markdown");

        try{
            execute(message);
        } catch (TelegramApiException e){
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }
}
