package tripApp.model;

/**
 * Created by Matexo on 2017-05-16.
 */
public class ProgressDTO {
    private Integer progress;
    private Status status;
    private String correlationID;
    private String progressInfo;
    private String content;

    public ProgressDTO(Integer progress, Status status, String correlationID) {
        this.progress = progress;
        this.status = status;
        this.correlationID = correlationID;
    }

    public ProgressDTO(Integer progress, Status status, String correlationID, String progressInfo) {
        this.progress = progress;
        this.status = status;
        this.correlationID = correlationID;
        this.progressInfo = progressInfo;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
