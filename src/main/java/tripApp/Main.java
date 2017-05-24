package tripApp;

import com.microsoft.azure.storage.StorageException;
import tripApp.config.StaticConfig;
import tripApp.queue.QueueRunner;
import tripApp.worker.IWorker;
import tripApp.worker.thumbnail.ResizeWorker;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by piotr on 17.05.17.
 */
public class Main {
    public static void main(String[] args) throws InvalidKeyException, StorageException, URISyntaxException {

        IWorker worker = new ResizeWorker(StaticConfig.getThumbnailBlobAzureConfig());
        QueueRunner queueRunner = new QueueRunner(StaticConfig.getThumbnailGenQueueAzureConfig() , worker);
        queueRunner.run();
    }
}
