package kz.kaznu.telegram.processing.dao.entities;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TelegramChat {

    private Long id;
    private String type;
    private String title;
    private LocalDateTime date;
    private Boolean isChannel;
    private Long countryId;
    private String description;
    private boolean isActive;
    private boolean isDeleted;
    private Integer memberCount;

    public TelegramChat(Long id) {
        this.id = id;
    }
}
