package tripApp.worker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tripApp.Main;
import tripApp.model.ThumbnailDTO;
import tripApp.queue.Queue;
import tripApp.queue.QueueRunner;
import tripApp.worker.thumbnail.VideoWorker;

/**
 * Created by terefere on 2017-05-25.
 */
public class VideoThumbnailGeneratorTest{

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().create();
        Queue generatorQueue = new Queue(Main.CONFIG.getThumbnailGenQueue());
        ThumbnailDTO message = new ThumbnailDTO("123321", "https://tripappdisks435.blob.core.windows.net/trip-media/trip-media/drop.avi");
        String json = gson.toJson(message);
        generatorQueue.addMessageToQueue(json);
        IWorker worker = new VideoWorker(Main.CONFIG.getBlobConfig(), Main.CONFIG.getThumbnailRespQueue());
        QueueRunner queueRunner = new QueueRunner(Main.CONFIG.getThumbnailGenQueue() , worker);
        queueRunner.run();
    }
}