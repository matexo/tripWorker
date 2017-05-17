package tripApp.worker;

import com.google.gson.Gson;
import com.microsoft.azure.storage.StorageException;
import tripApp.config.AzureConfig;
import tripApp.config.StaticConfig;
import tripApp.container.Container;
import tripApp.queue.Queue;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-06.
 */
public class Worker {
    public Container container;
    public Queue progressQueue;
    public Gson gson;

    public Worker(AzureConfig azureConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        container = new Container(azureConfig);
        progressQueue = new Queue(StaticConfig.getProgressResponseAzureConfig());
        gson = new Gson();
    }
}
