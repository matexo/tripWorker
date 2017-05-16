package queue;

import com.google.gson.Gson;
import config.AzureConfig;
import worker.IWorker;

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
    public Gson gson;

    public  QueueBody(AzureConfig azureConfig) {
        this.queue = new Queue(azureConfig);
        this.progressQueue = new Queue(new AzureConfig(progressQueueAccountName, progressQueueAccountKey , progressQueueServiceName));
    }
}
