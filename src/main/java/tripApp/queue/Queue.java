package tripApp.queue;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import tripApp.config.AzureConfig;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-07.
 */
public class Queue {

    private CloudStorageAccount cloudStorageAccount;
    private CloudQueueClient cloudQueueClient;
    private CloudQueue cloudQueue;
    private Integer timeToProcessMessage = 60;

    public Queue(AzureConfig queueConfig) throws URISyntaxException, InvalidKeyException, StorageException {
        cloudStorageAccount = CloudStorageAccount.parse(queueConfig.getConfig());
        cloudQueueClient = cloudStorageAccount.createCloudQueueClient();
        cloudQueue = cloudQueueClient.getQueueReference(queueConfig.getAzureServiceName());
    }

    public void addMessageToQueue(String message) throws StorageException {
        CloudQueueMessage cloudQueueMessage = new CloudQueueMessage(message);
        cloudQueue.addMessage(cloudQueueMessage);
    }

    public Long getLength() throws StorageException {
        cloudQueue.downloadAttributes();
        return cloudQueue.getApproximateMessageCount();
    }

    public CloudQueueMessage getNextMessage() throws StorageException {
        return cloudQueue.retrieveMessage(timeToProcessMessage, null, null);
    }

    public String getMessageAsString(CloudQueueMessage cloudQueueMessage) throws StorageException {
        return cloudQueueMessage.getMessageContentAsString();
    }

    public void deleteMessageAfterProcess(CloudQueueMessage messageToDelete) throws StorageException {
        cloudQueue.deleteMessage(messageToDelete);
    }

    public Integer getTimeToProcessMessage() {
        return timeToProcessMessage;
    }

    public void setTimeToProcessMessage(Integer timeToProcessMessage) {
        this.timeToProcessMessage = timeToProcessMessage;
    }


}
