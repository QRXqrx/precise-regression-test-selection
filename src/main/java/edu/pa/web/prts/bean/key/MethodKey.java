package edu.pa.web.prts.bean.key;


import lombok.Data;

/**
 * 表示Method类的ID(PK)
 *
 * @see edu.pa.web.prts.bean.Method
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@Data
public class MethodKey {

    private String projectName;

    private String fullName;
}
