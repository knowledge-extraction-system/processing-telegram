package kz.kaznu.telegram.processing.services;

import kz.kaznu.telegram.processing.configs.ElasticSearchConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

/**
 * @author magzhan.karasayev
 * @since 6/1/17 7:26 PM
 */
@Service
public class ElasticClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientFactory.class);

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    private RestHighLevelClient client;

    /**
     * Be sure to close this client when it is not needed
     * @return
     */
    public RestHighLevelClient getClientInstance() {
        if (client == null) {
            createClient();
        }
        return client;
    }

    private void createClient() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticSearchConfig.getHostName(), elasticSearchConfig.getHttpPort(), "http"),
                        new HttpHost(elasticSearchConfig.getHostName(), 9201, "http")));
    }

    private InetAddress getLocalhost() {
        try {
            return InetAddress.getByName(elasticSearchConfig.getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void createIndex(String idxName) throws IOException {
        if (isIndexExist(idxName)) {
            return;
        }
        CreateIndexRequest request = new CreateIndexRequest(idxName);
        String settings = readSettings(idxName);
        request.source(settings,  XContentType.JSON);

        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        LOGGER.info("CreateIndexResponse: " + createIndexResponse.toString());
    }

    public boolean deleteAndCreateIndex(String idxName) throws IOException {
        if (isIndexExist(idxName)) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(idxName);
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            if (!deleteIndexResponse.isAcknowledged()) {
                LOGGER.error("Could not delete an index " + idxName);
                return false;
            }
        }
        CreateIndexRequest request = new CreateIndexRequest(idxName);
        String settings = readSettings(idxName);
        request.source(settings,  XContentType.JSON);

        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        LOGGER.info("CreateIndexResponse: " + createIndexResponse.toString());
        return true;
    }

    public boolean isIndexExist(String idxName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(idxName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    private String readSettings(String indexName) {
        InputStream inputStream = ElasticClientFactory.class.getResourceAsStream("/settings-" + indexName + ".json");

        try {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                return bufferedReader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
