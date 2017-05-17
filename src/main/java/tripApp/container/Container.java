package tripApp.container;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import tripApp.config.AzureConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by Matexo on 2017-05-04.
 */
public class Container {

    private CloudStorageAccount cloudStorageAccount;
    private CloudBlobClient cloudBlobClient;
    private CloudBlobContainer cloudBlobContainer;

    public Container(AzureConfig containerConfig) throws URISyntaxException, InvalidKeyException, StorageException {
        cloudStorageAccount = CloudStorageAccount.parse(containerConfig.getConfig());
        cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
        cloudBlobContainer = cloudBlobClient.getContainerReference(containerConfig.getAzureServiceName());
    }

    public ByteArrayOutputStream downloadBlobItem(String blobItemName) throws URISyntaxException, StorageException {
        ByteArrayOutputStream outputStream;
        CloudBlob blob = cloudBlobContainer.getBlockBlobReference(blobItemName);
        outputStream = new ByteArrayOutputStream();
        blob.download(outputStream);
        return outputStream;
    }

    public void uploadBlobItem(String blobItemName, ByteArrayOutputStream itemStream)
            throws URISyntaxException, StorageException, IOException {
        CloudBlob blob = cloudBlobContainer.getBlockBlobReference(blobItemName);
        byte[] bytes = itemStream.toByteArray();
        blob.upload(new ByteArrayInputStream(bytes), bytes.length);
    }


}

