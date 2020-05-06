package edu.pa.web.prts.service.impl;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-06
 */
@Slf4j
@Service
public class UploadFile implements UploadFileService {

    VersionInfoOperation versionInfoOperation;

    @Autowired
    public UploadFile(VersionInfoOperation versionInfoOperation) {
        this.versionInfoOperation = versionInfoOperation;
    }

    /**
     * 确保目录存在，不存在则创建
     * @param filePath 目标路径，用于创建目录
     */
    private static void makeDir(String filePath) {
        if (filePath.lastIndexOf('/') > 0) {
            String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                boolean mkdirs = dir.mkdirs();
                if(mkdirs) {
                    log.debug("创建本地文件夹: " + dir.getAbsolutePath());
                }
            }
        }
    }

    public void storeMultiFiles(String folderPath, MultipartFile[] files) {
        storeMultiFiles(folderPath, Arrays.asList(files));
    }

    @Override
    public void storeMultiFiles(String folderPath, List<MultipartFile> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        if (folderPath.endsWith("/")) {
            folderPath = folderPath.substring(0, folderPath.length() - 1);
        }
        for (MultipartFile file : files) {
            String filePath = folderPath + "/" + file.getOriginalFilename();
            makeDir(filePath);
            File dest = new File(filePath);
            try {
                // 使用相对路径会出错
                file.transferTo(dest);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String parseProjectRoot(String originalFileName) {
        int loc = originalFileName.indexOf('/');
        return originalFileName.substring(0, loc);
    }

    @Override
    public VersionInfo storeVersionInfo(String groupID, String version, String path) {
        VersionInfo versionInfo = new VersionInfo(System.currentTimeMillis(), version, path, groupID);
        versionInfoOperation.updateTable(versionInfo);
        return versionInfo;
    }
}
