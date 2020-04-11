package edu.pa.web.prts.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project-version 视图对象，用于展示当前系统中可以查看的项目
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectVersionVo {

    private String groupID;

    private String presentVersion;

    private String previousVersion;


}
