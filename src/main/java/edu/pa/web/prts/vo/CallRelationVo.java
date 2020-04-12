package edu.pa.web.prts.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-12
 */

@AllArgsConstructor
@Data
public class CallRelationVo {
    private String name; // 对应Method.simpleName

    private String value; // 对应method.fullName

    private List<CallRelationVo> children; // 对应子节点

//    private String pointColor = "#426ab3"; // 默认为蓝色
    private Map<String, String> itemStyle; // 默认为蓝色

    private Map<String, String> lineStyle; // 默认为深灰色

    public CallRelationVo() {
        itemStyle = new HashMap<>();// 默认为蓝色
        itemStyle.put("borderColor", "#426ab3");
        lineStyle = new HashMap<>();// 默认为蓝色
        lineStyle.put("color", "#426ab3");
    }
}
