package tripApp;

import org.apache.log4j.Logger;
import tripApp.config.StaticConfig;
import tripApp.queue.QueueRunner;
import tripApp.worker.IWorker;
import tripApp.worker.presentation.VideoFromImagesWorker;
import tripApp.worker.thumbnail.ResizeWorker;

/**
 * Created by piotr on 17.05.17.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            IWorker worker = new ResizeWorker(StaticConfig.getThumbnailBlobAzureConfig());
            QueueRunner queueRunner = new QueueRunner(StaticConfig.getThumbnailGenQueueAzureConfig(), worker);
            new Thread(queueRunner).start();

            IWorker videoFromImagesWorker = new VideoFromImagesWorker(StaticConfig.getThumbnailBlobAzureConfig());
            QueueRunner presentationQueueRunner =
                    new QueueRunner(StaticConfig.getPresentationGenQueueAzureConfig(), videoFromImagesWorker);
            new Thread(presentationQueueRunner).start();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }
}
