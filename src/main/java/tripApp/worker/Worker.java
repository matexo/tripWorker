package tripApp.worker;

import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageException;
import tripApp.config.AzureConfig;
import tripApp.container.Container;
import tripApp.queue.Queue;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-06.
 */
public class Worker {
    //TODO kolejka po stronie backenu
    private static final String progressQueueAccountName = "tripcontainer";
    private static final String progressQueueAccountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    private static final String progressQueueServiceName = "progressqueue";
    // NIE RUSZAC DLA KAZDEGO WORKERA TAKIE SAME QUEUE

    public Container container;
    public Queue progressQueue;
    public Gson gson;

    public Worker(AzureConfig azureConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        container = new Container(azureConfig);
        progressQueue = new Queue(new AzureConfig(progressQueueAccountName,progressQueueAccountKey,progressQueueServiceName));
        gson = new Gson();
    }
}
