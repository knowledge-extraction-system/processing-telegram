package kz.kaznu.telegram.processing.schedulers;

import kz.kaznu.telegram.processing.services.DictionaryExtractionService;
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
public class DictionaryExtractionScheduler {

    @Autowired
    private DictionaryExtractionService dictionaryExtractionService;

    @Scheduled(cron = "0 5 0 * * *")
    private void extractSportDictionary() throws IOException {
        final LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        final LocalDateTime dateStart = LocalDateTime.of(today, midnight).withDayOfMonth(1).withMonth(7);
        final LocalDateTime dateStop = LocalDateTime.of(today, midnight).withDayOfMonth(31).withMonth(7);

        dictionaryExtractionService.extractSportDictionary(dateStart, dateStop);
    }

}
