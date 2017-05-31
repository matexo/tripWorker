package tripApp.config;

/**
 * Created by Matexo on 2017-05-07.
 */
public class AzureConfig {
    private final String accountName;
    private final String accountKey;
    private final String azureServiceName;

    public AzureConfig(String accountName, String accountKey, String azureServiceName) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.azureServiceName = azureServiceName;
    }

    public String getConfig() {
        return "DefaultEndpointsProtocol=http;"
                + "AccountName=" + accountName + ";"
                + "AccountKey=" + accountKey;
    }

    public String getAzureServiceName() {
        return azureServiceName;
    }
}
