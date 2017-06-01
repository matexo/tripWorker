package tripApp;

import org.apache.log4j.Logger;
import tripApp.config.WorkersConfig;
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
