package tripApp.logging;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by piotr on 17.05.17.
 */
public class LoggingTest {
    private static Logger LOGGER = Logger.getLogger(LoggingTest.class);

    /**
     * Wynik testu powinien pojawić się w wyspecyfikowanym pliku w konfiguracji log4j
     */
    @Test
    public void loggingTest() {
        LOGGER.info("Test logowania");
    }
}
