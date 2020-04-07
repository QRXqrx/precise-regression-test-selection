package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.key.InvocationKey;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Invocation的DAO层
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

public interface InvocationRepository extends CrudRepository<Invocation, InvocationKey> {

    List<Invocation> findAllByCaller(String caller);

    List<Invocation> findAllByCallerAndGroupID(String caller, String projectName);

    List<Invocation> findAllByCallerAndGroupIDAndIsAdded(String caller, String projectName, Boolean isAdded);

    List<Invocation> findAllByCallerAndGroupIDAndIsDeleted(String caller, String projectName, Boolean isDeleted);

    List<Invocation> findAllByGroupIDAndIsAdded(String projectName, Boolean isAdded);

    List<Invocation> findAllByGroupIDAndIsDeleted(String projectName, Boolean isDeleted);

    List<Invocation> findAllByGroupID(String projectName);

    List<Invocation> findAllByGroupIDAndVersion(String projectName, String version);


}
