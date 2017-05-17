package tripApp.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.log4j.Logger;
import tripApp.config.AzureConfig;
import tripApp.worker.thumbnail.ResizeWorker;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-07.
 */
public class ResizeQueue extends QueueBody implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ResizeQueue.class);
    private static final Integer SLEEP_TIME_IN_MS = 10000;

    private boolean isRunning = true;

    public ResizeQueue(AzureConfig queueWithRequests, AzureConfig containerConfig)
            throws InvalidKeyException, StorageException, URISyntaxException {
        super(queueWithRequests);
        this.worker = new ResizeWorker(containerConfig);
    }

    public void run() {
        while (isRunning) {
            try {
                Long lenghtOfQueue = queue.getLength();
                if (lenghtOfQueue > 0) {
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
