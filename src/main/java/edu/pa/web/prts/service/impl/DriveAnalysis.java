package edu.pa.web.prts.service.impl;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.service.DriveAnalysisService;
import edu.pa.web.prts.util.ShellCommendUtil;
import edu.pa.web.prts.util.enums.ShellPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分析驱动服务的实现类
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-06
 */
@Slf4j
@Service
public class DriveAnalysis implements DriveAnalysisService {

    VersionInfoOperation versionInfoOperation; // 需要根据groupID查询项目信息。项目的footRoot需要修改一下
    CallRelationAnalysis callRelationAnalysis; // 用于静态分析
    UploadProperties uploadProperties; //用于获取上传文件所在的文件夹

    /**
     * 通过压缩文件的路径推测解压后文件夹的路径
     *
     * @param zipPath 压缩文件路径
     * @return 一个字符串，表示解压后的文件路径
     */
    private String zipToFolder(String zipPath) {
        if(!zipPath.endsWith(".zip")) {
            throw new IllegalArgumentException("Invalid zip path: " + zipPath);
        }
        return zipPath.replace(".zip", "");
    }

    @Autowired
    public DriveAnalysis(VersionInfoOperation versionInfoOperation, CallRelationAnalysis callRelationAnalysis, UploadProperties uploadProperties) {
        this.versionInfoOperation = versionInfoOperation;
        this.callRelationAnalysis = callRelationAnalysis;
        this.uploadProperties = uploadProperties;
    }

    private void executeShell(VersionInfo versionInfo) {
        log.debug("[Now execute shell for]" + versionInfo + "...");
        // 执行脚本
        String zipPath = versionInfo.getRootFolder(); // 现在还是zip形式
        String folderPath = zipToFolder(zipPath); // 解压出来的folder路径
        String uploadFolderPath = uploadProperties.getUploadFolder(); // 上传文件存放的目录

        final String SCRIPT_PATH = "src/main/resources/shell/drive-analysis.sh";

        log.debug("[folder path]" + folderPath);
        log.debug("[zip path]" + zipPath);
        log.debug("[upload folder path]" + uploadFolderPath);

        ShellCommendUtil.executeCommand(
                new String[] {
                        ShellPath.WINDOWS_GIT_BASH.getPath(),
                        SCRIPT_PATH,
                        zipPath, // unzip, rm等
                        folderPath, // cd, mvn install等
                        uploadFolderPath // 要在这个文件夹下完成解压
                }
        );

        // 更新VersionInfo
        VersionInfo newVersionInfo = new VersionInfo(
            versionInfo.getUploadTime(),
            versionInfo.getVersion(),
            folderPath, // 改成文件夹路径，其余不变
            versionInfo.getGroupID(),
            versionInfo.getIsAnalyzed()
        );
        versionInfoOperation.update(versionInfo, newVersionInfo);
    }

    @Override
    public void shellProcess(String groupID) {
        // 一个项目有两个版本时，需要检查两个版本的分析情况
        if(!versionInfoOperation.onlyOneVersion(groupID)) {
            VersionInfo oldVersion = versionInfoOperation.findOldestVersionInfo(groupID);
            // 老版本尚未分析
            if(!oldVersion.getIsAnalyzed()) {
                executeShell(oldVersion);
            }
        }

        VersionInfo newVersion = versionInfoOperation.findLatestVersionInfo(groupID);
        // 新版本尚未更新
        if(!newVersion.getIsAnalyzed()) {
            executeShell(newVersion);
        }
    }

    private void executeAnalysis(VersionInfo versionInfo) {
        log.debug("[Now execute analysis for]" + versionInfo + "...");
        callRelationAnalysis.setGroupID(versionInfo.getGroupID());
        callRelationAnalysis.setRootPath(versionInfo.getRootFolder());

        callRelationAnalysis.analysis();
//        callRelationAnalysis.analysisAndSave(); // 暂时先不save

        log.debug("[Invocations Size]" + callRelationAnalysis.getInvocations().size());
        log.debug("[Methods Size]" + callRelationAnalysis.getMethods().size());

        // 更新VersionInfo
        VersionInfo newVersionInfo = new VersionInfo(
                versionInfo.getUploadTime(),
                versionInfo.getVersion(),
                versionInfo.getRootFolder(),
                versionInfo.getGroupID(),
                true // 修改为已经分析完毕，其余不变
        );
        versionInfoOperation.update(versionInfo, newVersionInfo);
    }

    @Override
    public void analysisProcess(String groupID) {
        // 两个版本，需要检查旧版本的分析情况
        if(!versionInfoOperation.onlyOneVersion(groupID)) {
            VersionInfo oldVersion = versionInfoOperation.findOldestVersionInfo(groupID);
            if(!oldVersion.getIsAnalyzed()) {
                // 分析旧版本
                executeAnalysis(oldVersion);
            }
        }

        VersionInfo newVersion = versionInfoOperation.findLatestVersionInfo(groupID);
        // 新版本尚未更新
        if(!newVersion.getIsAnalyzed()) {
            // 分析新版本
            executeAnalysis(newVersion);
        }

    }

    @Override
    public void drive(String groupID) {
        // 先执行shell
        shellProcess(groupID);
        // 再执行call relation analysis
        analysisProcess(groupID);
    }
}
