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
        posterData.filesList = Arrays.asList("https://tripappdisks435.blob.core.windows.net/trip-media/68411628-satellite-wallpapers_1TitgQathumbnail.jpeg",
                "https://tripappdisks435.blob.core.windows.net/trip-media/bartek_wiktor_2_sNRw8Vuthumbnail.jpg",
                "https://tripappdisks435.blob.core.windows.net/trip-media/Caption-This_12thumbnail.jpg",
                "https://tripappdisks435.blob.core.windows.net/trip-media/GH1rWAL_-_Imgur_NviiCl2thumbnail.jpg",
                "https://tripappdisks435.blob.core.windows.net/trip-media/received_1148491218515534thumbnail.jpeg",
                "https://tripappdisks435.blob.core.windows.net/trip-media/68411628-satellite-wallpapers_smilWEVthumbnail.jpeg");
        posterData.coordinates = Arrays.asList(new Point(52.2355063,21.0001000),
                    new Point(52.2399063,21.0001000),
                    new Point(52.2309063,21.0000000),
                    new Point(52.2300063,21.0020000),
                    new Point(52.2179529,20.9819435),
                    new Point(52.2159529,20.0019435)
                );
        posterData.correlationID = "aba05062017";
        posterData.tripName = "Superwycieczka2017";
        posterData.posterName = posterData.correlationID + "poster.jpg";
        try {
            PosterWorker worker = new PosterWorker(Main.CONFIG.getBlobConfig(), Main.CONFIG.getPosterRespQueue());
            worker.setPosterData(posterData);
            worker.doWork("{\"tripName\": \"Nowa\", \"coordinates\": [{\"latitude\": \"38.674882000\", \"longtitude\": \"-9.230961000\"}, {\"latitude\": \"38.673437000\", \"longtitude\": \"-9.233377000\"}, {\"latitude\": \"38.671723000\", \"longtitude\": \"-9.235775000\"}, {\"latitude\": \"38.669342000\", \"longtitude\": \"-9.238449000\"}, {\"latitude\": \"38.669202000\", \"longtitude\": \"-9.241609000\"}, {\"latitude\": \"38.669607000\", \"longtitude\": \"-9.244100000\"}, {\"latitude\": \"38.667785000\", \"longtitude\": \"-9.246167000\"}, {\"latitude\": \"38.667580000\", \"longtitude\": \"-9.235982000\"}, {\"latitude\": \"38.667522000\", \"longtitude\": \"-9.247108000\"}, {\"latitude\": \"38.665314000\", \"longtitude\": \"-9.246243000\"}, {\"latitude\": \"38.662967000\", \"longtitude\": \"-9.244441000\"}, {\"latitude\": \"38.663346000\", \"longtitude\": \"-9.243158000\"}, {\"latitude\": \"38.664657000\", \"longtitude\": \"-9.241614000\"}, {\"latitude\": \"38.665519000\", \"longtitude\": \"-9.239674000\"}, {\"latitude\": \"38.664899000\", \"longtitude\": \"-9.237151000\"}, {\"latitude\": \"38.665597000\", \"longtitude\": \"-9.235745000\"}, {\"latitude\": \"38.710063000\", \"longtitude\": \"-9.128068000\"}, {\"latitude\": \"38.712006000\", \"longtitude\": \"-9.124278000\"}, {\"latitude\": \"38.706983000\", \"longtitude\": \"-9.134699000\"}, {\"latitude\": \"38.708204000\", \"longtitude\": \"-9.136983000\"}, {\"latitude\": \"38.706603000\", \"longtitude\": \"-9.142192000\"}, {\"latitude\": \"38.706227000\", \"longtitude\": \"-9.156345000\"}, {\"latitude\": \"38.704805000\", \"longtitude\": \"-9.175648000\"}, {\"latitude\": \"38.698484000\", \"longtitude\": \"-9.188368000\"}, {\"latitude\": \"38.696087000\", \"longtitude\": \"-9.198629000\"}, {\"latitude\": \"38.694410000\", \"longtitude\": \"-9.198295000\"}, {\"latitude\": \"38.690039000\", \"longtitude\": \"-9.198903000\"}, {\"latitude\": \"38.681693000\", \"longtitude\": \"-9.206373000\"}, {\"latitude\": \"38.678253000\", \"longtitude\": \"-9.206587000\"}, {\"latitude\": \"38.677655000\", \"longtitude\": \"-9.216050000\"}, {\"latitude\": \"38.668023000\", \"longtitude\": \"-9.233662000\"}, {\"latitude\": \"38.670304000\", \"longtitude\": \"-9.231818000\"}, {\"latitude\": \"38.672796000\", \"longtitude\": \"-9.231164000\"}, {\"latitude\": \"38.674467000\", \"longtitude\": \"-9.230861000\"}, {\"latitude\": \"38.677662000\", \"longtitude\": \"-9.228195000\"}, {\"latitude\": \"38.678346000\", \"longtitude\": \"-9.206046000\"}, {\"latitude\": \"38.692399000\", \"longtitude\": \"-9.196071000\"}, {\"latitude\": \"38.694546000\", \"longtitude\": \"-9.200064000\"}, {\"latitude\": \"38.695000000\", \"longtitude\": \"-9.203429000\"}, {\"latitude\": \"38.693710000\", \"longtitude\": \"-9.205964000\"}, {\"latitude\": \"38.693037000\", \"longtitude\": \"-9.208586000\"}, {\"latitude\": \"38.693785000\", \"longtitude\": \"-9.210970000\"}, {\"latitude\": \"38.692505000\", \"longtitude\": \"-9.214520000\"}, {\"latitude\": \"38.691984000\", \"longtitude\": \"-9.216358000\"}, {\"latitude\": \"38.692984000\", \"longtitude\": \"-9.214294000\"}, {\"latitude\": \"38.694187000\", \"longtitude\": \"-9.212439000\"}, {\"latitude\": \"38.694659000\", \"longtitude\": \"-9.208417000\"}, {\"latitude\": \"38.695053000\", \"longtitude\": \"-9.206982000\"}, {\"latitude\": \"38.696892000\", \"longtitude\": \"-9.205508000\"}, {\"latitude\": \"38.697439000\", \"longtitude\": \"-9.203459000\"}, {\"latitude\": \"38.697345000\", \"longtitude\": \"-9.203594000\"}, {\"latitude\": \"38.697619000\", \"longtitude\": \"-9.191383000\"}, {\"latitude\": \"38.703738000\", \"longtitude\": \"-9.178939000\"}, {\"latitude\": \"38.706225000\", \"longtitude\": \"-9.145399000\"}, {\"latitude\": \"38.736117000\", \"longtitude\": \"-9.154192000\"}, {\"latitude\": \"38.737176000\", \"longtitude\": \"-9.153122000\"}, {\"latitude\": \"38.736592000\", \"longtitude\": \"-9.153263000\"}, {\"latitude\": \"38.735137000\", \"longtitude\": \"-9.154516000\"}, {\"latitude\": \"38.731828000\", \"longtitude\": \"-9.153146000\"}, {\"latitude\": \"38.730298000\", \"longtitude\": \"-9.154729000\"}, {\"latitude\": \"38.728429000\", \"longtitude\": \"-9.153375000\"}, {\"latitude\": \"38.726953000\", \"longtitude\": \"-9.152095000\"}, {\"latitude\": \"38.724690000\", \"longtitude\": \"-9.150488000\"}, {\"latitude\": \"38.714456000\", \"longtitude\": \"-9.140336000\"}, {\"latitude\": \"38.712177000\", \"longtitude\": \"-9.139000000\"}, {\"latitude\": \"38.712649000\", \"longtitude\": \"-9.136714000\"}, {\"latitude\": \"38.715123000\", \"longtitude\": \"-9.136002000\"}, {\"latitude\": \"38.715129000\", \"longtitude\": \"-9.134864000\"}, {\"latitude\": \"38.714829000\", \"longtitude\": \"-9.134088000\"}, {\"latitude\": \"38.714155000\", \"longtitude\": \"-9.130821000\"}, {\"latitude\": \"38.712419000\", \"longtitude\": \"-9.130392000\"}, {\"latitude\": \"38.711033000\", \"longtitude\": \"-9.129403000\"}, {\"latitude\": \"38.710418000\", \"longtitude\": \"-9.128629000\"}, {\"latitude\": \"38.712130000\", \"longtitude\": \"-9.125708000\"}, {\"latitude\": \"38.713045000\", \"longtitude\": \"-9.123257000\"}, {\"latitude\": \"38.715333000\", \"longtitude\": \"-9.121541000\"}], \"tripEndDate\": \"2017-06-14\", \"correlationID\": \"7d55b90b-192c-4051-872a-eea298849fb3\", \"filesList\": [\"https://tripappdisks435.blob.core.windows.net/trip-media/14968555076651895876845.jpg\"], \"tripDescription\": \"Jdndndnnd\", \"tripStartDate\": \"2017-06-07\"}");
        } catch (InvalidKeyException | StorageException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
