package container;

import com.microsoft.azure.storage.StorageException;
import worker.IWorker;
import worker.ResizeWorker;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Matexo on 2017-05-04.
 */
public class ContainerTest {


    public static void main(String[] args) throws URISyntaxException, StorageException, IOException {
        IWorker worker = new ResizeWorker("img-to-resize");
        worker.doWork("fifa17.jpg");
    }
}
