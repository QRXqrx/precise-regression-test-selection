package edu.pa.web.prts.bean.key;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表示Invocation类的ID(PK)
 *
 * @see edu.pa.web.prts.bean.Invocation
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvocationKey implements Serializable {

    private String caller;

    private String callee;

    private String groupID;

}
