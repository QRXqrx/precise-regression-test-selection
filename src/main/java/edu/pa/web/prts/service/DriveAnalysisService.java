package edu.pa.web.prts.service;

/**
 * 驱动分析过程
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-06
 */
public interface DriveAnalysisService {

    /**
     * 执行shell script 完成预处理
     * @param groupID 项目组别号
     */
    void shellProcess(String groupID);

    /**
     * 执行调用分析，可能需要执行两次
     * @param groupID 项目组别号
     */
    void analysisProcess(String groupID);


    /**
     * 驱动分析过程
     * @param groupID 项目组别号
     */
    void drive(String groupID);

}
