package edu.pa.web.prts.bean.key;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表示Method类的ID(PK)
 *
 * @see edu.pa.web.prts.bean.Method
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MethodKey implements Serializable {

    private String groupID;

    private String fullName;
}
