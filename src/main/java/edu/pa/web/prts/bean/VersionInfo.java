package edu.pa.web.prts.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 代表项目版本信息的java bean实体类
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@NoArgsConstructor
@Data
@Entity
@Table(name = "version_info", schema = "prts")
public class VersionInfo {

    @Id
    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "version")
    private String version;

    @Column(name = "root_folder")
    private String rootFolder;

    @Column(name = "project_name")
    private String projectName;

}


