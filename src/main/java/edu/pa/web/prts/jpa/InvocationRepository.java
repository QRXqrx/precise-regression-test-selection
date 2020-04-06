package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.key.InvocationKey;
import org.springframework.data.repository.CrudRepository;

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

    List<Invocation> findAllByCallerAndProjectName(String caller, String projectName);

    List<Invocation> findAllByProjectName(String projectName);

    List<Invocation> findAllByProjectNameAndVersion(String projectName, String version);


}
