package kz.kaznu.telegram.processing.consumers;

import kz.kaznu.telegram.processing.dao.shorts.TelegramMessageShortInfo;
import kz.kaznu.telegram.processing.services.TopicExtractionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicExtractionQueueConsumer {

    private final TopicExtractionService topicExtractionService;


    @Autowired
    public TopicExtractionQueueConsumer(TopicExtractionService topicExtractionService) {
        this.topicExtractionService = topicExtractionService;
    }

    @RabbitListener(queues = "${custom.rabbitmq.topic.extraction.queue}")
    public void consumeMessage(TelegramMessageShortInfo message) {
//        topicExtractionQueueService.consumeMessage(message);
    }
}
