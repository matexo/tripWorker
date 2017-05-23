package tripApp.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.log4j.Logger;
import tripApp.config.AzureConfig;
import tripApp.worker.poster.PosterWorker;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by martynawisniewska on 23.05.2017.
 */

public class PosterQueue extends QueueBody implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(PosterQueue.class);
    private static final Integer SLEEP_TIME_IN_MS = 10000;

    private boolean isRunning = true;

    public PosterQueue(AzureConfig queueWithRequests, AzureConfig containerConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        super(queueWithRequests);
        this.worker = new PosterWorker(containerConfig);
    }

    public void run() {
        while (isRunning) {
            try {
                if (queue.getLength() > 0) {
                    //w przypadku gdy pobierzemy wiadomosc jest ona blokowana na okres czasu podany w kolejce
                    // pobranie dlugosci kolejki w takim wypadku pokazuje niezerowe ale nie jest pobierana nowa wiadomosc
                    CloudQueueMessage cloudQueueMessage = queue.getNextMessage();
                    if (cloudQueueMessage != null) {
                        String message = queue.getMessageAsString(cloudQueueMessage);
                        if (message != null && message.length() > 0) {
                            worker.doWork(message);
                            queue.deleteMessageAfterProcess(cloudQueueMessage);
                        }
                    }
                } else {
                    Thread.sleep(SLEEP_TIME_IN_MS);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
