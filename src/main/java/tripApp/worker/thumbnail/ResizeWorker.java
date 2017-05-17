package tripApp.worker.thumbnail;

import com.microsoft.azure.storage.StorageException;
import org.imgscalr.Scalr;
import tripApp.config.AzureConfig;
import tripApp.model.ErrorMessage;
import tripApp.model.PresentationDTO;
import tripApp.model.Progress;
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

    private final List<String> acceptableFormat = Arrays.asList("jpg", "png" , "jpeg"); // z kropka czy bez?

    //bardzo brzydka szpachla
    private final String HARDCODED_BASE_URL = "https://tripappdisks435.blob.core.windows.net/trip-media/";


    public ResizeWorker(AzureConfig azureConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        super(azureConfig);
    }

    //Przyjmuje nazwe pliku do pobrania ewentualnie url w tym przypadku trzeba sparsowac
    public String doWork(String message) throws StorageException {

        PresentationDTO presentationDTO = gson.fromJson(message, PresentationDTO.class);

        if (!validateMessage(presentationDTO)) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(0, Progress.ERROR, presentationDTO.getCorrelationID())));
            System.out.println(ErrorMessage.VALIDATION_ERROR + " " + presentationDTO.toString());
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(0, Progress.WORKING, presentationDTO.getCorrelationID())));

        presentationDTO = parseUrl(presentationDTO);
        if (!validateFileInfo(presentationDTO)) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(20, Progress.ERROR, presentationDTO.getCorrelationID())));
            System.out.println(ErrorMessage.URL_PARSING_ERROR + " " + presentationDTO.getFileUrl());
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(20, Progress.WORKING, presentationDTO.getCorrelationID())));

        // pobierz obrazek z bloba
        ByteArrayOutputStream downloadedBlobItem;
        try {
            downloadedBlobItem = container.downloadBlobItem(presentationDTO.getFileName() + "." + presentationDTO.getFileFormat());
        } catch (Exception e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(40, Progress.ERROR, presentationDTO.getCorrelationID())));
            System.out.println(ErrorMessage.DOWNLOADING_ERROR + " " + presentationDTO.getFileName() + presentationDTO.getFileFormat());
            return null;
        }


        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(40, Progress.WORKING, presentationDTO.getCorrelationID())));


        if (downloadedBlobItem == null)
            return null; //cos jeszcze??

        // przetworz obrazek (co sie bedzie dzialo jak nie znajdzie)
        BufferedImage bufferedImage;
        try {
            bufferedImage = Scalr.resize(ImageIO.read(new ByteArrayInputStream(downloadedBlobItem.toByteArray())), THUMBNAIL_Y_SIZE);
        } catch (IOException e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(60, Progress.ERROR, presentationDTO.getCorrelationID())));
            System.out.println(ErrorMessage.RESIZING_ERROR + " " + THUMBNAIL_Y_SIZE);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(60, Progress.WORKING, presentationDTO.getCorrelationID())));


        // wrzuc obrazek do bloba
        String thumbnailName = presentationDTO.getFileName() + ADDITIONAL_FILE_NAME + presentationDTO.getFileFormat();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, DEFAULT_FORMAT, byteArrayOutputStream);
        } catch (IOException e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(80, Progress.ERROR, presentationDTO.getCorrelationID())));
            System.out.println(ErrorMessage.WRITING_ERROR);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(80, Progress.WORKING, presentationDTO.getCorrelationID())));


        try {
            container.uploadBlobItem(thumbnailName, byteArrayOutputStream);
        } catch (Exception e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(90, Progress.ERROR, presentationDTO.getCorrelationID())));
            System.out.println(ErrorMessage.UPLOADING_ERROR);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(100, Progress.SUCCESS, presentationDTO.getCorrelationID())));

        progressQueue.addMessageToQueue(gson.toJson(new PresentationDTO(presentationDTO.getCorrelationID(), HARDCODED_BASE_URL + thumbnailName, null, null)));

        return thumbnailName;
    }

    private boolean validateMessage(PresentationDTO presentationDTO) {
        return presentationDTO.getCorrelationID() != null
                && presentationDTO.getFileUrl() != null && presentationDTO.getFileUrl().length() > 0
//                && presentationDTO.getSizeX() != null && presentationDTO.getSizeY() != null
                ;
    }

    private PresentationDTO parseUrl(PresentationDTO presentationDTO) {
        String url = presentationDTO.getFileUrl();
        // https://tripcontainer.blob.core.windows.net/img-to-resize/fifa12.jpg
        //szpachla
        String[] urlElem = url.split("/");
        String tmp[] = urlElem[urlElem.length - 1].split("\\.");
        String fileName = tmp[0];
        String fileFormat = tmp[1];
        //szpachla
        if (validateFormat(fileFormat)) {
            presentationDTO.setFileName(fileName);
            presentationDTO.setFileFormat(fileFormat);
        }
        return presentationDTO;
    }

    private boolean validateFormat(String format) {
        return acceptableFormat.contains(format.toLowerCase());
    }

    private boolean validateFileInfo(PresentationDTO presentationDTO) {
        return presentationDTO.getFileName() != null && presentationDTO.getFileFormat() != null;
    }
}
