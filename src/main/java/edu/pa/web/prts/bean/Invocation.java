package edu.pa.web.prts.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表示方法间调用关系的java bean
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "invocation", schema = "prts")
public class Invocation {

    @Id
    @Column(name = "caller")
    private String caller;

    @Id
    @Column(name = "callee")
    private String callee;

    @Id
    @Column(name = "project_name")
    private String projectName;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_added")
    private Boolean isAdded;

    @Column(name = "version")
    private String version;

}
