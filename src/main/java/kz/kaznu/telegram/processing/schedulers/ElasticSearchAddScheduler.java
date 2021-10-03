package kz.kaznu.telegram.processing.schedulers;

import kz.kaznu.telegram.processing.services.TelegramChatService;
import kz.kaznu.telegram.processing.services.TelegramMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Component
public class ElasticSearchAddScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchAddScheduler.class);

    private final TelegramMessageService telegramMessageService;
    private final TelegramChatService telegramChatService;

    @Autowired
    public ElasticSearchAddScheduler(TelegramMessageService telegramMessageService,
                                     TelegramChatService telegramChatService) {
        this.telegramMessageService = telegramMessageService;
        this.telegramChatService = telegramChatService;
    }

    @Scheduled(cron = "0 24 13 * * *")
    public void addMessages() throws IOException {
        final LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        final LocalDateTime dateStart = LocalDateTime.of(today, midnight).withDayOfMonth(23).withMonth(4);
        final LocalDateTime dateStop = LocalDateTime.of(today, midnight).withDayOfMonth(25).withMonth(4);

        telegramMessageService.addMessagesToElasticSearch(dateStart, dateStop);
    }

    @Scheduled(cron = "0 56 18 * * *")
    public void addChats() throws IOException {
        telegramChatService.addChatsToElasticSearch();
    }

    //TODO: too long
//    @Scheduled(cron = "0 30 14 * * *")
//    public void addMessagesLemmatized() throws IOException {
//        final LocalTime midnight = LocalTime.MIDNIGHT;
//        LocalDate today = LocalDate.now();
//        final LocalDateTime dateStop = LocalDateTime.of(today, midnight);
//        final LocalDateTime dateStart = LocalDateTime.of(today, midnight).minusDays(1);
//
//        telegramMessageService.addMessagesLemmatizedToElasticSearch(dateStart, dateStop);
//    }

    //TODO: define the frequency of tokens update
    @Scheduled(cron = "0 0 15 * * *")
    public void calculateFrequencies() throws IOException {
        telegramMessageService.calculateFrequenciesAndAddToElasticsearch();
    }

}
