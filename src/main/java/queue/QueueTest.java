package queue;

import com.microsoft.azure.storage.StorageException;
import config.AzureConfig;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Matexo on 2017-05-07.
 */
public class QueueTest {
    public static String accountName = "tripcontainer";
    public static String accountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    public static String azureServiceName = "img-to-resize";

    public static void main(String[] args) throws URISyntaxException, StorageException, IOException {
        AzureConfig queueConfig = new AzureConfig(accountName , accountKey , azureServiceName);
        AzureConfig containerConfig = new AzureConfig(accountName , accountKey , azureServiceName);
        ResizeQueue resizeQueue = new ResizeQueue(queueConfig , containerConfig);
        resizeQueue.queue.addMessageToQueue("fifa17.jpg");
        resizeQueue.run();
    }
}
