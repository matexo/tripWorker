package tripApp.worker;

import com.microsoft.azure.storage.StorageException;
import tripApp.exception.WorkerException;

/**
 * Created by Matexo on 2017-05-06.
 */
public interface IWorker {

    String doWork(String message) throws StorageException, WorkerException;
}
