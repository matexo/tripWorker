package tripApp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tripApp.Main;
import tripApp.config.AzureConfig;
import tripApp.model.ThumbnailDTO;
import tripApp.worker.IWorker;
import tripApp.worker.thumbnail.ResizeWorker;
import tripApp.worker.thumbnail.ThumbnailSwitcher;
import tripApp.worker.thumbnail.VideoWorker;

/**
 * Created by Matexo on 2017-05-18.
 */
public class ThumbnailGeneratorTest {

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().create();
        AzureConfig azureConfig = new AzureConfig("tripcontainer" , "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==","img-to-resize");
        Queue generatorQueue = new Queue(azureConfig);
//        for(Integer i=1; i<10 ;i++) {
            ThumbnailDTO message = new ThumbnailDTO("1", "https://tripcontainer.blob.core.windows.net/img-to-resize/1.png");
            String json = gson.toJson(message);
            generatorQueue.addMessageToQueue(json);
//        }
//        System.out.println(generatorQueue.getLength());
//        IWorker worker = new ResizeWorker(azureConfig, Main.CONFIG.getThumbnailRespQueue());
//        QueueRunner queueRunner = new QueueRunner(azureConfig , worker);
//        queueRunner.run();


        IWorker resizeWorker = new ResizeWorker(azureConfig, Main.CONFIG.getThumbnailRespQueue());
        IWorker videoWorker = new VideoWorker(azureConfig , Main.CONFIG.getThumbnailRespQueue());
        IWorker thumbnailSwitcher = new ThumbnailSwitcher(resizeWorker , videoWorker);
        QueueRunner queueRunner = new QueueRunner(azureConfig, thumbnailSwitcher);
        Thread thumbThread = new Thread(queueRunner);
        thumbThread.start();
    }
}