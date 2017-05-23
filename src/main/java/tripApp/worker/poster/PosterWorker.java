package tripApp.worker.poster;

import com.microsoft.azure.storage.StorageException;
import tripApp.config.AzureConfig;
import tripApp.exception.WorkerException;
import tripApp.model.ErrorMessage;
import tripApp.model.ProgressDTO;
import tripApp.model.Status;
import tripApp.worker.IWorker;
import tripApp.worker.Worker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by martynawisniewska on 23.05.2017.
 */

public class PosterWorker extends Worker implements IWorker {

    private static final String DEFAULT_FORMAT = "jpg";

    private String correlationID;

    private int mapWidth;
    private int mapHeight;

    private BufferedImage map;
    private BufferedImage poster;
    private String posterName;
    private int progress = 0;
    private double progressPerImage = 0;
    private ByteArrayOutputStream blobForPoster;

    public PosterWorker(AzureConfig azureConfig) throws InvalidKeyException, StorageException, URISyntaxException {
        super(azureConfig);
    }

    public String doWork(String message) throws StorageException, WorkerException {
        parseMessage(message);
        validateMessage();
        downloadImages();
        calculateParams();
        generateMap();
        joinImages();
        addTripNameToPoster();
        savePoster();
        return "";
    }

    private void addTripNameToPoster() {
        // dodanie do plakatu informacji o nazwie wycieczki - z JSONa
    }

    private void savePoster() throws StorageException, WorkerException {
        setBlobFromPoster();
        uploadBlobForPoster();
    }

    private void uploadBlobForPoster() throws StorageException, WorkerException {
        try {
            container.uploadBlobItem(posterName, blobForPoster);
        } catch (Exception e) {
            addProgressMessageToQueue(progress, Status.ERROR);
            logErrorMessage(ErrorMessage.UPLOADING_ERROR);
            throw new WorkerException("Error in uploading blob");
        }
    }

    private void setBlobFromPoster() throws StorageException, WorkerException {
        try {
            ImageIO.write(poster, DEFAULT_FORMAT, blobForPoster);
        } catch (IOException e) {
            addProgressMessageToQueue(progress, Status.ERROR);
            logErrorMessage(ErrorMessage.WRITING_ERROR);
            throw new WorkerException("Error in writing to blob");
        }
    }

    private void setPosterName() {
        posterName = ""; // z JSONA
    }

    private void sendCalculatingSizesEndedMessage() throws StorageException {
        progress += 4;
        addProgressMessageToQueue(progress, Status.WORKING);
    }

    private void sendProcessingNextImageEndedMessage() throws StorageException {
        progress += progressPerImage;
        addProgressMessageToQueue(progress, Status.WORKING);
    }

    private void sendWorkStartMessage() throws StorageException {
        addProgressMessageToQueue(0, Status.WORKING);
    }

    private void validateMessage() throws StorageException {
        // walidacja
        // jeśli zła throw new WorkerException("Message not valid");
        sendWorkStartMessage();
        logDebugMessage("Message validated");
    }

    private void downloadImages() throws StorageException {
        // pobranie miniaturek podanych w JSONie
        // jeśli bład w pobieraniu throw new WorkerException("Error in downloading images");
        sendProcessingNextImageEndedMessage(); // po każdym zdjęciu
        logDebugMessage("Images downloaded"); // na końcu
    }

    private void joinImages() throws StorageException {
        // odpowiednie łączenie zdjęć
        sendProcessingNextImageEndedMessage(); // po każdym zdjęciu
        logDebugMessage("Images joined"); //na końcu
    }

    private void parseMessage(String message) {
        // przetworzenie JSONa i dodanie odpowiednich pól
        // jeśli bład w przetwarzaniu throw new WorkerException("Error in parsing message");
        logDebugMessage("Message parsed");
    }

    private void calculateParams() throws StorageException {
        // obliczenie wymiarów
        // czy miniaturki doklejamy tylko po prawej stronie czy też na dole
        // ile kolumn/wierszy tworzymy z miniatur
        calculateProgressPerImage();
        sendCalculatingSizesEndedMessage();
    }

    private void calculateProgressPerImage() {
        // progressPerImage = 86.0 / liczba zdjęć + 1
    }

    private void generateMap() throws WorkerException {
        MapGenerator mapGenerator = new MapGenerator();
        try {
            map = mapGenerator.generateMapWithWidthAndHeight(mapWidth ,mapHeight);
        } catch (IOException e) {
            throw new WorkerException("error in generating map");
        }
    }

    public BufferedImage joinImagesHorizontally(BufferedImage img1, BufferedImage img2) {
        int offset  = 5;
        int wid = img1.getWidth()+img2.getWidth()+offset;
        int height = Math.max(img1.getHeight(),img2.getHeight())+offset;

        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();

        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);

        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth()+offset, 0);
        g2.dispose();
        return newImage;
    }

    public BufferedImage joinImagesVertically(BufferedImage img1,BufferedImage img2) {
        int offset  = 5;
        int wid = Math.max(img1.getWidth(), img2.getWidth()) + offset;
        int height = img1.getHeight() + img2.getHeight() + offset;

        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();

        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);

        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight() + offset);
        g2.dispose();
        return newImage;
    }

    private void addProgressMessageToQueue(int progress, Status status) throws StorageException {
        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(progress, status, correlationID)));
    }

    private void logDebugMessage(String message){
        logger.debug(message + " :"  + correlationID);
    }

    private void logErrorMessage(String errorMessage){
        logger.error(errorMessage);
    }
}
