package tripApp.model;

import java.util.List;

/**
 * Created by Matexo on 2017-06-07.
 */
public class VideoFromImagesDTO {
    //        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<String> filesList;
    private String tripName;
    private String correlationID;

    public VideoFromImagesDTO(List<String> filesList, String tripName, String correlationID) {
        this.filesList = filesList;
        this.tripName = tripName;
        this.correlationID = correlationID;
    }

    public List<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<String> filesList) {
        this.filesList = filesList;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    @Override
    public String toString() {
        return "VideoFromImagesDTO{" +
                "filesList=" + filesList +
                ", tripName='" + tripName + '\'' +
                ", correlationID='" + correlationID + '\'' +
                '}';
    }
}
