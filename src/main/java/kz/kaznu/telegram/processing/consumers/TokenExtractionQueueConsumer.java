package kz.kaznu.telegram.processing.consumers;

import kz.kaznu.telegram.processing.dao.shorts.TelegramMessageShortInfo;
import kz.kaznu.telegram.processing.services.TokenExtractionQueueService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractionQueueConsumer {

    private final TokenExtractionQueueService tokenExtractionQueueService;


    @Autowired
    public TokenExtractionQueueConsumer(TokenExtractionQueueService tokenExtractionQueueService) {
        this.tokenExtractionQueueService = tokenExtractionQueueService;
    }

    @RabbitListener(queues = "${custom.rabbitmq.token.extraction.queue}")
    public void consumeMessage(TelegramMessageShortInfo message) {
        tokenExtractionQueueService.consumeMessage(message);
    }
}
