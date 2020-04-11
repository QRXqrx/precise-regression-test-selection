package edu.pa.web.prts.service;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.jpa.VersionInfoRepository;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class VersionInfoOperationTest {

    @Autowired
    VersionInfoOperation versionInfoOperation;
    @Autowired
    VersionInfoRepository versionInfoRepository;

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testVersionInfoOperation(){
        String groupID = "edu.pa";
        List<VersionInfo> versionInfos = new ArrayList<>();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), "version1", "folder1", groupID));
        sleep();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), "version2", "folder1", groupID));
        sleep();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), "version3", "folder1", groupID));
        sleep();

        System.out.println(versionInfos);

        versionInfoOperation.updateTable(versionInfos);
        String latestVersionID = versionInfoOperation.findLatestVersionID(groupID);
        System.out.println("[Latest Version ID]" + latestVersionID);
        Assert.assertEquals("version3", latestVersionID);

        String oldestVersionID = versionInfoOperation.findOldestVersionID(groupID);
        System.out.println("[Oldest Version ID]" + oldestVersionID);
        Assert.assertEquals("version2", oldestVersionID);

        int deleteNum = versionInfoOperation.deleteRecords(versionInfos);
        Assert.assertEquals(2, deleteNum);
        Assert.assertFalse(versionInfoRepository.findAll().iterator().hasNext());
    }


    @Test
    void testLambdaSort(){
        VersionInfo[] infos = new VersionInfo[] {
            new VersionInfo(1L, "version1", "folder1", "1"),
            new VersionInfo(3L, "version3", "folder3", "3"),
            new VersionInfo(4L, "version4", "folder4", "4"),
            new VersionInfo(2L, "version2", "folder2", "2")
        };

        List<VersionInfo> versionInfos = Arrays.asList(infos);

        Optional<VersionInfo> latestVersionInfo =
                versionInfos.stream().max(Comparator.comparingLong(VersionInfo::getUploadTime));

        if(latestVersionInfo.isPresent()) {
            System.out.println(latestVersionInfo.get().getVersion());
        } else {
            System.out.println("error");
        }
    }
}
