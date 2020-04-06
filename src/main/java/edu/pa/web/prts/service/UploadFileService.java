package edu.pa.web.prts.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 上传文件功能
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */
public interface UploadFileService {

    /**
     * 将上传的文件保存到指定的目录下
     *
     * @param folderPath 文件的存储位置
     * @param files 从客户端上传上来的文件夹
     */
    void storeMultiFiles(String folderPath, List<MultipartFile> files);

    /**
     *
     *
     * @param originalFileName 任意一个上传文件的原始文件名
     * @return 项目根目录名
     */
    String parseProjectRoot(String originalFileName);

}
