package container;

import com.microsoft.azure.storage.StorageException;
import config.AzureConfig;
import worker.IWorker;
import worker.ResizeWorker;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Matexo on 2017-05-04.
 */
public class ContainerTest {
    public static String accountName = "tripcontainer";
    public static String accountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    public static String azureServiceName = "img-to-resize";

    public static void main(String[] args) throws URISyntaxException, StorageException, IOException {
        AzureConfig containerConfig = new AzureConfig(accountName , accountKey , "img-to-resize");
        IWorker worker = new ResizeWorker(containerConfig);
        worker.doWork("fifa17.jpg");
    }
}
