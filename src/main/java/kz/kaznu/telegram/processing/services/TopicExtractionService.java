package kz.kaznu.telegram.processing.services;

import kz.kaznu.telegram.processing.dao.entities.TelegramMessage;
import kz.kaznu.telegram.processing.models.*;
import kz.kaznu.telegram.processing.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional
public class TopicExtractionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicExtractionService.class);

    private final Utils utils = new Utils();
    private final TelegramMessageService telegramMessageService;

    private final List<String> kazakhChars = Arrays.asList("ң","қ","ғ","ө","і","ұ","ү","ә","Ә","Ғ","Қ","Ң","Ө","Ұ","Ү","Һ","І");

    @Autowired
    public TopicExtractionService(TelegramMessageService telegramMessageService) {
        this.telegramMessageService = telegramMessageService;
    }

    public Map<TimeWindow, List<String>> extractTopic(LocalDateTime dateStart, LocalDateTime dateStop, int maxTopicCount) throws IOException {
        LOGGER.info("Extract topics for dates: " + dateStart + " - " + dateStop);
        final List<TelegramMessage> telegramMessages = telegramMessageService.findTelegramMessagesBetweenDates(dateStart, dateStop);

        // Time window by date is not strong, could be changed
        final Map<LocalDate, List<TelegramMessage>> messagesByDate = new HashMap<>();
        for (TelegramMessage telegramMessage : telegramMessages) {
            if (kazakhChars.stream().anyMatch(kazakhChar -> telegramMessage.getMessage().contains(kazakhChar))) {
                continue;
            }
            if (messagesByDate.containsKey(telegramMessage.getDate().toLocalDate())) {
                final List<TelegramMessage> telegramMessagesByDate = messagesByDate.get(telegramMessage.getDate().toLocalDate());
                telegramMessagesByDate.add(telegramMessage);
                messagesByDate.put(telegramMessage.getDate().toLocalDate(), telegramMessagesByDate);
            } else {
                messagesByDate.put(telegramMessage.getDate().toLocalDate(), new ArrayList<>(Arrays.asList(telegramMessage)));
            }
        }

        // Preprocess data
        //TODO: add date_stop, now it depends on difference by 1 day
        final Map<TimeWindow, List<String>> tokensPerTimeWindow = new HashMap<>();
        for (Map.Entry<LocalDate, List<TelegramMessage>> entry : messagesByDate.entrySet()) {
            LOGGER.info("Dates: " + entry.getKey().toString() + " - " + entry.getValue().size());
            final String wholeTextLemmas = utils.extractLemmasAsText(entry.getValue().stream().map(TelegramMessage::getMessage).reduce(" ", String::concat));
            tokensPerTimeWindow.put(new TimeWindow(entry.getKey(), entry.getKey()), utils.extractTokens(wholeTextLemmas));
        }

        final Map<TimeWindow, List<String>> topicsByTimeWindow = new HashMap<>();
        for (Map.Entry<TimeWindow, List<String>> entry: tokensPerTimeWindow.entrySet()) {
            final Map<String, Double> tfIdfPerToken = utils.calculateTfIds(entry.getValue(), tokensPerTimeWindow);
            final Map<String, Double> tfIdfPerTokenSorted = tfIdfPerToken.entrySet().stream()
                    .sorted(Map.Entry.<String, Double> comparingByValue().reversed())
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

            topicsByTimeWindow.put(entry.getKey(), new ArrayList<>());
            int count = 0;
            for (Map.Entry<String, Double> entryToken : tfIdfPerTokenSorted.entrySet()) {
                topicsByTimeWindow.get(entry.getKey()).add(entryToken.getKey());
                count++;
                if (count == maxTopicCount) {
                    break;
                }
            }
        }
        return topicsByTimeWindow;
    }


}
