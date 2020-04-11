package edu.pa.web.prts.service;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.jpa.VersionInfoRepository;
import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@RunWith(SpringRunner.class)
@SpringBootTest
class InvocationOperationTest {

    private final String TARGET = "target";

    private String groupID = "com.headius.invokebinder";
    private String version1 = "1.0";
    private String version2 = "1.1";
    private String folder1 = "invokebinder-invokebinder-1.0";
    private String folder2 = "invokebinder-invokebinder-1.1";

    @Autowired
    UploadProperties uploadProperties;

    // 用来生成项目的target目录路径
    private String targetPath(String rootFolderPath) {
        StringBuilder targetPathBuidler = new StringBuilder(uploadProperties.getUploadFolder());
        if(!targetPathBuidler.toString().endsWith("/")) {
            targetPathBuidler.append("/");
        }
        targetPathBuidler.append(rootFolderPath);
        if(!targetPathBuidler.toString().endsWith("/")) {
            targetPathBuidler.append("/");
        }
        targetPathBuidler.append(TARGET);
        return targetPathBuidler.toString();
    }





    @Autowired
    VersionInfoOperation versionInfoOperation;

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void prepareVersionInfo(){
        List<VersionInfo> versionInfos = new ArrayList<>();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), version1, folder1, groupID));
        sleep();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), version2, folder2, groupID));

        versionInfoOperation.updateTable(versionInfos);
    }

    
}
