package kz.kaznu.telegram.processing.dao.entities;

import kz.kaznu.telegram.processing.models.enums.Gender;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TelegramUser {

    private Long id;
    private String firstName = "";
    private String lastName = "";
    private String userName = "";
    private String phoneNumber = "";
    private String languageCode;
    private String type;
    private Long countryId;
    private Gender gender;
    private LocalDateTime date;

    public TelegramUser(Long id) {
        this.id = id;
    }
}
