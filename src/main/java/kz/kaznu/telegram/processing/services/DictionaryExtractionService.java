package kz.kaznu.telegram.processing.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kaznu.telegram.processing.dao.entities.TelegramMessage;
import kz.kaznu.telegram.processing.dao.repositories.TelegramMessageRepository;
import kz.kaznu.telegram.processing.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

@Service
@EnableAsync
public class DictionaryExtractionService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());

    // Спорт Шредингера, С-Ш чатик, Okko Спорт, Okko Спорт Chat
    private final List<Long> chatIdsSport = Arrays.asList(-1001335196901L, -1001191368922L, -1001208550418L, -1001531149112L);

    // zakon.kz - обсуждения, Tengrinews.kz
    private final List<Long> chatIdsNews = Arrays.asList(-1001208833686L, -1001003360080L);

    private final long minFrequency = 1;
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private TelegramMessageRepository telegramMessageRepository;

    private final Utils utils = new Utils();

    public void extractSportDictionary(LocalDateTime dateStart, LocalDateTime dateStop) throws IOException {
        LOGGER.info("extractSportDictionary for dates: " + dateStart + " - " + dateStop);

        final List<TelegramMessage> telegramMessagesSport = telegramMessageRepository.findByChatIdAndBetweenDates(chatIdsSport, dateStart, dateStop);
        LOGGER.info("telegramMessagesSport count: " + telegramMessagesSport.size());
        final List<TelegramMessage> telegramMessagesNews = telegramMessageRepository.findByChatIdAndBetweenDates(chatIdsNews, dateStart, dateStop);
        LOGGER.info("telegramMessagesNews count: " + telegramMessagesNews.size());

        final Map<String, Long> sportTokens = new HashMap<>();
        extractShigles(telegramMessagesSport, sportTokens);
        LOGGER.info("sportTokens count: " + sportTokens.size());

        final Map<String, Long> newsTokens = new HashMap<>();
        extractShigles(telegramMessagesNews, newsTokens);
        LOGGER.info("newsTokens count: " + newsTokens.size());

        final Map<String, Double> shinglesSport = new HashMap<>();

        for (Map.Entry<String, Long> messageNGram : sportTokens.entrySet()){

            if(newsTokens.containsKey(messageNGram.getKey())) {
                if (messageNGram.getValue() >= minFrequency &&
                        newsTokens.get(messageNGram.getKey()) >= minFrequency) {
                    final long inMessages = messageNGram.getValue();
                    final long inNewsMessage = newsTokens.get(messageNGram.getKey());
                    final double exigencies = Math.log((double) inMessages / inNewsMessage);

                    shinglesSport.put(messageNGram.getKey(), exigencies);
                }
            }
        }
        LOGGER.info("shinglesSport count: " + shinglesSport.size());

        Map<String, Double> sortedAscendingOrder = Utils.sortByValue(shinglesSport, false);

        final PrintWriter writer = new PrintWriter(new File("shinglesSport_" + new Date()));
        final String mapAsString = gson.toJson(sortedAscendingOrder);
        writer.println(mapAsString);
        writer.close();
    }

    private void extractShigles(List<TelegramMessage> telegramMessagesNews, Map<String, Long> newsTokens) throws IOException {
        for (TelegramMessage telegramMessageNews : telegramMessagesNews) {
            List<String> tokens = utils.extractTokens(telegramMessageNews.getMessage());

            for (String token : tokens) {
                if (newsTokens.containsKey(token)) {
                    newsTokens.put(token, newsTokens.get(token) + 1);
                } else {
                    newsTokens.put(token, 1L);
                }
            }
        }
    }

}
