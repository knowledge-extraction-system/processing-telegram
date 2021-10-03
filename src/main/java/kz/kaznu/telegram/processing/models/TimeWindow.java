package kz.kaznu.telegram.processing.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeWindow {

    private LocalDate dateStart;
    private LocalDate dateStop;

}
