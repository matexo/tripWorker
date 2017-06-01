package tripApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import tripApp.config.AzureConfig;
import tripApp.config.WorkersConfig;
import tripApp.model.ThumbnailDTO;
import tripApp.queue.Queue;
import tripApp.queue.QueueRunner;
import tripApp.worker.IWorker;
import tripApp.worker.presentation.VideoFromImagesWorker;
import tripApp.worker.thumbnail.ResizeWorker;
import tripApp.worker.poster.*;
import tripApp.worker.thumbnail.ThumbnailSwitcher;
import tripApp.worker.thumbnail.VideoWorker;

/**
 * Created by piotr on 17.05.17.
 */
public class Main {
    public static final WorkersConfig CONFIG = new WorkersConfig();
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
//            Gson gson = new GsonBuilder().create();
//            AzureConfig azureConfig = new AzureConfig(CONFIG.getThumbnailGenQueue());
//            Queue generatorQueue = new Queue(CONFIG.getPosterGenQueue());
//        for(Integer i=1; i<10 ;i++) {
//            ThumbnailDTO message = new ThumbnailDTO("1", "https://tripappdisks435.blob.core.windows.net/trip-media/Item%20name2.avi");
//            String json = gson.toJson(message);
//            String msg =""
//            generatorQueue.addMessageToQueue(json);
//        }1

            IWorker resizeWorker = new ResizeWorker(CONFIG.getBlobConfig(), CONFIG.getThumbnailRespQueue());
            IWorker videoWorker = new VideoWorker(CONFIG.getBlobConfig() , CONFIG.getThumbnailRespQueue());
            IWorker thumbnailSwitcher = new ThumbnailSwitcher(resizeWorker , videoWorker);
            QueueRunner queueRunner = new QueueRunner(CONFIG.getThumbnailGenQueue(), thumbnailSwitcher);
            Thread thumbThread = new Thread(queueRunner);
	    thumbThread.start();

            IWorker videoFromImagesWorker
                    = new VideoFromImagesWorker(CONFIG.getBlobConfig(), CONFIG.getPresentationRespQueue());
            QueueRunner presentationQueueRunner
                    = new QueueRunner(CONFIG.getPresentationGenQueue(), videoFromImagesWorker);
            Thread presThread = new Thread(presentationQueueRunner);
            presThread.start();

	    IWorker posterWorker = new PosterWorker(CONFIG.getBlobConfig(), CONFIG.getPosterRespQueue());
	    QueueRunner posterRunner = new QueueRunner(CONFIG.getPosterGenQueue(), posterWorker);
	    Thread posterThread = new Thread(posterRunner);
	    posterThread.start();

	    thumbThread.join();
	    presThread.join();
	    posterThread.join();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }
}
