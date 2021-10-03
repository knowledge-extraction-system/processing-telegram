package kz.kaznu.telegram.processing.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kaznu.telegram.processing.dao.entities.TelegramMessage;
import kz.kaznu.telegram.processing.dao.extended.NlpChatTokenExtended;
import kz.kaznu.telegram.processing.dao.repositories.NlpChatTokenRepository;
import kz.kaznu.telegram.processing.dao.repositories.TelegramMessageRepository;
import kz.kaznu.telegram.processing.models.TelegramMessageElasticSearch;
import kz.kaznu.telegram.processing.models.TokenFrequency;
import kz.kaznu.telegram.processing.utils.DateUtils;
import kz.kaznu.telegram.processing.utils.Utils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@EnableAsync
public class TextProcessingAsyncService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private Gson gson = new GsonBuilder().create();

    private final Utils utils = new Utils();

    @Autowired
    public TextProcessingAsyncService() {

    }

    @Async
    public Future<NlpChatTokenExtended> getLemma(NlpChatTokenExtended nlpChatTokenExtended) throws IOException {
        LOGGER.info("getLemma");
        return new AsyncResult(new NlpChatTokenExtended(utils.extractLemmaFromToken(nlpChatTokenExtended.getToken()), nlpChatTokenExtended.getChatCount()));
    }

    @Async
    public Future<TelegramMessageElasticSearch> getMessageLemmatized(TelegramMessageElasticSearch message) throws IOException {
        LOGGER.info("getMessageLemmatized");
        return new AsyncResult(new TelegramMessageElasticSearch(message, utils.extractLemmasAsText(message.getMessage())));
    }

}
