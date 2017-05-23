package tripApp.worker.poster;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by martynawisniewska on 23.05.2017.
 */
class MapGenerator {

    private int height = 0;
    private int width = 0;
    private URL url;
    private String urlString = "http://maps.googleapis.com/maps/api/staticmap?";
    private BufferedImage map;

    BufferedImage generateMapWithWidthAndHeight(int width, int height) throws IOException {
        initMapPrams(width, height);
        setMapUrl();
        getImageFromUrl();
        return map;
    }

    private void initMapPrams(int width, int height) {
        this.width = width;
        this.height = height;
        urlString = "http://maps.googleapis.com/maps/api/staticmap?";
    }

    private void setMapUrl() throws MalformedURLException {
        setUrlString();
        setUrlFromUrlString();
    }

    private void getImageFromUrl() throws IOException {
        map = ImageIO.read(url);
    }

    private void setUrlFromUrlString() throws MalformedURLException {
        URL url = new URL(urlString);
    }

    private void setUrlString(){
        setSize();
        setSensor();
        setMarkers();
        setPath();
    }

    private void setSize(){
        urlString = urlString + width + "x" + height;
    }

    private void setSensor(){
        urlString = urlString + "&sensor=true";
    }

    private void setMarkers(){
        //iteracja po punktach
        getMarkerStringFromCoordinatesWithLabel(0, 0, 'A');
    }

    private String getMarkerStringFromCoordinatesWithLabel(double latitude, double longitude, char label) {
        return "&markers=label:" + label + "%7C" + latitude + "," + longitude;
    }

    private void setPath(){
        urlString = urlString + "&path=color:0x0000ff|weight:5|geodesic:true" + getPathCoordinates();
    }

    private String getPathCoordinates(){
        String pathCoordinates = "";
        //iteracja po punktach i dodanie jako |latitude,longitude
        return pathCoordinates;
    }
}
