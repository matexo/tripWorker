package container;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import config.AzureConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
     * Created by Matexo on 2017-05-04.
     */
    public class Container {

        private CloudStorageAccount cloudStorageAccount;
        private CloudBlobClient cloudBlobClient;
        private CloudBlobContainer cloudBlobContainer;

        public Container(AzureConfig containerConfig) {
            try {
                cloudStorageAccount = CloudStorageAccount.parse(containerConfig.getConfig());
                cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
                cloudBlobContainer = cloudBlobClient.getContainerReference(containerConfig.getAzureServiceName());
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

