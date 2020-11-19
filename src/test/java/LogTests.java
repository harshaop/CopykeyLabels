import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTests {
    private final static Logger log = LoggerFactory.getLogger(SmartlingAPI.class);

    @Test
    public void test() {
        log.info("test");
    }
}
