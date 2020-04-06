package edu.pa.web.prts.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 上传文件配置类。该配置记录一些上传文件的属性
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "upload.properties")
public class UploadProperties {

    /**
     * 上传文件的存放位置，记录为一个绝对路径
     */
    private String uploadFolder;
}




