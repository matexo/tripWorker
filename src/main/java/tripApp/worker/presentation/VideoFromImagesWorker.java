package tripApp.worker.presentation;

import com.microsoft.azure.storage.StorageException;
import tripApp.Main;
import tripApp.config.AzureConfig;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import org.imgscalr.Scalr;
import tripApp.model.ProgressDTO;
import tripApp.model.Status;
import tripApp.model.VideoFromImagesDTO;
import tripApp.worker.IWorker;
import tripApp.worker.Worker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.List;

import static tripApp.config.WorkersConfig.BLOB_URL;

/**
 * Created by mr on 5/8/17.
 */
public class VideoFromImagesWorker extends Worker implements IWorker {


    public VideoFromImagesWorker(AzureConfig blobConfig, AzureConfig respConfig)
            throws InvalidKeyException, StorageException, URISyntaxException {
        super(blobConfig, respConfig);
    }

    public String doWork(String message) throws StorageException {
        VideoFromImagesDTO deserializedMessage = parseMessage(message);

        sendWorkStartMessage(deserializedMessage.getCorrelationID());

        final Rational framerate = Rational.make(1, 1);
        final String outputName = deserializedMessage.getTripName() + ".avi";
        final Muxer muxer = Muxer.make(outputName, null, "avi");
        final MuxerFormat format = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        int progress = 0;

        Encoder encoder = Encoder.make(codec);

        encoder.setWidth(800);
        encoder.setHeight(600);
        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelFormat);
        encoder.setTimeBase(framerate);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);

        muxer.addNewStream(encoder);

        try {
            muxer.open(null, null);

            progress = 4;
            addProgressMessageToQueue(progress, Status.WORKING, deserializedMessage.getCorrelationID(), "");

            MediaPictureConverter converter = null;
            final MediaPicture picture = MediaPicture.make(
                    encoder.getWidth(),
                    encoder.getHeight(),
                    pixelFormat);
            picture.setTimeBase(framerate);

            final MediaPacket packet = MediaPacket.make();
            for (int i = 0; i < deserializedMessage.getFilesList().size(); i++) {
                URL imageURL = new URL(deserializedMessage.getFilesList().get(i));
                BufferedImage img = ImageIO.read(imageURL);
                img = Scalr.resize(img, Scalr.Mode.FIT_EXACT, encoder.getWidth(), encoder.getHeight());
                img = convertToType(img, BufferedImage.TYPE_3BYTE_BGR);

                if (converter == null)
                    converter = MediaPictureConverterFactory.createConverter(img, picture);

                converter.toPicture(picture, img, i);

                do {
                    encoder.encode(packet, picture);
                    if (packet.isComplete())
                        muxer.write(packet, false);
                } while (packet.isComplete());
                progress += 88 / deserializedMessage.getFilesList().size();
                addProgressMessageToQueue(progress, Status.WORKING, deserializedMessage.getCorrelationID(), "");
            }

            do {
                encoder.encode(packet, null);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } while (packet.isComplete());

            progress += 4;
            addProgressMessageToQueue(progress, Status.WORKING, deserializedMessage.getCorrelationID(), "");

        } catch (Exception e) {
            e.printStackTrace();
            addProgressMessageToQueue(progress, Status.ERROR, deserializedMessage.getCorrelationID(), "");
        }

        muxer.close();

        try {
            InputStream is = new FileInputStream(outputName);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            try {
                container.uploadBlobItem(outputName, os);
            } catch (Exception ex) {
                ex.printStackTrace();
                addProgressMessageToQueue(progress, Status.ERROR, deserializedMessage.getCorrelationID(), "");
            }
            try {
                is.close();
                os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                addProgressMessageToQueue(progress, Status.ERROR, deserializedMessage.getCorrelationID(), "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            addProgressMessageToQueue(progress, Status.ERROR, deserializedMessage.getCorrelationID(), "");
        }

        addProgressMessageToQueue(100, Status.COMPLETED, deserializedMessage.getCorrelationID(), outputName);

        return outputName;
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType)
            image = sourceImage;
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    private VideoFromImagesDTO parseMessage(String message) {
        VideoFromImagesDTO deserializedMessage = gson.fromJson(message, VideoFromImagesDTO.class);
        logDebugMessage("Message parsed", deserializedMessage.getCorrelationID());
        return deserializedMessage;
    }

    private void logDebugMessage(String message, String correlationId) {
        logger.debug(message + " :" + correlationId);
    }

    private void sendWorkStartMessage(String correlationID) throws StorageException {
        addProgressMessageToQueue(0, Status.WORKING, correlationID, "");
    }

    private void addProgressMessageToQueue(int progress, Status status, String correlationID, String name) throws StorageException {
        ProgressDTO msg = new ProgressDTO(progress, status, correlationID);
        msg.setContent(Main.CONFIG.getProperty(BLOB_URL) + name);
        progressQueue.addMessageToQueue(gson.toJson(msg));
    }








}
