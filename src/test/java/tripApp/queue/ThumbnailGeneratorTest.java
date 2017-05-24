package tripApp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tripApp.config.StaticConfig;
import tripApp.model.ThumbnailDTO;
import tripApp.worker.IWorker;
import tripApp.worker.thumbnail.ResizeWorker;

/**
 * Created by Matexo on 2017-05-18.
 */
public class ThumbnailGeneratorTest {

    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().create();
        Queue generatorQueue = new Queue(StaticConfig.getThumbnailGenQueueAzureConfig());
        for(Integer i=1; i<10 ;i++) {
            ThumbnailDTO message = new ThumbnailDTO(i.toString(), "https://tripappdisks435.blob.core.windows.net/trip-media/Caption-This_"+ i +".jpg");
            String json = gson.toJson(message);
            generatorQueue.addMessageToQueue(json);
        }
        System.out.println(generatorQueue.getLength());
        IWorker worker = new ResizeWorker(StaticConfig.getThumbnailBlobAzureConfig());
        QueueRunner queueRunner = new QueueRunner(StaticConfig.getThumbnailGenQueueAzureConfig() , worker);
        queueRunner.run();

    }


}
