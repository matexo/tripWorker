package tripApp.worker;

import tripApp.config.AzureConfig;
import tripApp.worker.presentation.VideoFromImagesWorker;

/**
 * Created by mr on 5/8/17.
 */
public class VideoFromImagesWorkerTest {

    public static String accountName = "tripcontainer";
    public static String accountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    public static String azureServiceName = "img-to-resize";

    public static void main(String[] args) throws Exception {
        VideoFromImagesWorker worker = new VideoFromImagesWorker(new AzureConfig(accountName, accountKey, azureServiceName));
        worker.doWork("fifa12.jpg;fifa13.jpg;fifa14.jpg;fifa15.jpg;fifa17.jpg");
    }
}
