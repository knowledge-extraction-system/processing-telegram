package kz.kaznu.telegram.processing.configs;

import kz.kaznu.telegram.processing.services.TokenExtractionQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;

public class RabbitMQExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQExceptionStrategy.class);

    /**
     * Fatal messages are not requeued, they go straight to dead-letter-queue
     */
    @Override
    public boolean isFatal(Throwable t) {
        LOGGER.error("Error handler", t);
        // all exceptions that are not AVExceptions are fatal
        return true;
    }
}
