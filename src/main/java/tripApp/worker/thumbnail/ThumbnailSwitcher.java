package tripApp.worker.thumbnail;

import com.google.gson.Gson;
import tripApp.model.ThumbnailDTO;
import tripApp.worker.IWorker;

/**
 * Created by Matexo on 2017-06-01.
 */
public class ThumbnailSwitcher implements IWorker {

    private IWorker resizeWorker;
    private IWorker videoWorker;

    public ThumbnailSwitcher(IWorker resizeWorker, IWorker videoWorker) {
        this.resizeWorker = resizeWorker;
        this.videoWorker = videoWorker;
    }

    @Override
    public String doWork(String message) throws Exception {
        Gson gson = new Gson();
        ThumbnailDTO thumbnailDTO = gson.fromJson(message, ThumbnailDTO.class);
        String url = thumbnailDTO.getFileUrl();
        String[] urlElem = url.split("/");
        String tmp[] = urlElem[urlElem.length - 1].split("\\.");
        String fileFormat = tmp[1];
        String output = null;
        if (fileFormat != null) {
            if (ResizeWorker.acceptableFormat.contains(fileFormat)) {
                output = resizeWorker.doWork(message);
            } else if (VideoWorker.acceptableFormat.contains(videoWorker)) {
                output = videoWorker.doWork(message);
            }
        }
        return output;
    }
}
