package kz.kaznu.telegram.processing.dao.shorts;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TelegramMessageShortInfo {

    private Long chatId;
    private Long userId;
    private Long messageId;
    private String message;
    private String osn;

}
