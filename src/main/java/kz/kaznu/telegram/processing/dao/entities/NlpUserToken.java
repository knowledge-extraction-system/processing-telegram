package kz.kaznu.telegram.processing.dao.entities;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NlpUserToken {

    private TelegramUser telegramUser;
    private NlpToken nlpToken;
    private Long count;

    public NlpUserToken(TelegramUser telegramUser, NlpToken nlpToken) {
        this.telegramUser = telegramUser;
        this.nlpToken = nlpToken;
    }
}
