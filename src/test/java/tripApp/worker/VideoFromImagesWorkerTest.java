package tripApp.worker;

import com.google.gson.Gson;
import tripApp.config.AzureConfig;
import tripApp.worker.presentation.VideoFromImagesWorker;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mr on 5/8/17.
 */
public class VideoFromImagesWorkerTest {

    public static String accountName = "tripcontainer";
    public static String accountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
    public static String azureServiceName = "img-to-resize";

    public static OurMessage message = new OurMessage();

    public static void main(String[] args) throws Exception {
        VideoFromImagesWorker worker = new VideoFromImagesWorker(new AzureConfig(accountName, accountKey, azureServiceName));
        Gson gson = new Gson();
        String json = gson.toJson(message);
        String json2 = "{\"filesList\": [\"https://tripappdisks435.blob.core.windows.net/trip-media/bartek_wiktor_4B2ExIX.jpg\", \"https://tripappdisks435.blob.core.windows.net/trip-media/GH1rWAL_-_Imgur.jpg\", \"https://tripappdisks435.blob.core.windows.net/trip-media/rzesz.jpg\", \"https://tripappdisks435.blob.core.windows.net/trip-media/Caption-This_10.jpg\"], \"tripEndDate\": \"2017-05-09\", \"tripDescription\": \"rodzinny wyjazd\", \"tripName\": \"Narty w Alpach 2000\", \"tripStartDate\": \"2017-05-04\", \"correlationID\": \"dd9ba1f2-1f83-435d-9fee-404db7aec2c0\"}";
        worker.doWork(json2);
    }

    private static class OurMessage {
        private ArrayList<String> filesList;
        private Date tripEndDate;
        private String tripDescription;
        private String tripName;
        private Date tripStartDate;
        private String correlationID;

        private OurMessage()
        {
            this.filesList = new ArrayList<String>(){{
                add("https://tripappdisks435.blob.core.windows.net/trip-media/bartek_wiktor_4B2ExIX.jpg");
                add("https://tripappdisks435.blob.core.windows.net/trip-media/GH1rWAL_-_Imgur.jpg");
                add("https://tripappdisks435.blob.core.windows.net/trip-media/rzesz.jpg");
                add("https://tripappdisks435.blob.core.windows.net/trip-media/Caption-This_10.jpg");
            }};
            this.tripEndDate = new Date(2017-04-30);
            this.tripDescription = "trip";
            this.tripName = "trip 3";
            this.tripStartDate = new Date(2017-05-02);
            this.correlationID = "526f9aa4-8465-45b2-a5d2-70f091b15b32";
        }
    }
}
