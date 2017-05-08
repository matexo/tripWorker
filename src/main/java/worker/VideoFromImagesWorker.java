package worker;

import config.AzureConfig;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mr on 5/8/17.
 */
public class VideoFromImagesWorker extends Worker implements IWorker {
    public VideoFromImagesWorker(AzureConfig azureConfig) {
        super(azureConfig);
    }

    public String doWork(String message) {
        String[] fileNames = message.split(";");

        final Rational framerate = Rational.make(1, 1);
        final String outputName = fileNames[0].split("\\.")[0];
        final Muxer muxer = Muxer.make(outputName, null, "avi");
        final MuxerFormat format = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

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

            MediaPictureConverter converter = null;
            final MediaPicture picture = MediaPicture.make(
                    encoder.getWidth(),
                    encoder.getHeight(),
                    pixelFormat);
            picture.setTimeBase(framerate);

            final MediaPacket packet = MediaPacket.make();
            for (int i = 0; i < fileNames.length; i++) {
                ByteArrayOutputStream download = container.downloadBlobItem(fileNames[i]);
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(download.toByteArray()));
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
            }

            do {
                encoder.encode(packet, null);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } while (packet.isComplete());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        muxer.close();

        // TODO Upload do bloba
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

}
