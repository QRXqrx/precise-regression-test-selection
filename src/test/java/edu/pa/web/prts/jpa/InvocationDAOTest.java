package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.Invocation;
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
class InvocationDAOTest {

    @Autowired
    InvocationRepository invocationRepository;

    @BeforeEach
    void beforeEach() {
        invocationRepository.save(new Invocation("caller1", "callee1", "test", true, false, "1.0"));
        invocationRepository.save(new Invocation("caller1", "callee2", "test", false, true, "1.0"));
        invocationRepository.save(new Invocation("caller1", "callee3", "test", false, false, "1.0"));
        invocationRepository.save(new Invocation("caller1", "callee3", "test1", false, false, "1.0"));
        invocationRepository.save(new Invocation("caller1", "callee2", "test1", false, false, "1.0"));
        invocationRepository.save(new Invocation("caller1", "callee1", "test1", true, false, "1.0"));
        invocationRepository.save(new Invocation("caller2", "callee4", "test1", false, true, "1.0"));
        invocationRepository.save(new Invocation("caller2", "callee4", "test1", false, false, "1.0"));
    }

    @AfterEach
    void afterEach() {
        invocationRepository.deleteAll();
    }

    @Test
    void testFindAllByProjectNameAndIsAdded(){
        List<Invocation> all = invocationRepository.findAllByProjectNameAndIsAdded("test", true);
        dumpList(all);
        Assert.assertEquals(1, all.size());
    }


    @Test
    void testFindAllByCallerAndProjectName(){
        List<Invocation> allCallers = invocationRepository.findAllByCallerAndProjectName("caller1", "test");
        dumpList(allCallers);
        Assert.assertEquals(3, allCallers.size());
    }


    private void dumpList(List<?> list) {
        list.forEach((obj) -> System.out.println(obj.toString()));
    }
}
