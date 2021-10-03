package kz.kaznu.telegram.processing.controllers;

import kz.kaznu.telegram.processing.models.TimeWindow;
import kz.kaznu.telegram.processing.services.TopicExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/topic")
public class TopicController {

    @Autowired
    private TopicExtractionService topicExtractionService;

    @RequestMapping(value = "/extract-by-dates", method = RequestMethod.GET)
    public Map<TimeWindow, List<String>> extractByDates(
            @RequestParam(name = "date_start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @RequestParam(name = "date_stop") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStop,
            @RequestParam(name = "max_count") int maxCount) throws IOException {
        final LocalTime midnight = LocalTime.MIDNIGHT;
        return topicExtractionService.extractTopic(LocalDateTime.of(dateStart, midnight), LocalDateTime.of(dateStop, midnight), maxCount);
    }

}
