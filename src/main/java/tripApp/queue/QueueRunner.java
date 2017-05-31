package tripApp.queue;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.log4j.Logger;
import tripApp.config.AzureConfig;
import tripApp.model.ErrorMessage;
import tripApp.worker.IWorker;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;


/**
 * Created by Matexo on 2017-05-24.
 */
public class QueueRunner implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(QueueRunner.class);
    private static final Integer SLEEP_TIME_IN_MS = 10000;

    public Queue queue;
    public IWorker worker;
    private boolean isRunning = true;

    public QueueRunner(AzureConfig queueConfig, IWorker worker) throws InvalidKeyException, StorageException, URISyntaxException {
        this.queue = new Queue(queueConfig);
        this.worker = worker;
    }

    public QueueRunner(AzureConfig queueConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        this.queue = new Queue(queueConfig);
    }

    public void setWorker(IWorker worker) {
        this.worker = worker;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void run() {
        if (worker == null) {
            LOGGER.error(ErrorMessage.WORKER_NOT_SELECTED);
        } else {
            LOGGER.info("QueueRunner started");
            while (isRunning) {
                try {
                    CloudQueueMessage cloudQueueMessage = queue.getNextMessage();
                    if (cloudQueueMessage != null) {
                        String message = queue.getMessageAsString(cloudQueueMessage);
                        if (message != null && message.length() > 0) {
                            LOGGER.info("Getting message: " + message);
                            String result = worker.doWork(message);
                            if (result != null) {
                                queue.deleteMessageAfterProcess(cloudQueueMessage);
                                LOGGER.info("Processed message. Result: " + result);
                            } else
                                LOGGER.error(ErrorMessage.QUEUERUNNER_ERROR + message);
                        }
                    } else Thread.sleep(SLEEP_TIME_IN_MS);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            isRunning = false;
        }
    }

}

