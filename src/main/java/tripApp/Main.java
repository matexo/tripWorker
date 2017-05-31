package tripApp;

import org.apache.log4j.Logger;
import tripApp.config.WorkersConfig;
import tripApp.queue.QueueRunner;
import tripApp.worker.IWorker;
import tripApp.worker.presentation.VideoFromImagesWorker;
import tripApp.worker.thumbnail.ResizeWorker;

/**
 * Created by piotr on 17.05.17.
 */
public class Main {
    public static final WorkersConfig CONFIG = new WorkersConfig();
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            IWorker worker = new ResizeWorker(CONFIG.getBlobConfig(), CONFIG.getThumbnailRespQueue());
            QueueRunner queueRunner = new QueueRunner(CONFIG.getThumbnailGenQueue(), worker);
            new Thread(queueRunner).start();

            IWorker videoFromImagesWorker
                    = new VideoFromImagesWorker(CONFIG.getBlobConfig(), CONFIG.getPresentationRespQueue());
            QueueRunner presentationQueueRunner
                    = new QueueRunner(CONFIG.getPresentationGenQueue(), videoFromImagesWorker);
            new Thread(presentationQueueRunner).start();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }
}
