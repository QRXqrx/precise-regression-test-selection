package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.VersionInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-07
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class VersionInfoDAOTest {

    @Autowired
    private VersionInfoRepository versionInfoRepository;

    @AfterEach
    void afterEach() {
        versionInfoRepository.deleteAll();
    }

    @Test
    void test(){
        long time = System.currentTimeMillis();
        VersionInfo versionInfo = new VersionInfo(time, "1.0", "/test_project", "test");
        versionInfoRepository.save(versionInfo);

        Optional<VersionInfo> byId = versionInfoRepository.findById(time);
        Assert.assertTrue(byId.isPresent());
        Assert.assertEquals(versionInfo, byId.get());

        versionInfoRepository.deleteById(time);
        Optional<VersionInfo> byId1 = versionInfoRepository.findById(time);
        Assert.assertFalse(byId1.isPresent());
    }

}
