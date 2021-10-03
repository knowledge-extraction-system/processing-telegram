package kz.kaznu.telegram.processing.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kaznu.telegram.processing.dao.entities.*;
import kz.kaznu.telegram.processing.models.enums.OnlineSocialNetworkEnum;
import kz.kaznu.telegram.processing.models.logger.NlpTokenLogging;
import kz.kaznu.telegram.processing.dao.shorts.TelegramMessageShortInfo;
import kz.kaznu.telegram.processing.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class TokenExtractionQueueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenExtractionQueueService.class);
    private Gson gson = new GsonBuilder().create();

    private final AmqpTemplate amqpTemplate;
    private final Utils utils = new Utils();

    private final NlpTokenService nlpTokenService;
    private final NlpUserTokenService nlpUserTokenService;
    private final NlpChatTokenService nlpChatTokenService;
    private final TelegramChatService telegramChatService;
    private final TelegramUserService telegramUserService;

    @Autowired
    public TokenExtractionQueueService(AmqpTemplate amqpTemplate, NlpTokenService nlpTokenService, NlpUserTokenService nlpUserTokenService,
                                       NlpChatTokenService nlpChatTokenService, TelegramChatService telegramChatService, TelegramUserService telegramUserService) {
        this.amqpTemplate = amqpTemplate;
        this.nlpTokenService = nlpTokenService;
        this.nlpUserTokenService = nlpUserTokenService;
        this.nlpChatTokenService = nlpChatTokenService;
        this.telegramChatService = telegramChatService;
        this.telegramUserService = telegramUserService;
    }

    public void consumeMessage(TelegramMessageShortInfo message) {
        LOGGER.info("Consumed message: " + gson.toJson(message));
        if (message.getOsn().equals(OnlineSocialNetworkEnum.TELEGRAM.name())) {
            extractAndSaveTokens(message);
        }
    }

    private void extractAndSaveTokens(TelegramMessageShortInfo messageShortInfo) {
        final List<String> tokens;
        try {
            tokens = utils.extractTokens(messageShortInfo.getMessage());
            final TelegramUser telegramUser = telegramUserService.getTelegramUserById(messageShortInfo.getUserId());
            final TelegramChat telegramChat = telegramChatService.getTelegramChatById(messageShortInfo.getChatId());

            for (String token : tokens) {
                final NlpTokenLogging tokenLogging = new NlpTokenLogging(token);
                final NlpToken nlpToken = new NlpToken(token);
                final Long tokenId = nlpTokenService.saveAndReturnId(nlpToken);
                nlpToken.setId(tokenId);

                if (telegramChat != null && !telegramChat.getType().equals("ChatTypePrivate")) {
                    final NlpChatToken nlpChatToken = new NlpChatToken(telegramChat, nlpToken);
                    final Long count = nlpChatTokenService.incrementCount(nlpChatToken);
                    tokenLogging.setTelegramChatId(telegramChat.getId());
                    tokenLogging.setChatTitle(telegramChat.getTitle());
                    tokenLogging.setCountByChat(count);
                }

                if (telegramUser != null) {
                    final NlpUserToken nlpUserToken = new NlpUserToken(telegramUser, nlpToken);
                    final Long count = nlpUserTokenService.incrementCount(nlpUserToken);
                    tokenLogging.setTelegramUserId(telegramUser.getId());
                    tokenLogging.setUserName(telegramUser.getFirstName() + " " + telegramUser.getLastName());
                    tokenLogging.setCountByUser(count);
                }

                LOGGER.info(gson.toJson(tokenLogging));
            }
        } catch (IOException e) {
            LOGGER.error("Error during token extraction", e);
        }
    }


}
