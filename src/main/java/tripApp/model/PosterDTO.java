package tripApp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martynawisniewska on 30.05.2017.
 */
public class PosterDTO {
    public List<Point> coordinates = new ArrayList<>();
    public String tripName;
    public List<String> blobsNames = new ArrayList<>();
    public String posterName;
    public String correlationID;
}
