package tripApp.worker.poster;

import com.microsoft.azure.storage.StorageException;
import org.imgscalr.Scalr;
import tripApp.config.AzureConfig;
import tripApp.exception.WorkerException;
import tripApp.model.ErrorMessage;
import tripApp.model.PosterDTO;
import tripApp.model.ProgressDTO;
import tripApp.model.Status;
import tripApp.worker.IWorker;
import tripApp.worker.Worker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martynawisniewska on 23.05.2017.
 */

public class PosterWorker extends Worker implements IWorker {

    private static final String DEFAULT_FORMAT = "png";
    private static final int MAX_MAP_WIDTH = 640; // Google
    private static final int MAX_MAP_HEIGHT = 640; // Google
    private static final int THUMBNAIL_SIDE_SIZE = 200;
    private static final int HORIZONTAL_PHOTO_SIDE = 126;
    private static final int VERTICAL_PHOTO_SIDE = 155;

    private PosterDTO posterData;

    private int mapWidth;
    private int mapHeight;

    private int photosPerBarInRightSidePhotos;
    private int photosPerBarInBottomPhotos;

    private BufferedImage map;
    private BufferedImage poster;
    private BufferedImage titleImage;
    private BufferedImage rightSidePhotos;
    private BufferedImage bottomPhotos;
    private String posterName;
    private int progress = 0;
    private double progressPerImage = 0;
    private ByteArrayOutputStream blobForPoster;
    private List<ByteArrayOutputStream> downloadedBlobs = new ArrayList<>();
    private List<BufferedImage> verticalPhotos = new ArrayList<>();
    private List<BufferedImage> horizontalPhotos = new ArrayList<>();

    public PosterWorker(AzureConfig blobConfig, AzureConfig respConfig)
            throws InvalidKeyException, StorageException, URISyntaxException {
        super(blobConfig, respConfig);
    }

    public String doWork(String message) throws StorageException {
        parseMessage(message);
        try {
            parseMessage(message);
            validateMessage();
            initFields();
            setPosterName();
            setPhotosFromBlobs();
            calculateParams();
            generateMap();
            createTitle();
            joinImages();
            savePoster();
        } catch (WorkerException | URISyntaxException | IOException e) {
            return null;
        }
        return "";
    }

    private void setPhotosFromBlobs() throws URISyntaxException, StorageException, IOException {
        downloadImages();
        setListsOfPhotos();
    }

    private void setListsOfPhotos() throws IOException {
        List<BufferedImage> photos = convertDownloadedImagesToBufferedImages();
        divideIntoVerticalAndHorizontalPhotos(photos);
    }

    private void divideIntoVerticalAndHorizontalPhotos(List<BufferedImage> photos){
        photos.forEach(photo ->{
            if(photo.getWidth() == THUMBNAIL_SIDE_SIZE){
                horizontalPhotos.add(photo);
            } else {
                verticalPhotos.add(photo);
            }
        });
    }

    private List<BufferedImage> convertDownloadedImagesToBufferedImages() throws IOException {
        List<BufferedImage> photos = new ArrayList<>();
        for(ByteArrayOutputStream blob: downloadedBlobs){
            photos.add(ImageIO.read(new ByteArrayInputStream(blob.toByteArray())));
        }
        return photos;
    }

    private void initFields(){
        downloadedBlobs = new ArrayList<>();
        verticalPhotos = new ArrayList<>();
        horizontalPhotos = new ArrayList<>();
        blobForPoster = new ByteArrayOutputStream();
    }

