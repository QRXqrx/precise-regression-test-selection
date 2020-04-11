package edu.pa.web.prts.service.impl;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.jpa.VersionInfoRepository;
import edu.pa.web.prts.service.DBOperationService;
import edu.pa.web.prts.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * VersionInfo相关的数据库复杂操作的实现类
 *
 * @see VersionInfo
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@Slf4j
@Service
public class VersionInfoOperation implements DBOperationService<VersionInfo> {

    private VersionInfoRepository versionInfoRepository;

    @Autowired
    public VersionInfoOperation(VersionInfoRepository repository) {
        this.versionInfoRepository = repository;
    }

    /**
     * 包装VersionInfoRepository提供的findAll方法
     * @return List形式的全部VersionInfo
     */
    public List<VersionInfo> findAll() {
        return DBUtil.convertIterableToList(versionInfoRepository.findAll());
    }

    /**
     * 判定某个groupID的项目是不是只有一个版本
     * @param groupID 组别号
     * @return 是否只有一个版本（新旧版本是否相同）
     */
    public boolean onlyOneVersion(String groupID) {
        return findLatestVersionID(groupID).equals(findOldestVersionID(groupID));
    }

    /**
     * @param groupID 目标组号
     * @return 组号对应项目的最新版本信息
     */
    public VersionInfo findLatestVersionInfo(String groupID) {
        List<VersionInfo> versionInfos = versionInfoRepository.findAllByGroupID(groupID);
        Optional<VersionInfo> versionInfoOp = versionInfos.stream()
                .max(Comparator.comparingLong(VersionInfo::getUploadTime));

        if(versionInfoOp.isPresent()) {
            return versionInfoOp.get();
        } else {
            throw new NoSuchElementException("关于" + groupID + "的VersionID记录不存在！");
        }
    }

    /**
     * @param groupID 目标组号
     * @return 组号对应项目的最新版本号
     */
    public String findLatestVersionID(String groupID) {
        return findLatestVersionInfo(groupID).getVersion();
    }


    /**
     * @param groupID 目标组号
     * @return 组号对应项目的最旧版本信息
     */
    public VersionInfo findOldestVersionInfo(String groupID) {
        List<VersionInfo> versionInfos = versionInfoRepository.findAllByGroupID(groupID);
        Optional<VersionInfo> versionInfoOp = versionInfos.stream()
                .min(Comparator.comparingLong(VersionInfo::getUploadTime));

        if(versionInfoOp.isPresent()) {
            return versionInfoOp.get();
        } else {
            throw new NoSuchElementException("关于" + groupID + "的VersionID记录不存在！");
        }
    }

    /**
     * @param groupID 目标组号
     * @return 组号对应项目的最旧版本号
     */
    public String findOldestVersionID(String groupID) {
        return findOldestVersionInfo(groupID).getVersion();
    }

    @Override
    public void update(VersionInfo oldRecord, VersionInfo newRecord) {
        versionInfoRepository.delete(oldRecord);
        versionInfoRepository.save(newRecord);
    }

    @Override
    public Map<String, Integer> updateTable(VersionInfo newRecord) {
        List<VersionInfo> list = new ArrayList<>();
        list.add(newRecord);
        return updateTable(list);
    }

    @Override
    public Map<String, Integer> updateTable(List<VersionInfo> newRecords) {
        // 确定更新的是对应哪个groupID的项目的VersionInfo信息
        int addCnt = 0;
        int deleteCnt = 0;
        for (VersionInfo newRecord : newRecords) {
            String groupID = newRecord.getGroupID();
            List<VersionInfo> allByGroupID = versionInfoRepository.findAllByGroupID(groupID);
            if(allByGroupID.size() < 2) {
                versionInfoRepository.save(newRecord);
                addCnt++;
            } else {
                VersionInfo oldestVersionInfo =
                        allByGroupID.stream().min(Comparator.comparingLong(VersionInfo::getUploadTime)).get();
                update(oldestVersionInfo, newRecord);
                addCnt++;
                deleteCnt++;
            }
        }

        Map<String, Integer> updateResult = new HashMap<>();
        updateResult.put("添加的VersionInfo总数", addCnt);
        updateResult.put("删除的VersionInfo总数", deleteCnt);
        return updateResult;
    }

    @Override
    public void saveRecord(VersionInfo record) {
        Map<String, Integer> updateResult = updateTable(record);
        log.debug("将" + record + "存入数据库...");
        updateResult.forEach((k, v) -> log.debug(k + ": " + v));
    }

    @Override
    public int saveRecords(List<VersionInfo> records) {
        Map<String, Integer> updateResult = updateTable(records);
        log.debug("将" + records + "存入数据库...");
        updateResult.forEach((k, v) -> log.debug(k + ": " + v));
        return updateResult.get("添加的VersionInfo总数");
    }


    @Override
    public int deleteRecords(List<VersionInfo> records) {
        int cnt = 0;
        for (VersionInfo record : records) {
            if(versionInfoRepository.findById(record.getUploadTime()).isPresent()) {
                cnt++;
                versionInfoRepository.delete(record);
            }
        }
        return cnt;
    }


}
