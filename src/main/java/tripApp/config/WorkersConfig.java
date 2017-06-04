package tripApp.config;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by piotr on 17.05.17.
 */
public class WorkersConfig {
    public static final String
            STORAGE_NAME = "storage.name",
            STORAGE_ACCESS_KEY_1 = "storage.access.key1",
            STORAGE_ACCESS_KEY_2 = "storage.access.key2",
            BLOB_NAME = "storage.blobs.container.name",
            BLOB_URL = "storage.blobs.container.url",
            THUMBNAILS_GEN_QUEUE_NAME = "storage.queues.thumbnails.requests.name",
            THUMBNAILS_GEN_QUEUE_URL = "storage.queues.thumbnails.requests.url",
            THUMBNAILS_RESP_QUEUE_NAME = "storage.queues.thumbnails.responses.name",
            THUMBNAILS_RESP_QUEUE_URL = "storage.queues.thumbnails.responses.url",
            PRESENTATIONS_GEN_QUEUE_NAME = "storage.queues.presentations.requests.name",
            PRESENTATIONS_GEN_QUEUE_URL = "storage.queues.presentations.requests.url",
            PRESENTATIONS_RESP_QUEUE_NAME = "storage.queues.presentations.responses.name",
            PRESENTATIONS_RESP_QUEUE_URL = "storage.queues.presentations.responses.url",
            POSTERS_GEN_QUEUE_NAME = "storage.queues.posters.requests.name",
            POSTERS_GEN_QUEUE_URL = "storage.queues.posters.requests.url",
            POSTERS_RESP_QUEUE_NAME = "storage.queues.posters.responses.name",
            POSTERS_RESP_QUEUE_URL = "storage.queues.posters.responses.url";

    private static final Logger LOGGER = Logger.getLogger(WorkersConfig.class);

    private final Properties properties;

    public WorkersConfig() {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/workers.properties"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    private String getStorageName() {
        return getProperty(STORAGE_NAME);
    }

    private String getStorageAccessKey1() {
        return getProperty(STORAGE_ACCESS_KEY_1);
    }

    private String getStorageAccessKey2() {
        return getProperty(STORAGE_ACCESS_KEY_2);
    }

    private String getBlobName() {
        return getProperty(BLOB_NAME);
    }

    public AzureConfig getBlobConfig() {
        return new AzureConfig(getStorageName(), getStorageAccessKey2(), getBlobName());
    }

    public AzureConfig getThumbnailGenQueue() {
        return new AzureConfig(
                getStorageName(), getStorageAccessKey1(), getProperty(THUMBNAILS_GEN_QUEUE_NAME)
        );
    }

    public AzureConfig getThumbnailRespQueue() {
        return new AzureConfig(
                getStorageName(), getStorageAccessKey1(), getProperty(THUMBNAILS_RESP_QUEUE_NAME)
        );
    }

    public AzureConfig getPresentationGenQueue() {
        return new AzureConfig(
                getStorageName(), getStorageAccessKey1(), getProperty(PRESENTATIONS_GEN_QUEUE_NAME)
        );
    }

    public AzureConfig getPresentationRespQueue() {
        return new AzureConfig(
                getStorageName(), getStorageAccessKey1(), getProperty(PRESENTATIONS_RESP_QUEUE_NAME)
        );
    }

    public AzureConfig getPosterGenQueue() {
        return new AzureConfig(
                getStorageName(), getStorageAccessKey1(), getProperty(POSTERS_GEN_QUEUE_NAME)
        );
    }

    public AzureConfig getPosterRespQueue() {
        return new AzureConfig(
                getStorageName(), getStorageAccessKey1(), getProperty(POSTERS_RESP_QUEUE_NAME)
        );
    }

}
