package kz.kaznu.telegram.processing.dao.entities;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TelegramMessage {

    private Long id;
    private TelegramChat telegramChat;
    private TelegramUser telegramUser;
    private String message;
    private LocalDateTime date;
    private LocalDateTime messageDate;
    private Long messageId;
    private boolean isDeleted;
    private LocalDateTime deletionDate;

    public TelegramMessage(TelegramChat telegramChat, TelegramUser telegramUser, String message, Long messageId) {
        this.telegramChat = telegramChat;
        this.telegramUser = telegramUser;
        this.message = message;
        this.date = LocalDateTime.now();
        this.messageId = messageId;
        this.isDeleted = false;
    }
}
