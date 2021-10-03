package kz.kaznu.telegram.processing.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessageElasticSearch {

    private Long telegramMessageId;
    private Long date;
    private String message;
    private Long telegramUserId;
    private Long telegramChatId;
    private Long threadId;
    private Long threadCount;
    private Long userChatCount;

    public TelegramMessageElasticSearch(TelegramMessageElasticSearch telegramMessage, String lemmatizedMessage) {
        this.telegramMessageId = telegramMessage.getTelegramMessageId();
        this.date = telegramMessage.getDate();
        this.message = lemmatizedMessage;
        this.telegramUserId = telegramMessage.getTelegramUserId();
        this.telegramChatId = telegramMessage.getTelegramChatId();
        this.threadCount = telegramMessage.getThreadCount();
        this.userChatCount = telegramMessage.getUserChatCount();
    }

}
