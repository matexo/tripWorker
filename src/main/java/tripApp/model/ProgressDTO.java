package tripApp.model;

/**
 * Created by Matexo on 2017-05-16.
 */
public class ProgressDTO {
    private Integer percentStatus;
    private Progress progress;
    //long czy int
    private String correlationID;
    private String progressInfo;
    private String content;

    public ProgressDTO(Integer percentStatus, Progress progress, String correlationID) {
        this.percentStatus = percentStatus;
        this.progress = progress;
        this.correlationID = correlationID;
    }

    public ProgressDTO(Integer percentStatus, Progress progress, String correlationID, String progressInfo) {
        this.percentStatus = percentStatus;
        this.progress = progress;
        this.correlationID = correlationID;
        this.progressInfo = progressInfo;
    }

    public Integer getPercentStatus() {
        return percentStatus;
    }

    public void setPercentStatus(Integer percentStatus) {
        this.percentStatus = percentStatus;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getProgressInfo() {
        return progressInfo;
    }

    public void setProgressInfo(String progressInfo) {
        this.progressInfo = progressInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
