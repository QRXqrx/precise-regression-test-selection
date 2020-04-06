package edu.pa.web.prts.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表示方法信息的java bean实体类
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@NoArgsConstructor
@Data
@Entity
@Table(name = "method", schema = "prts")
public class Method {

    @Id
    @Column(name = "project_name")
    private String projectName;

    @Id
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_changed")
    private Boolean isChanged;

    @Column(name = "is_test")
    private Boolean isTest;

    @Column(name = "version")
    private String version;

    @Column(name = "simple_name")
    private String simpleName;

    @Column(name = "class_name")
    private String className;

}
