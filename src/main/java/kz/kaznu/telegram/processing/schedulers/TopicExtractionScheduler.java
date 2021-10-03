package kz.kaznu.telegram.processing.schedulers;

import kz.kaznu.telegram.processing.models.TimeWindow;
import kz.kaznu.telegram.processing.services.TopicExtractionService;
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
import java.util.List;
import java.util.Map;

@Service
@Component
public class TopicExtractionScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicExtractionScheduler.class);

    private final TopicExtractionService topicExtractionService;

    private final int MAX_TOPIC_COUNT = 5;

    @Autowired
    public TopicExtractionScheduler(TopicExtractionService topicExtractionService) {
        this.topicExtractionService = topicExtractionService;
    }

//    @Scheduled(cron = "0 0 0 * * *")
    public void extract() throws IOException {
        final LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        final LocalDateTime dateStop = LocalDateTime.of(today, midnight);
        final LocalDateTime dateStart = LocalDateTime.of(today, midnight).minusDays(7);
        final Map<TimeWindow, List<String>> topicsByTimeWindow = topicExtractionService.extractTopic(dateStart, dateStop, MAX_TOPIC_COUNT);

        for (Map.Entry<TimeWindow, List<String>> entry : topicsByTimeWindow.entrySet()) {
            LOGGER.info("Time window: " + entry.getKey());
            LOGGER.info("Topics: " + entry.getValue());
        }
    }

}
