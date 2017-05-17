package tripApp.config;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by piotr on 17.05.17.
 */
public class WorkersConfig {
    private static final Logger LOGGER = Logger.getLogger(WorkersConfig.class);

    private final Properties properties;

    public WorkersConfig() {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("workers.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
