package edu.pa.web.prts.controller;

import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.service.UploadFileService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 用来处理上传文件模块的相关映射
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */

@Slf4j
@Controller
@Data
public class UploadController {

    private UploadProperties properties; // 只要注解了Data就会自动装载
    private UploadFileService uploadFileService;

    @Autowired
    public UploadController(UploadProperties properties, UploadFileService uploadFileService) {
        this.properties = properties;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "upload_status";
    }

    @PostMapping("/uploadFolder")
    public String uploadFolder(
            @RequestParam("project-folder") MultipartFile[] folder,
            @RequestParam("project-name") String name,
            @RequestParam("project-version") String version,
            RedirectAttributes redirectAttributes
    ) {
        // 转成列表是为了好输出
        List<MultipartFile> multipartFiles = Arrays.asList(folder);

        // 直接解析出项目的根目录
        MultipartFile file = multipartFiles.get(0);
        String projectRoot = uploadFileService.parseProjectRoot(file.getOriginalFilename());
        // 将版本信息存入数据库
        log.debug("上传项目根目录:" + projectRoot);
        log.debug("上传项目名:" + name);
        log.debug("上传项目版本号:" + version);


        // 保存上传文件到本地
        if(folder.length == 0) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "上传项目失败，请上传可用的Maven项目！"
            );
            return "redirect:/uploadStatus";
        }

        List<String> originalNames = multipartFiles.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        log.debug("获取到文件: " + originalNames);

        log.debug("UploadFolderLocation: " + properties.getUploadFolder());
        uploadFileService.storeMultiFiles(properties.getUploadFolder(), multipartFiles);

        redirectAttributes.addFlashAttribute(
                "message",
                "上传项目成功！"
        );
        return "redirect:/uploadStatus";
    }

}
