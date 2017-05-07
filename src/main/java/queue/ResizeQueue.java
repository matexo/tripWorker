package queue;

import com.microsoft.azure.storage.queue.CloudQueueMessage;
import config.AzureConfig;
import worker.ResizeWorker;

/**
 * Created by Matexo on 2017-05-07.
 */
public class ResizeQueue extends QueueBody implements Runnable {

    private final Integer SLEEP_TIME_IN_MS = 10000;

    public ResizeQueue(AzureConfig queueConfig, AzureConfig containerConfig) {
        super(queueConfig);
        this.worker = new ResizeWorker(containerConfig);
    }

    public void run() {
    while(true) {
        Long lenghtOfQueue = queue.getLength();
        if(lenghtOfQueue > 0) { //w przypadku gdy pobierzemy wiadomosc jest ona blokowana na okres czasu podany w kolejce
            // pobranie dlugosci kolejki w takim wypadku pokazuje niezerowe ale nie jest pobierana nowa wiadomosc
            CloudQueueMessage cloudQueueMessage = queue.getNextMessage();
            if(cloudQueueMessage != null) {
                String message = queue.getMessageAsString(cloudQueueMessage);
                if(message != null && message.length() > 0) {
                    String processedItemName = worker.doWork(message);
                    queue.deleteMessageAfterProcess(cloudQueueMessage);
                    // wysylac cos backendowi????
                }
            }
        } else {
            try {
                Thread.sleep(SLEEP_TIME_IN_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    }
}
