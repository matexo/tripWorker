package tripApp.worker.poster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by martynawisniewska on 30.05.2017.
 */
public class TextToGraphicConverter {
    private static final Font FONT = new Font("Arial", Font.PLAIN, 35);
    private static final int HEIGHT = 50;
    private static Color BACKGROUND_COLOR = Color.WHITE;
    private static Color TEXT_COLOR = Color.BLACK;

    public static void main(String[] args) throws Exception {
        BufferedImage image = TextToGraphicConverter.convertTextToGraphic("my text", 300);
        //write BufferedImage to file
        ImageIO.write(image, "png", new File("path-to-file2.png"));
    }

    public static BufferedImage convertTextToGraphic(String text, int width) {

        FontRenderContext frc = new FontRenderContext(null, true, true);

        Rectangle2D bounds = FONT.getStringBounds(text, frc);
        int w = (int) bounds.getWidth();
        int h = (int) bounds.getHeight();

        BufferedImage image = new BufferedImage(width, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, width, HEIGHT);

        g.fillRect(0, 0, w, h);
        g.setColor(TEXT_COLOR);
        g.setFont(FONT);
        g.drawString(text, (float) bounds.getX(), (float) -bounds.getY());

        g.dispose();
        return image;
    }

}
