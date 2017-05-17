package tripApp.queue;

import com.microsoft.azure.storage.StorageException;
import tripApp.config.AzureConfig;
import tripApp.worker.IWorker;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-07.
 */
public class QueueBody {
    //TODO kolejka po stronie backenu
    private static final String progressQueueAccountName = "tripcontainer";
    private static final String progressQueueAccountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    private static final String progressQueueServiceName = "progressqueue";
    // NIE RUSZAC DLA KAZDEGO WORKERA TAKIE SAME QUEUE

    public Queue queue;
    public Queue progressQueue;
    public IWorker worker;

    public QueueBody(AzureConfig azureConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        this.queue = new Queue(azureConfig);
        this.progressQueue = new Queue(new AzureConfig(progressQueueAccountName, progressQueueAccountKey,
                progressQueueServiceName));
    }
}
