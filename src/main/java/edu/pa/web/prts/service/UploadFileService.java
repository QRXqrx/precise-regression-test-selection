package edu.pa.web.prts.service;

import edu.pa.web.prts.bean.VersionInfo;
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
     * @param originalFileName 任意一个上传文件的原始文件名
     * @return 项目根目录名
     */
    String parseProjectRoot(String originalFileName);


    /**
     * 将上传的version信息存入数据库
     *
     * @param groupID 项目组别号
     * @param version 项目版本号
     * @param path 文件路径，用于解压
     * @return 存入数据库的versionInfo记录
     */
    VersionInfo storeVersionInfo(String groupID, String version, String path);

}
