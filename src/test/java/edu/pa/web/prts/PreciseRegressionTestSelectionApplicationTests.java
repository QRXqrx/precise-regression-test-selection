package edu.pa.web.prts;

import edu.pa.web.prts.properties.UploadProperties;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class PreciseRegressionTestSelectionApplicationTests {

    @Autowired
    UploadProperties properties;

    @Test
    void contextLoads() {
        System.out.println(properties.getUploadFolder());
    }

}
