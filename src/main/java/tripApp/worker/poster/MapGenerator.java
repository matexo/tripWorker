package tripApp.worker.poster;

import tripApp.model.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by martynawisniewska on 23.05.2017.
 */
class MapGenerator{

    private int height = 0;
    private int width = 0;
    private List<Point> coordinates;
    private URL url;
    private String urlString = "http://maps.googleapis.com/maps/api/staticmap?";
    private BufferedImage map;

    BufferedImage generateMapWithWidthAndHeight(int width, int height, List<Point> coordinates) throws IOException {
        initMapPrams(width, height, coordinates);
        setMapUrl();
        getImageFromUrl();
        return map;
    }

    private void initMapPrams(int width, int height, List<Point> coordinates) {
        this.width = width;
        this.height = height;
        this.coordinates = coordinates;
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
        url = new URL(urlString);
    }

    private void setUrlString(){
        setSize();
        setSensor();
        setMarkers();
        setPath();
    }

    private void setSize(){
        urlString = urlString + "size=" + width + "x" + height;
    }

    private void setSensor(){
        urlString = urlString + "&sensor=true";
    }

    private void setMarkers(){
        coordinates.forEach(point -> {
            urlString += getMarkerStringFromCoordinatesWithLabel(point.latitude, point.longitude, '*');
        });
    }

    private String getMarkerStringFromCoordinatesWithLabel(double latitude, double longitude, char label) {
        return "&markers=label:" + label + "%7C" + latitude + "," + longitude;
    }

    private void setPath(){
        urlString += "&path=color:0x0000ff|weight:5|geodesic:true";
        addPathCoordinates();
    }

    private void addPathCoordinates(){
        coordinates.forEach(point -> {
            urlString += "|" + point.latitude + "," + point.longitude;
        });
    }

    public String getUrlString(){
        return urlString;
    }
}
