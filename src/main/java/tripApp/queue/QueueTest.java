package tripApp.queue;

import tripApp.config.AzureConfig;

/**
 * Created by Matexo on 2017-05-07.
 */
public class QueueTest {
    public static String accountName = "tripappdisks435";
    public static String accountKey = "1nXbjOtmxm1sX3z6sPVGpDOUCfrrMTJpQ6NWc59VbyzmATdbrFUWE5uXMbk6DTJ9GUmH36590thxOKWxoXLNkA==";
    public static String queueServiceName = "thumbnailgen";
    public static String containerServiceName = "trip-media";

    public static void main(String[] args) throws Exception {
        AzureConfig queueConfig = new AzureConfig(accountName , accountKey , queueServiceName);
        AzureConfig containerConfig = new AzureConfig(accountName , accountKey , containerServiceName);
        ResizeQueue resizeQueue = new ResizeQueue(queueConfig , containerConfig);
        resizeQueue.run();
    }
}
