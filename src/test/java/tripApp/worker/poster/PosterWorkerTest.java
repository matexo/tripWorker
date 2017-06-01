package tripApp.worker.poster;

import com.microsoft.azure.storage.StorageException;
import org.junit.Test;
import tripApp.Main;
import tripApp.model.Point;
import tripApp.model.PosterDTO;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Arrays;

/**
 * Created by martynawisniewska on 30.05.2017.
 */
public class PosterWorkerTest {

    @Test
    public void posterGenerationTest() {
        PosterDTO posterData = new PosterDTO();
        posterData.blobsNames = Arrays.asList("68411628-satellite-wallpapers_1TitgQathumbnail.jpeg",
                "bartek_wiktor_2_sNRw8Vuthumbnail.jpg",
                "Caption-This_12thumbnail.jpg",
                "GH1rWAL_-_Imgur_NviiCl2thumbnail.jpg",
                "received_1148491218515534thumbnail.jpeg",
                "68411628-satellite-wallpapers_smilWEVthumbnail.jpeg");
        posterData.coordinates = Arrays.asList(new Point(52.2355063,21.0001000),
                    new Point(52.2399063,21.0001000),
                    new Point(52.2309063,21.0000000),
                    new Point(52.2300063,21.0020000),
                    new Point(52.2179529,20.9819435),
                    new Point(52.2159529,20.0019435)
                );
        posterData.correlationID = "aba2";
        posterData.tripName = "Super wycieczka";
        posterData.posterName = posterData.correlationID + "poster.jpg";
        try {
            PosterWorker worker = new PosterWorker(Main.CONFIG.getBlobConfig(), Main.CONFIG.getPosterRespQueue());
            worker.setPosterData(posterData);
            worker.doWork("aaa");
        } catch (InvalidKeyException | StorageException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
