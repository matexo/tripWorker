package tripApp;

import com.microsoft.azure.storage.StorageException;
import tripApp.config.StaticConfig;
import tripApp.queue.ResizeQueue;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by piotr on 17.05.17.
 */
public class Main {
    public static void main(String[] args) throws InvalidKeyException, StorageException, URISyntaxException {

        //Thread resizeThread = new Thread(new ResizeQueue())
        ResizeQueue resizeQueue = new ResizeQueue(StaticConfig.getThumbnailGenQueueAzureConfig() , StaticConfig.getThumbnailBlobAzureConfig());
        resizeQueue.run();
    }
}
