package worker;

import config.AzureConfig;
import container.Container;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Matexo on 2017-05-06.
 */
public class ResizeWorker extends Worker implements IWorker{

    private static final Integer THUMBNAIL_Y_SIZE = 100;
    private static final String DEFAULT_FORMAT = "jpg";

    public ResizeWorker(AzureConfig azureConfig) {
        super(azureConfig);
    }

    //Przyjmuje nazwe pliku do pobrania ewentualnie url w tym przypadku trzeba sparsowac
    public String doWork(String message) {

        // przetworz wiadomosc
        String[] parsedMessage = message.split("\\.");
        if(parsedMessage.length != 2)
            throw new IllegalArgumentException();
        String fileName = parsedMessage[0];
        String fileFormat = "." + parsedMessage[1];

        // pobierz obrazek z bloba
        ByteArrayOutputStream downloadedBlobItem = container.downloadBlobItem(fileName + fileFormat);

        // przetworz obrazek (co sie bedzie dzialo jak nie znajdzie)
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = Scalr.resize(ImageIO.read(new ByteArrayInputStream(downloadedBlobItem.toByteArray())) , THUMBNAIL_Y_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // wrzuc obrazek do bloba
        String thumbnailName = fileName + "thumbnail" + fileFormat;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write( bufferedImage , DEFAULT_FORMAT , byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.uploadBlobItem(thumbnailName , byteArrayOutputStream);

        // zwroc id/url
        return thumbnailName;
    }
}
