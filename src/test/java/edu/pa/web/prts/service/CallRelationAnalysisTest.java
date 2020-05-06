package edu.pa.web.prts.service;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.service.impl.CallRelationAnalysis;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试静态分析服务类的功能
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-06
 */

@RunWith(SpringRunner.class)
@SpringBootTest
class CallRelationAnalysisTest {

    private String folder1 = "invokebinder-invokebinder-1.0";
    private String folder2 = "invokebinder-invokebinder-1.1";
    private String groupID = "com.headius.invokebinder";

    @Autowired
    CallRelationAnalysis callRelationAnalysis;

    @Autowired
    VersionInfoOperation versionInfoOperation;

    @Test
    void testAnalysisAndSave2(){
        versionInfoOperation.updateTable(new VersionInfo(System.currentTimeMillis(), "1.1", folder2, groupID));

        callRelationAnalysis.setRootPath(folder2);
        callRelationAnalysis.setGroupID(groupID);

        callRelationAnalysis.analysisAndSave();
    }

    @Test
    void testAnalysisAndSave1(){
        versionInfoOperation.updateTable(new VersionInfo(System.currentTimeMillis(), "1.0", folder1, groupID));

        callRelationAnalysis.setRootPath(folder1);
        callRelationAnalysis.setGroupID(groupID);

        callRelationAnalysis.analysisAndSave();
    }

    @Test
    void testAnalysis() {
        callRelationAnalysis.setRootPath(folder1);
        callRelationAnalysis.setGroupID(groupID);

        Assert.assertNotNull(callRelationAnalysis.getInvocationOperation());
        Assert.assertNull(callRelationAnalysis.getInvocations());

        callRelationAnalysis.analysis();

        Assert.assertNotNull(callRelationAnalysis.getInvocations());
    }


}
