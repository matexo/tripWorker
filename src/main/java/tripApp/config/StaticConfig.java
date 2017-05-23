package tripApp.config;

/**
 * Created by Matexo on 2017-05-17.
 */
public class StaticConfig {
    //SZPACHLA TODO do przeniesienia do propertiesow // parametrow wywolania

    public static final String storageName = "tripappdisks435";
    public static final String storageKey = "goOmWqmWbUi6OvMMRuOBKeaGjYuBRI4J0UZGj7LUn4VmgiCGvdOuwvKTuJLdXJpAAm3u7SejQTeiaHUnx5ltHg==";
    public static final String imageBlobServiceName = "trip-media";

    public static final String thumbnailResponseQueueServiceName = "thumbnailresp";
    public static final String thumbnailGenQueueServiceName = "thumbnailgen";

    public static final String posterResponseQueueServiceName = "posterresp";
    public static final String posterGenQueueServiceName = "postergen";
    public static final String posterBlobServiceName = "posters";

    public static AzureConfig getThumbnailResponseAzureConfig() {
        return new AzureConfig(storageName, storageKey,thumbnailResponseQueueServiceName);
    }

    public static AzureConfig getProgressResponseAzureConfig() {
        return new AzureConfig(storageName, storageKey,thumbnailResponseQueueServiceName);
    }

    public static AzureConfig getThumbnailBlobAzureConfig() {
        return new AzureConfig(storageName, storageKey, imageBlobServiceName);
    }

    public static AzureConfig getThumbnailGenQueueAzureConfig() {
        return new AzureConfig(storageName, storageKey, thumbnailGenQueueServiceName);
    }

    public static AzureConfig getPosterBlobAzureConfig() {
        return new AzureConfig(storageName, storageKey, posterBlobServiceName);
    }

    public static AzureConfig getPosterGenQueueAzureConfig() {
        return new AzureConfig(storageName, storageKey, posterGenQueueServiceName);
    }

    public static AzureConfig getPosterResponseAzureConfig() {
        return new AzureConfig(storageName, storageKey, posterResponseQueueServiceName);
    }

}
