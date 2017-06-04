package tripApp.worker.poster;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by martynawisniewska on 30.05.2017.
 */
class ImageTools {

    static BufferedImage joinImagesHorizontally(BufferedImage img1, BufferedImage img2) {
        img1 = initBufferedImage(img1);
        img2 = initBufferedImage(img2);
        int offset = 5;
        int wid = img1.getWidth() + img2.getWidth() + offset;
        int height = Math.max(img1.getHeight(), img2.getHeight()) + offset;

        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();

        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);

        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth() + offset, 0);
        g2.dispose();
        return newImage;
    }

    static BufferedImage joinImagesVertically(BufferedImage img1, BufferedImage img2) {
        img1 = initBufferedImage(img1);
        img2 = initBufferedImage(img2);
        int offset = 5;
        int wid = Math.max(img1.getWidth(), img2.getWidth()) + offset;
        int height = img1.getHeight() + img2.getHeight() + offset;

        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
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

    private static BufferedImage initBufferedImage(BufferedImage img){
        if(img == null){
            img = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
            return img;
        }
        return img;
    }
}
