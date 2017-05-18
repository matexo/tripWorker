package tripApp.model;

/**
 * Created by Matexo on 2017-05-16.
 */
public class ThumbnailDTO {
    private String correlationID;
    private String fileUrl;
    private String fileName;
    private String fileFormat;

    public ThumbnailDTO(String correlationID, String fileUrl) {
        this.correlationID = correlationID;
        this.fileUrl = fileUrl;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return "ThumbnailDTO{" +
                "correlationID=" + correlationID +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                '}';
    }
}
