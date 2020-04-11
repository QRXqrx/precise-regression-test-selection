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

    List<Method> findAllByGroupIDAndIsArtifact(String groupID, Boolean isArtifact);

    List<Method> findAllByGroupID(String groupID);

    List<Method> findAllByGroupIDAndIsChanged(String groupID, Boolean isChanged);

    List<Method> findAllByGroupIDAndIsTest(String groupID, Boolean isTest);

    List<Method> findAllByGroupIDAndIsTestAndIsChanged(String groupID, Boolean isTest, Boolean isChanged);

}
