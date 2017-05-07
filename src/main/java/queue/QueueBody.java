package queue;

import config.AzureConfig;
import worker.IWorker;

/**
 * Created by Matexo on 2017-05-07.
 */
public class QueueBody {

    public Queue queue;
    public IWorker worker;

    public  QueueBody(AzureConfig azureConfig) {
        this.queue = new Queue(azureConfig);
    }
}
