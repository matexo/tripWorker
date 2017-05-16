package container;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.storage.StorageException;
import config.AzureConfig;
import model.PresentationDTO;
import model.Progress;
import model.ProgressDTO;
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
        Gson gson = new GsonBuilder().create();
        ProgressDTO progressDTO = new ProgressDTO(10 , Progress.WORKING , 123);
        String json = gson.toJson(progressDTO);
        System.out.println(json);

        PresentationDTO presentationDTO = new PresentationDTO(123 , "https://tripcontainer.blob.core.windows.net/img-to-resize/fifa12.jpg" , 100 , 200);
        json = gson.toJson(presentationDTO);
        System.out.println(json);

        AzureConfig containerConfig = new AzureConfig(accountName , accountKey , azureServiceName);
        IWorker worker = new ResizeWorker(containerConfig);
        worker.doWork(json);

    }
}
