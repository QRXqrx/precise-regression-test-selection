package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.key.MethodKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */
public interface MethodRepository extends CrudRepository<Method, MethodKey> {

    List<Method> findAllByProjectName(String projectName);

    List<Method> findAllByIsChangedAndProjectName(Boolean isChanged, String projectName);

    List<Method> findAllByIsTestAndProjectName(Boolean isTest, String projectName);

}
