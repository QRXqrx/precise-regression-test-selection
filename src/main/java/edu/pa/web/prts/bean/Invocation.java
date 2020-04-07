package edu.pa.web.prts.bean;

import edu.pa.web.prts.bean.key.InvocationKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 表示方法间调用关系的java bean实体类
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@IdClass(InvocationKey.class)
@Table(name = "invocation", schema = "prts")
public class Invocation implements Serializable {

    @Id
    @Column(name = "caller")
    private String caller;

    @Id
    @Column(name = "callee")
    private String callee;

    @Id
    @Column(name = "group_id")
    private String groupID;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_added")
    private Boolean isAdded;

    @Column(name = "version")
    private String version;

}
