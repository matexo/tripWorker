package tripApp.container;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tripApp.config.AzureConfig;
import tripApp.model.PresentationDTO;
import tripApp.model.Progress;
import tripApp.model.ProgressDTO;
import tripApp.worker.IWorker;
import tripApp.worker.thumbnail.ResizeWorker;

/**
 * Created by Matexo on 2017-05-04.
 */
public class ContainerTest {
    public static String accountName = "tripcontainer";
    public static String accountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    public static String azureServiceName = "img-to-resize";

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().create();
        ProgressDTO progressDTO = new ProgressDTO(10, Progress.WORKING, "123");
        String json = gson.toJson(progressDTO);
        System.out.println(json);

        PresentationDTO presentationDTO = new PresentationDTO("123", "https://tripcontainer.blob.core.windows.net/img-to-resize/fifa12.jpg", 100, 200);
        json = gson.toJson(presentationDTO);
        System.out.println(json);

        AzureConfig containerConfig = new AzureConfig(accountName, accountKey, azureServiceName);
        IWorker worker = new ResizeWorker(containerConfig);
        worker.doWork(json);

    }
}
