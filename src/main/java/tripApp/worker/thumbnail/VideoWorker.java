package tripApp.worker.thumbnail;

import com.microsoft.azure.storage.StorageException;
import org.imgscalr.Scalr;
import tripApp.Main;
import tripApp.config.AzureConfig;
import tripApp.config.WorkersConfig;
import tripApp.model.ErrorMessage;
import tripApp.model.ProgressDTO;
import tripApp.model.Status;
import tripApp.model.ThumbnailDTO;
import tripApp.worker.IWorker;
import tripApp.worker.Worker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.List;


import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;


public class VideoWorker extends Worker implements IWorker {

    private static final Integer THUMBNAIL_Y_SIZE = 200;
    private static final String DEFAULT_FORMAT = "jpg";
    private static final String ADDITIONAL_FILE_NAME = "thumbnail.";

    private static final double SECONDS_BETWEEN_FRAMES = 2;
    private static int mVideoStreamIndex = -1;
    private static boolean END_PROCESSING = false;
    private static BufferedImage videoImage;
    private static long mLastPtsWrite = Global.NO_PTS;
    static final long MICRO_SECONDS_BETWEEN_FRAMES =
            (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);

    static final List<String> acceptableFormat = Arrays.asList("mp4", "avi", "flv");


    public VideoWorker(AzureConfig blobConfig, AzureConfig respConfig)
            throws InvalidKeyException, StorageException, URISyntaxException {
        super(blobConfig, respConfig);
    }

    public String doWork(String message) throws StorageException {

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

        ByteArrayOutputStream downloadedBlobItem;
        String fullFileName = thumbnailDTO.getFileName() + "." + thumbnailDTO.getFileFormat();
        try {
            downloadedBlobItem = container.downloadBlobItem(fullFileName);
            saveFileFromBAOS(downloadedBlobItem, fullFileName);
        } catch (Exception e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(40, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.DOWNLOADING_ERROR + " " + thumbnailDTO.getFileName() + thumbnailDTO.getFileFormat());
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(40, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Downloading completed" + thumbnailDTO.getCorrelationID());

        if (downloadedBlobItem == null) {
            return null;
        }

        try {
            makeThumbnailFromVideo(fullFileName);
        } catch (Exception e) {
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(60, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.CREATING_IMAGE_ERROR);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(60, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Creating image from video completed" + thumbnailDTO.getCorrelationID());

        BufferedImage bufferedImage;
        try {
            bufferedImage = Scalr.resize(videoImage, THUMBNAIL_Y_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(70, Status.ERROR, thumbnailDTO.getCorrelationID())));
            logger.error(ErrorMessage.RESIZING_ERROR + " " + THUMBNAIL_Y_SIZE);
            return null;
        }

        progressQueue.addMessageToQueue(gson.toJson(new ProgressDTO(70, Status.WORKING, thumbnailDTO.getCorrelationID())));
        logger.debug("Resizing completed" + thumbnailDTO.getCorrelationID());

        String thumbnailName = thumbnailDTO.getFileName() + ADDITIONAL_FILE_NAME + DEFAULT_FORMAT;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, DEFAULT_FORMAT, byteArrayOutputStream);
            deleteFileFromDisk(fullFileName);
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
        progressDTO.setContent(Main.CONFIG.getProperty(WorkersConfig.BLOB_URL) + thumbnailName);
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
        String[] urlElem = url.split("/");
        String tmp[] = urlElem[urlElem.length - 1].split("\\.");
        String fileName = tmp[0];
        String fileFormat = tmp[1];
        if (validateFormat(fileFormat)) {
            thumbnailDTO.setFileName(fileName);
            thumbnailDTO.setFileFormat(fileFormat);
        }
        return thumbnailDTO;
    }

    private boolean validateFormat(String format) {
        return acceptableFormat.contains(format.toLowerCase());
    }

    private boolean validateFileInfo(ThumbnailDTO thumbnailDTO) {
        return thumbnailDTO.getFileName() != null && thumbnailDTO.getFileFormat() != null;
    }

    private static  void saveFileFromBAOS(ByteArrayOutputStream baos, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
            baos.writeTo(fos);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private static void deleteFileFromDisk(String filePath){
        try{
            File file = new File(filePath);
            file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void makeThumbnailFromVideo(String filePath) {
        IMediaReader mediaReader = ToolFactory.makeReader(filePath);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        mediaReader.addListener(new ImageSnapListener());

        while (mediaReader.readPacket() == null && !END_PROCESSING) ;
        mediaReader.close();
    }

    private static class ImageSnapListener extends MediaListenerAdapter {
        public void onVideoPicture(IVideoPictureEvent event) {

            if (event.getStreamIndex() != mVideoStreamIndex) {
                if (mVideoStreamIndex == -1) {
                    mVideoStreamIndex = event.getStreamIndex();
                } else {
                    return;
                }
            }

            if (mLastPtsWrite == Global.NO_PTS) {
                mLastPtsWrite = 0;
            }

            if (event.getTimeStamp() - mLastPtsWrite
                    > MICRO_SECONDS_BETWEEN_FRAMES) {
                END_PROCESSING = true;

                mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
                videoImage = event.getImage();
            }
        }
    }

}