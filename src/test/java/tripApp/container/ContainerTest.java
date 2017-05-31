package tripApp.container;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tripApp.Main;
import tripApp.model.ProgressDTO;
import tripApp.model.Status;
import tripApp.model.ThumbnailDTO;
import tripApp.worker.IWorker;
import tripApp.worker.thumbnail.ResizeWorker;

/**
 * Created by Matexo on 2017-05-04.
 */
public class ContainerTest {
    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().create();
        ProgressDTO progressDTO = new ProgressDTO(10, Status.WORKING, "123");
        String json = gson.toJson(progressDTO);
        System.out.println(json);

        ThumbnailDTO thumbnailDTO = new ThumbnailDTO("123", "https://tripcontainer.blob.core.windows.net/img-to-resize/fifa12.jpg");
        json = gson.toJson(thumbnailDTO);
        System.out.println(json);

        IWorker worker = new ResizeWorker(Main.CONFIG.getBlobConfig(), Main.CONFIG.getThumbnailRespQueue());
        worker.doWork(json);

    }
}
