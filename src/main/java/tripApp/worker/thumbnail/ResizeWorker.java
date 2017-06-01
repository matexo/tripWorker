package tripApp.worker.thumbnail;

import com.microsoft.azure.storage.StorageException;
import org.imgscalr.Scalr;
import tripApp.config.AzureConfig;
import tripApp.model.ErrorMessage;
import tripApp.model.ThumbnailDTO;
import tripApp.model.Status;
import tripApp.model.ProgressDTO;
import tripApp.worker.IWorker;
import tripApp.worker.Worker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matexo on 2017-05-06.
 */
public class ResizeWorker extends Worker implements IWorker {

    private static final Integer THUMBNAIL_Y_SIZE = 200;
    private static final String DEFAULT_FORMAT = "jpg";
    private static final String ADDITIONAL_FILE_NAME = "thumbnail.";

    public static final List<String> acceptableFormat = Arrays.asList("jpg", "png" , "jpeg"); // z kropka czy bez?

    //bardzo brzydka szpachla
    private final String HARDCODED_BASE_URL = "https://tripappdisks435.blob.core.windows.net/trip-media/";


    public ResizeWorker(AzureConfig blobConfig, AzureConfig respConfig)
            throws InvalidKeyException, StorageException, URISyntaxException {
        super(blobConfig, respConfig);
    }

    //Przyjmuje nazwe pliku do pobrania ewentualnie url w tym przypadku trzeba sparsowac
    public String doWork(String message) throws Exception {

        ThumbnailDTO thumbnailDTO = gson.fromJson(message, ThumbnailDTO.class);

        if (!validateMessage(thumbnailDTO)) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(0, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.VALIDATION_ERROR + " " + thumbnailDTO.toString());
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(0, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Validating message completed" + thumbnailDTO.toString());

        thumbnailDTO = parseUrl(thumbnailDTO);
        if (!validateFileInfo(thumbnailDTO)) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(20, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.URL_PARSING_ERROR + " " + thumbnailDTO.getFileUrl());
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(20, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Parsing and validating URL compleated" + thumbnailDTO.getCorrelationID());

        // pobierz obrazek z bloba
        ByteArrayOutputStream downloadedBlobItem;
        try {
            downloadedBlobItem = container.downloadBlobItem(thumbnailDTO.getFileName() + "." + thumbnailDTO.getFileFormat());
        } catch (Exception e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(40, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.DOWNLOADING_ERROR + " " + thumbnailDTO.getFileName() + thumbnailDTO.getFileFormat());
            return null;
        }


        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(40, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Downloading completed"  + thumbnailDTO.getCorrelationID());

        if (downloadedBlobItem == null)
            return null; //cos jeszcze??

        // przetworz obrazek (co sie bedzie dzialo jak nie znajdzie)
        BufferedImage bufferedImage;
        try {
            bufferedImage = Scalr.resize(ImageIO.read(new ByteArrayInputStream(downloadedBlobItem.toByteArray())), THUMBNAIL_Y_SIZE);
        } catch (IOException e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(60, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.RESIZING_ERROR + " " + THUMBNAIL_Y_SIZE);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(60, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Resizing completed"  + thumbnailDTO.getCorrelationID());

        // wrzuc obrazek do bloba
        String thumbnailName = thumbnailDTO.getFileName() + ADDITIONAL_FILE_NAME + thumbnailDTO.getFileFormat();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, thumbnailDTO.getFileFormat().toUpperCase(), byteArrayOutputStream);
        } catch (IOException e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(80, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.WRITING_ERROR);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(80, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Uploading completed" + thumbnailDTO.getCorrelationID());

        try {
            container.uploadBlobItem(thumbnailName, byteArrayOutputStream);
        } catch (Exception e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(90, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.UPLOADING_ERROR);
            return null;
        }

        ProgressDTO progressDTO = new ProgressDTO(100, Status.COMPLETED, thumbnailDTO.getCorrelationID());
        progressDTO.setContent(HARDCODED_BASE_URL + thumbnailName);
        progressQueue.addMessageToQueue(gson.toJson(progressDTO));
        logger.debug("Processed " + thumbnailDTO.getCorrelationID());

        return thumbnailName;
    }

    private boolean validateMessage(ThumbnailDTO thumbnailDTO) {
        return thumbnailDTO.getCorrelationID() != null
                && thumbnailDTO.getFileUrl() != null && thumbnailDTO.getFileUrl().length() > 0
                ;
    }

    private ThumbnailDTO parseUrl(ThumbnailDTO thumbnailDTO) {
        String url = thumbnailDTO.getFileUrl();
        // https://tripcontainer.blob.core.windows.net/img-to-resize/fifa12.jpg
        //szpachla
        String[] urlElem = url.split("/");
        String tmp[] = urlElem[urlElem.length - 1].split("\\.");
        String fileName = tmp[0];
        String fileFormat = tmp[1];
        //szpachla
        if (validateFormat(fileFormat)) {
            thumbnailDTO.setFileName(fileName);
            thumbnailDTO.setFileFormat(fileFormat);
        } else {
            logger.error("Wrong format of image");
        }
        return thumbnailDTO;
    }

    private boolean validateFormat(String format) {
        return acceptableFormat.contains(format.toLowerCase());
    }

    private boolean validateFileInfo(ThumbnailDTO thumbnailDTO) {
        return thumbnailDTO.getFileName() != null && thumbnailDTO.getFileFormat() != null;
    }
}
