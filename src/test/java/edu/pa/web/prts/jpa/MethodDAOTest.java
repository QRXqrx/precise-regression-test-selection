package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.Method;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-07
 */

@SpringBootTest
public class MethodDAOTest {

    @Autowired
    MethodRepository methodRepository;

    @BeforeEach
    void beforeEach(){
        methodRepository.save(new Method("test1", "package.Class1.method1", false, false, "1.0", "method1", "Class1"));
        methodRepository.save(new Method("test1", "package.Class1.method2", true, false, "2.0", "method2", "Class1"));
        methodRepository.save(new Method("test1", "package.Class2.method1", false, true, "2.0", "method1", "Class2"));
        methodRepository.save(new Method("test2", "package.Class2.method", false, true, "1.0", "method", "Class2"));
        methodRepository.save(new Method("test2", "package1.Class1.method", false, true, "1.0", "method", "Class1"));
        methodRepository.save(new Method("test2", "package.Class2.method1", false, true, "1.0", "method1", "Class2"));
    }

    @AfterEach
    void afterEach() {
        methodRepository.deleteAll();
    }

    @Test
    void testFindAllByProjectNameAndIsTest(){
        List<Method> all = methodRepository.findAllByProjectNameAndIsTest("test2", true);
        dumpList(all);
        Assert.assertEquals(3, all.size());
    }

    @Test
    void testFindAllByProjectName(){
        List<Method> all = methodRepository.findAllByProjectName("test1");
        dumpList(all);
        Assert.assertEquals(3, all.size());
    }

    private void dumpList(List<?> list) {
        list.forEach((obj) -> System.out.println(obj.toString()));
    }
}
