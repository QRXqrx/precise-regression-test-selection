package edu.pa.web.prts.service;

import edu.pa.web.prts.service.impl.DriveAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-06
 */

@RunWith(SpringRunner.class)
@SpringBootTest
class DriveAnalysisTest {

    String groupID = "com.headius.invokebinder";

    @Autowired
    DriveAnalysis driveAnalysis;

    @Test
    void testAnalysis(){
        driveAnalysis.analysisProcess(groupID);
    }

    @Test
    void testShell(){
        driveAnalysis.shellProcess(groupID);
    }


}
