package kz.kaznu.telegram.processing.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author magzhan.karasayev
 * @since 6/1/17 7:00 PM
 */
@Service
public class ElasticSearchConfig {
    public String getBaseUrl() {
        return "http://" + getHostName() + ":" + getHttpPort() + "/";
    }

    private int httpPort = 9200;
    private int transportPort = 9300;

    @Value("${common.option.elasticsearch.hostname}")
    private String hostName;

    /**
     * for test
     * @param httpPort
     */
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public int getTransportPort() {
        return transportPort;
    }

    public void setTransportPort(int transportPort) {
        this.transportPort = transportPort;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
