package worker;

import config.AzureConfig;
import container.Container;

/**
 * Created by Matexo on 2017-05-06.
 */
public class Worker {

    public Container container;

    public Worker(AzureConfig azureConfig) { container = new Container(azureConfig);}
}