    private void createTitle() {
        titleImage = TextToGraphicConverter.convertTextToGraphic(posterData.tripName, mapWidth);
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
        posterName = posterData.posterName; // z JSONA
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

    private void downloadImages() throws URISyntaxException, StorageException {
        // pobranie miniaturek podanych w JSONie
        for(String blobName: posterData.blobsNames){
            downloadedBlobs.add(getBlobImage(blobName));
            sendProcessingNextImageEndedMessage(); // po każdym zdjęciu
        }
        logDebugMessage("Images downloaded"); // na końcu
    }

    private ByteArrayOutputStream getBlobImage(String blobName) throws URISyntaxException, StorageException {
        return container.downloadBlobItem(blobName);
    }

    private void joinImages() throws StorageException {
        joinVerticalPhotos();
        joinHorizontalPhotos();
        joinMapAndRightSidePhotosToPoster();
        joinBottomPhotosToPoster();
        joinTitleToPoster();
        logDebugMessage("Images joined"); //na końcu
    }

    private void joinTitleToPoster() {
        poster = ImageTools.joinImagesVertically(titleImage, poster);
    }

    private void joinBottomPhotosToPoster() {
        poster = ImageTools.joinImagesVertically(poster, bottomPhotos);
    }

    private void joinMapAndRightSidePhotosToPoster() {
        poster = ImageTools.joinImagesHorizontally(map, rightSidePhotos);
    }

    private void joinHorizontalPhotos() throws StorageException {
        stickBarsVertically(createBottomPhotoBars());
    }

    private List<BufferedImage> createBottomPhotoBars() throws StorageException {
        List<BufferedImage> bottomSideBars = new ArrayList<>();
        BufferedImage barOfPhotos = null;
        int counter = 1;
        for(BufferedImage photo: horizontalPhotos){
            if(counter == photosPerBarInBottomPhotos){
                bottomSideBars.add(barOfPhotos);
                barOfPhotos = null;
            }
            if(barOfPhotos == null){
                photo = Scalr.resize(photo, Scalr.Mode.FIT_TO_HEIGHT, THUMBNAIL_SIDE_SIZE, HORIZONTAL_PHOTO_SIDE);
                barOfPhotos = photo;
                counter = 1;
                sendProcessingNextImageEndedMessage();
            } else {
                photo = Scalr.resize(photo, Scalr.Mode.FIT_TO_HEIGHT, THUMBNAIL_SIDE_SIZE, HORIZONTAL_PHOTO_SIDE);
                barOfPhotos = ImageTools.joinImagesHorizontally(barOfPhotos, photo);
                counter++;
                sendProcessingNextImageEndedMessage();
            }
        }
        bottomSideBars.add(barOfPhotos);
        return bottomSideBars;
    }

    private void stickBarsVertically(List<BufferedImage> bottomSidePhotoBars) {
        bottomSidePhotoBars.forEach(bar -> bottomPhotos = ImageTools.joinImagesVertically(bottomPhotos, bar));
    }

    private void joinVerticalPhotos() throws StorageException {
        stickBarsHorizontally(createRightSidePhotoBars());
    }

    private List<BufferedImage> createRightSidePhotoBars() throws StorageException {
        List<BufferedImage> rightSideBars = new ArrayList<>();
        BufferedImage barOfPhotos = null;
        int counter = 1;
        for(BufferedImage photo: verticalPhotos){
            if(counter == photosPerBarInRightSidePhotos){
                rightSideBars.add(barOfPhotos);
                barOfPhotos = null;
            }
            if(barOfPhotos == null){
                photo = Scalr.resize(photo, Scalr.Mode.FIT_TO_WIDTH, VERTICAL_PHOTO_SIDE, THUMBNAIL_SIDE_SIZE);
                barOfPhotos = photo;
                counter = 1;
                sendProcessingNextImageEndedMessage();
            } else {
                photo = Scalr.resize(photo, Scalr.Mode.FIT_TO_WIDTH, VERTICAL_PHOTO_SIDE, THUMBNAIL_SIDE_SIZE);
                barOfPhotos = ImageTools.joinImagesVertically(barOfPhotos, photo);
                counter++;
                sendProcessingNextImageEndedMessage();
            }
        }
        rightSideBars.add(barOfPhotos);
        return rightSideBars;
    }

    private void stickBarsHorizontally(List<BufferedImage> rightSideBars) {
        rightSideBars.forEach(bar -> rightSidePhotos = ImageTools.joinImagesHorizontally(rightSidePhotos, bar));
    }

    private void parseMessage(String message) {
        // przetworzenie JSONa i dodanie odpowiednich pól
        // jeśli bład w przetwarzaniu throw new WorkerException("Error in parsing message");
        posterData = gson.fromJson(message, PosterDTO.class);
        logDebugMessage("Message parsed");
    }

    private void calculateParams() throws StorageException {
        calculateMapSize();
        calculateProgressPerImage();
        calculatePhotosPerBarInRightSidePhotos();
        calculatePhotosPerBarInBottomPhotos();
        sendCalculatingSizesEndedMessage();
    }

    private void calculatePhotosPerBarInBottomPhotos() {
        photosPerBarInBottomPhotos = mapWidth / THUMBNAIL_SIDE_SIZE;
    }

    private void calculatePhotosPerBarInRightSidePhotos() {
        photosPerBarInRightSidePhotos = mapHeight / THUMBNAIL_SIDE_SIZE;
    }

    private void calculateMapSize(){
        int preferredMapSide = THUMBNAIL_SIDE_SIZE * downloadedBlobs.size() > 400 ? THUMBNAIL_SIDE_SIZE * downloadedBlobs.size():400;
        mapWidth = preferredMapSide < MAX_MAP_WIDTH ? preferredMapSide:MAX_MAP_WIDTH;
        mapHeight = preferredMapSide < MAX_MAP_HEIGHT ? preferredMapSide:MAX_MAP_HEIGHT;
    }

    private void calculateProgressPerImage() {
         progressPerImage = 86.0 / verticalPhotos.size() + horizontalPhotos.size() + 1;
    }

    private void generateMap() throws WorkerException {
        MapGenerator mapGenerator = new MapGenerator();
        try {
            map = mapGenerator.generateMapWithWidthAndHeight(mapWidth, mapHeight, posterData.coordinates);
        } catch (IOException e) {
            throw new WorkerException("error in generating map");
        }
    }

    private void addProgressMessageToQueue(int progress, Status status) throws StorageException {
        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(progress, status, posterData.correlationID)));
    }

    private void logDebugMessage(String message) {
        logger.debug(message + " :" + posterData.correlationID);
    }

    private void logErrorMessage(String errorMessage) {
        logger.error(errorMessage);
    }

    public void setPosterData(PosterDTO posterData){
        this.posterData = posterData;
    }
}
