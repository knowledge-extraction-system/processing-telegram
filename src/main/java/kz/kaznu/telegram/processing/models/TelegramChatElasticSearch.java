package kz.kaznu.telegram.processing.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramChatElasticSearch {

    private Long telegramChatId;
    private String title;
    private String description;
    private Integer memberCount;
    private boolean isChannel;

}
