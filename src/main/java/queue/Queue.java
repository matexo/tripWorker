package queue;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import config.AzureConfig;

/**
 * Created by Matexo on 2017-05-07.
 */
public class Queue {

    private CloudStorageAccount cloudStorageAccount;
    private CloudQueueClient cloudQueueClient;
    private CloudQueue cloudQueue;
    private Integer timeToProcessMessage = 60;

    public Queue(AzureConfig queueConfig) {
        try {
            cloudStorageAccount = CloudStorageAccount.parse(queueConfig.getConfig());
            cloudQueueClient = cloudStorageAccount.createCloudQueueClient();
            cloudQueue = cloudQueueClient.getQueueReference(queueConfig.getAzureServiceName());
        } catch (Exception e) {
            System.out.println(e.getMessage()); //dac logi czy cos
        }
    }

    public void addMessageToQueue(String message) {
        CloudQueueMessage cloudQueueMessage = new CloudQueueMessage(message);
        try {
            cloudQueue.addMessage(cloudQueueMessage);
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    public Long getLength() {
        try {
            cloudQueue.downloadAttributes();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return cloudQueue.getApproximateMessageCount();
    }

    public CloudQueueMessage getNextMessage() {
        CloudQueueMessage message = null;
        try {
            message = cloudQueue.retrieveMessage(timeToProcessMessage , null , null);
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return message;
    }

    public String getMessageAsString(CloudQueueMessage cloudQueueMessage) {
        String message = null;
        try {
            message = cloudQueueMessage.getMessageContentAsString();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void deleteMessageAfterProcess(CloudQueueMessage messageToDelete) {
        try {
            cloudQueue.deleteMessage(messageToDelete);
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    public Integer getTimeToProcessMessage() {
        return timeToProcessMessage;
    }

    public void setTimeToProcessMessage(Integer timeToProcessMessage) {
        this.timeToProcessMessage = timeToProcessMessage;
    }


}
