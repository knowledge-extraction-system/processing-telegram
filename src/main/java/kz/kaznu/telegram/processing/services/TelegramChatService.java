package kz.kaznu.telegram.processing.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kz.kaznu.telegram.processing.dao.entities.TelegramChat;
import kz.kaznu.telegram.processing.dao.repositories.TelegramChatRepository;
import kz.kaznu.telegram.processing.models.TelegramChatElasticSearch;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TelegramChatService {

    private final String TELEGRAM_CHAT_INDEX_NAME = "telegram-chat-index";

    private final ElasticClientFactory elasticClientFactory;
    private RestHighLevelClient client;
    private final TelegramChatRepository telegramChatRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private Gson gson = new GsonBuilder().create();

    @Autowired
    public TelegramChatService(ElasticClientFactory elasticClientFactory, TelegramChatRepository telegramChatRepository) {
        client = elasticClientFactory.getClientInstance();
        this.elasticClientFactory = elasticClientFactory;
        this.telegramChatRepository = telegramChatRepository;
    }

    public TelegramChat getTelegramChatById(Long id) {
        return telegramChatRepository.findById(id);
    }

    public void addChatsToElasticSearch() throws IOException {
        final List<TelegramChatElasticSearch> telegramChats = telegramChatRepository.findAllSuperGroupsActive();
        LOGGER.info("Number of messages: " + telegramChats.size());

        elasticClientFactory.deleteAndCreateIndex(TELEGRAM_CHAT_INDEX_NAME);

        elasticBulkRequest(telegramChats, TELEGRAM_CHAT_INDEX_NAME);

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
