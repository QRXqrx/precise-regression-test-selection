package edu.pa.web.prts.vo;

import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-12
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CallRelationVo {
    private Method root;
    List<Method> nodes;
    List<Invocation> edges;
}
