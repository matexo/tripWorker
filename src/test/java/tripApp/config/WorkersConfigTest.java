package tripApp.config;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by piotr on 31.05.17.
 */
public class WorkersConfigTest {
    @Test
    public void testCreation() {
        WorkersConfig config = new WorkersConfig();
        assertNotNull(config);
    }
}
