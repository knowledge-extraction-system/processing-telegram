package kz.kaznu.telegram.processing.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {

    public static LocalDateTime getTodayMidnight() {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        return LocalDateTime.of(today, midnight);
    }

}
