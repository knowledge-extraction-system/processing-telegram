package kz.kaznu.telegram.processing.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kaznu.telegram.processing.dao.entities.TelegramMessage;
import kz.kaznu.telegram.processing.dao.extended.NlpChatTokenExtended;
import kz.kaznu.telegram.processing.dao.repositories.NlpChatTokenRepository;
import kz.kaznu.telegram.processing.dao.repositories.TelegramMessageRepository;
import kz.kaznu.telegram.processing.models.TelegramMessageElasticSearch;
import kz.kaznu.telegram.processing.models.TokenFrequency;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@EnableAsync
public class TelegramMessageService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private Gson gson = new GsonBuilder().create();

    private final String TELEGRAM_MESSAGE_INDEX_NAME = "telegram-message-index";
    private final String TELEGRAM_MESSAGE_LEMMATIZED_INDEX_NAME = "telegram-message-lemmatized-index";
    private final String NLP_TOKEN_FREQUENCY_INDEX_NAME = "nlp-token-frequency-index";
    private final String TELEGRAM_MESSAGE_MAPPING_NAME = "TelegramMessage";

    private final TextProcessingAsyncService textProcessingAsyncService;

    private final NlpChatTokenRepository nlpChatTokenRepository;
    private final TelegramMessageRepository telegramMessageRepository;

    private final ElasticClientFactory elasticClientFactory;
    private RestHighLevelClient client;

    private final Utils utils = new Utils();

    @Autowired
    public TelegramMessageService(ElasticClientFactory elasticClientFactory, TextProcessingAsyncService textProcessingAsyncService,
                                  NlpChatTokenRepository nlpChatTokenRepository, TelegramMessageRepository telegramMessageRepository) {
        client = elasticClientFactory.getClientInstance();
        this.textProcessingAsyncService = textProcessingAsyncService;
        this.nlpChatTokenRepository = nlpChatTokenRepository;
        this.telegramMessageRepository = telegramMessageRepository;
        this.elasticClientFactory = elasticClientFactory;
    }

    public void addMessagesToElasticSearch(LocalDateTime dateStart, LocalDateTime dateStop) throws IOException {
        LOGGER.info("addMessagesToElasticSearch for dates: " + dateStart + " - " + dateStop);
        final List<TelegramMessageElasticSearch> telegramMessages = telegramMessageRepository.extractDataForElasticSearch(dateStart, dateStop);
        LOGGER.info("Number of messages: " + telegramMessages.size());

        elasticClientFactory.createIndex(TELEGRAM_MESSAGE_INDEX_NAME);

        elasticBulkRequest(telegramMessages, TELEGRAM_MESSAGE_INDEX_NAME);

    }

    public void addMessagesLemmatizedToElasticSearch(LocalDateTime dateStart, LocalDateTime dateStop) throws IOException {
        LOGGER.info("addMessagesToElasticSearch for dates: " + dateStart + " - " + dateStop);
        final List<TelegramMessageElasticSearch> telegramMessages = telegramMessageRepository.extractDataForElasticSearch(dateStart, dateStop);
        LOGGER.info("Number of messages: " + telegramMessages.size());

        Collection<Future<TelegramMessageElasticSearch>> messageLemmasAsyncResults = new ArrayList<>();

        for (TelegramMessageElasticSearch message: telegramMessages) {
            messageLemmasAsyncResults.add(textProcessingAsyncService.getMessageLemmatized(message));
        }

        final List<TelegramMessageElasticSearch> telegramMessagesLemmatized = new ArrayList<>();
        messageLemmasAsyncResults.forEach(result -> {
            try {
                telegramMessagesLemmatized.add(result.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("LemmaExtraction Thread Exception.", e);
            }
        });
        LOGGER.info("Number of telegramMessagesLemmatized: " + telegramMessagesLemmatized.size());

        elasticClientFactory.createIndex(TELEGRAM_MESSAGE_LEMMATIZED_INDEX_NAME);

        elasticBulkRequest(telegramMessagesLemmatized, TELEGRAM_MESSAGE_LEMMATIZED_INDEX_NAME);

    }

    public List<TelegramMessage> findTelegramMessagesBetweenDates(LocalDateTime dateStart, LocalDateTime dateStop) {
        return telegramMessageRepository.findByDateBetween(dateStart, dateStop);
    }

    public void calculateFrequenciesAndAddToElasticsearch() throws IOException {
        LOGGER.info("calculateFrequenciesAndAddToElasticsearch");
        final List<NlpChatTokenExtended> nlpChatTokenExtendedList = nlpChatTokenRepository.findAllWithToken();
        LOGGER.info("nlpChatTokenExtendedList size: " + nlpChatTokenExtendedList.size());
        final Map<String, Long> lemmaFrequencies = new HashMap<>();

        Collection<Future<NlpChatTokenExtended>> lemmasAsyncResults = new ArrayList<>();
        for (int i = 0; i < nlpChatTokenExtendedList.size(); i++) {
            NlpChatTokenExtended nlpChatTokenExtended = nlpChatTokenExtendedList.get(i);
            if (nlpChatTokenExtended.getToken().trim().isEmpty()) {
                continue;
            }
            lemmasAsyncResults.add(textProcessingAsyncService.getLemma(nlpChatTokenExtended));
            LOGGER.info(i + ": " + nlpChatTokenExtended.getToken());
        }

        lemmasAsyncResults.forEach(result -> {
            try {
                NlpChatTokenExtended nlpChatTokenExtended = result.get();
                if (lemmaFrequencies.containsKey(nlpChatTokenExtended.getToken())) {
                    lemmaFrequencies.put(nlpChatTokenExtended.getToken(), lemmaFrequencies.get(nlpChatTokenExtended.getToken()) + nlpChatTokenExtended.getChatCount());
                } else {
                    lemmaFrequencies.put(nlpChatTokenExtended.getToken(), nlpChatTokenExtended.getChatCount());
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("LemmaExtraction Thread Exception.", e);
            }
        });

        LOGGER.info("lemmaFrequencies size: " + lemmaFrequencies.size());

        final List<TokenFrequency> tokenFrequencies = new ArrayList<>();
        final int tokenAmount = lemmaFrequencies.size();
        for (Map.Entry<String, Long> lemmaFrequency: lemmaFrequencies.entrySet()) {
            tokenFrequencies.add(new TokenFrequency(lemmaFrequency.getKey(), (double) lemmaFrequency.getValue()/tokenAmount));
        }

        elasticClientFactory.deleteAndCreateIndex(NLP_TOKEN_FREQUENCY_INDEX_NAME);

        elasticBulkRequest(tokenFrequencies, NLP_TOKEN_FREQUENCY_INDEX_NAME);
    }

    private void elasticBulkRequest(List<?> objectList, String indexName) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        int counter = 0;
        for (int i = 0; i < objectList.size(); i++) {
            bulkRequest.add(new IndexRequest(indexName)
                    .source(gson.toJson(objectList.get(i)), XContentType.JSON));
            counter++;
            if (counter >= 4000) {
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                LOGGER.info("BulkResponse status " + bulkResponse.status().name());
                bulkRequest = new BulkRequest();
                counter = 0;
            }
        }
        if (counter > 0) {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            LOGGER.info("BulkResponse status " + bulkResponse.status().name());
        }
    }
}
