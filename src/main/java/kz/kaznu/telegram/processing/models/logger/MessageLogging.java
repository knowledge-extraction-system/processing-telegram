package kz.kaznu.telegram.processing.models.logger;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageLogging {

    private Long telegramMessageId;
    private String telegramMessageText;
    private long telegramUserId;
    private String telegramChatType;
    private String telegramUserType;

}
