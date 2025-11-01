package org.example;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NewsParser {
    public static void main(String []args){
        System.out.println("Парсим новости с Lenta.ru...");
        List<NewsArticle> articles = parseLentaRu();

        System.out.println("Найдено " + articles.size() + " новостей:\n");
        for (NewsArticle article: articles){
            System.out.println(article);
        }
    }

    //главный метод, который возвращает список новостей с Lenta.ru
    public static List<NewsArticle> parseLentaRu(){
        List<NewsArticle> newsList = new ArrayList<>();
        String url = "https://lenta.ru/";

        try {
            Document doc = Jsoup.connect(url).userAgent("Mozill a/5.0 (Windows NT 10.0; Win64; x64)" +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36").timeout(10000).get();

            Elements newsElements = doc.select("a[href*=/news/]");

            if (newsElements.isEmpty()){
                newsElements = doc.select("selection a, .b-yellow-box a, .item");
            }

            if (newsElements.isEmpty()){
                newsElements = doc.select("a[href*=/news/2024]");
            }

            System.out.println("Найдено элементов: " + newsElements.size());

            int count = 0;
            for (Element element: newsElements){
                String title = element.text().trim();
                String link = element.attr("abs:href");

                boolean isValid = !title.isEmpty() && title.length() > 5 && link.contains("lenta.ru/news/") &&
                        !title.toLowerCase().contains("фото") &&
                        !title.toLowerCase().contains("видео");

                if (isValid){
                    if (link.startsWith("/")) {
                        link = "https://lenta.ru" + link;
                    }
                    newsList.add(new NewsArticle(title, link));
                    count++;
                    System.out.println("Добавлена новость: " + title);

                    if (count >= 10) {
                        break;
                    }
                }
            }

            if (newsList.isEmpty()) {
                System.out.println("Не удалось найти новость. Отладочная информация:");
                System.out.println("Заголовок страницы: " + doc.title());

                Elements allLinks = doc.select("a[href]");

                System.out.println("Всего ссылок на странице: " + allLinks.size());
                for (int i=0; i < Math.min(20, allLinks.size()); i++){
                    Element link = allLinks.get(i);

                    System.out.println("Ссылка " + i + ": " + link.attr("href") + " -> " + link.text());
                }
            }

        } catch (IOException e){
            System.err.println("Ошибка при подключении к сайту: " + e.getMessage());
        } catch (Exception e){
            System.err.println("Произошла непредвиденная ошибка при парсинге: " + e.getMessage());
            e.printStackTrace();
        }

        return newsList;
    }
}
