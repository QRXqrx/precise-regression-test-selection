package edu.pa.web.prts.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 代表项目版本信息的java bean实体类
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "version_info", schema = "prts")
public class VersionInfo {

//    @Id
//    @Column(name = "upload_date")
//    private Timestamp uploadDate;
////    private Date uploadDate; // 普通的date可以存入数据库，但是不好用，弃用了
    @Id
    @Column(name = "upload_time")
    private Long uploadTime;
//    private Date uploadDate; // 普通的date可以存入数据库，但是不好用，弃用了

    @Column(name = "version")
    private String version;

    @Column(name = "root_folder")
    private String rootFolder;

    @Column(name = "group_id")
    private String groupID;

}


