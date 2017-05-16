package model;

/**
 * Created by Matexo on 2017-05-16.
 */
public class PresentationDTO {
    private Integer correlationID;
    private String fileUrl;
    private Integer sizeX;
    private Integer sizeY;

    private String fileName;
    private String fileFormat;

    public PresentationDTO(Integer correlationID, String fileUrl, Integer sizeX, Integer sizeY) {
        this.correlationID = correlationID;
        this.fileUrl = fileUrl;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public Integer getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(Integer correlationID) {
        this.correlationID = correlationID;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getSizeX() {
        return sizeX;
    }

    public void setSizeX(Integer sizeX) {
        this.sizeX = sizeX;
    }

    public Integer getSizeY() {
        return sizeY;
    }

    public void setSizeY(Integer sizeY) {
        this.sizeY = sizeY;
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
        return "PresentationDTO{" +
                "correlationID=" + correlationID +
                ", fileUrl='" + fileUrl + '\'' +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", fileName='" + fileName + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                '}';
    }
}
