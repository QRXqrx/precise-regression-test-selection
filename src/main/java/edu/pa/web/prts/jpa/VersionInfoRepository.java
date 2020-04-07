package edu.pa.web.prts.jpa;

import edu.pa.web.prts.bean.VersionInfo;
import org.springframework.data.repository.CrudRepository;


import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * VersionInfo的DAO层
 *
 * @see VersionInfo
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

public interface VersionInfoRepository extends CrudRepository<VersionInfo, Long> {

    List<VersionInfo> findAllByProjectName(String projectName);

    VersionInfo findByUploadTime(Long time);


}
