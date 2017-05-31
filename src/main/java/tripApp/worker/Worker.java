package tripApp.worker;

import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageException;
import org.apache.log4j.Logger;
import tripApp.config.AzureConfig;
import tripApp.container.Container;
import tripApp.queue.Queue;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-06.
 */
public abstract class Worker {
    public Container container;
    public Queue progressQueue;
    public Gson gson;
    public static final Logger logger = Logger.getLogger(Worker.class);

    public Worker(AzureConfig blobConfig, AzureConfig respQueueConfig)
            throws InvalidKeyException, StorageException, URISyntaxException {
        container = new Container(blobConfig);
        progressQueue = new Queue(respQueueConfig);
        gson = new Gson();
    }
}
