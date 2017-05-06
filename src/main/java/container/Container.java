package container;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
     * Created by Matexo on 2017-05-04.
     */
    public class Container {

        public static final String accountName = "tripcontainer";
        public static final String accountKey = "beUQum99z5wuiahalcLEesKHwmzNxvP75QNjbSVakxclvqKFuQuNYB0d9SciriyG1RV9Die3wJ+a/lY9KADIhA==";
        public static final String storageConnectionName =
                "DefaultEndpointsProtocol=http;"
                        + "AccountName=" + accountName +";"
                        + "AccountKey=" + accountKey;


        private CloudStorageAccount cloudStorageAccount;
        private CloudBlobClient cloudBlobClient;
        private CloudBlobContainer cloudBlobContainer;

        public Container(String blobName) {
            try {
                cloudStorageAccount = CloudStorageAccount.parse(storageConnectionName);
                cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
                cloudBlobContainer = cloudBlobClient.getContainerReference(blobName);
            } catch (Exception e) {
                System.out.println(e.getMessage()); //dac logi czy cos
            }
        }

        public ByteArrayOutputStream downloadBlobItem(String blobItemName) {
            ByteArrayOutputStream outputStream = null;
            try {
            CloudBlob blob = cloudBlobContainer.getBlockBlobReference(blobItemName);
            outputStream = new ByteArrayOutputStream();
            blob.download(outputStream);
            } catch (Exception e) {
                System.out.println(e.getMessage()); //dac logi czy cos
            }
            return outputStream;
        }

        public void uploadBlobItem(String blobItemName , ByteArrayOutputStream itemStream) {
            try {
                CloudBlob blob = cloudBlobContainer.getBlockBlobReference(blobItemName);
                byte[] bytes = itemStream.toByteArray();
                blob.upload(new ByteArrayInputStream(bytes) , bytes.length);
            } catch (Exception e) {
                System.out.println(e.getMessage()); //dac logi czy cos
            }

        }


    }

