package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.key.MethodKey;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

public interface MethodRepository extends CrudRepository<Method, MethodKey> {

    List<Method> findAllByProjectName(String projectName);

    List<Method> findAllByProjectNameAndIsChanged(String projectName, Boolean isChanged);

    List<Method> findAllByProjectNameAndIsTest(String projectName, Boolean isTest);

    List<Method> findAllByProjectNameAndIsTestAndIsChanged(String projectName, Boolean isTest, Boolean isChanged);

}
