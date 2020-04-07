package edu.pa.web.prts.bean;

import edu.pa.web.prts.bean.key.InvocationKey;
import edu.pa.web.prts.bean.key.MethodKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 表示方法信息的java bean实体类
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@IdClass(MethodKey.class)
@Table(name = "method", schema = "prts")
public class Method implements Serializable {

    @Id
    @Column(name = "group_id")
    private String groupID;

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

    @Column(name = "is_artifact")
    private Boolean isArtifact;

}
