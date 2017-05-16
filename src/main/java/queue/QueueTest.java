package queue;

import com.microsoft.azure.storage.StorageException;
import config.AzureConfig;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Matexo on 2017-05-07.
 */
public class QueueTest {
    public static String accountName = "tripappdisks435";
    public static String accountKey = "1nXbjOtmxm1sX3z6sPVGpDOUCfrrMTJpQ6NWc59VbyzmATdbrFUWE5uXMbk6DTJ9GUmH36590thxOKWxoXLNkA==";
    public static String queueServiceName = "thumbnailgen";
    public static String containerServiceName = "trip-media";

    public static void main(String[] args) throws URISyntaxException, StorageException, IOException {
        AzureConfig queueConfig = new AzureConfig(accountName , accountKey , queueServiceName);
        AzureConfig containerConfig = new AzureConfig(accountName , accountKey , containerServiceName);
        ResizeQueue resizeQueue = new ResizeQueue(queueConfig , containerConfig);
        resizeQueue.run();
    }
}
