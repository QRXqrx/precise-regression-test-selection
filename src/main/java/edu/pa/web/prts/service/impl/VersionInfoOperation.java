package edu.pa.web.prts.service.impl;

import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.jpa.VersionInfoRepository;
import edu.pa.web.prts.service.DBOperationService;
import edu.pa.web.prts.util.DBUtil;
import edu.pa.web.prts.vo.VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@Service
public class VersionInfoOperation implements DBOperationService<VersionInfo> {

    private VersionInfoRepository versionInfoRepository;

    public String findLatestVersionID() {
        Iterable<VersionInfo> all = versionInfoRepository.findAll();
        List<VersionInfo> versionInfos = DBUtil.convertIterableToList(all);
        // TODO: 返回最新的版本
        return "";
    }

    @Autowired
    public VersionInfoOperation(VersionInfoRepository repository) {
        this.versionInfoRepository = repository;
    }

    @Override
    public void update(VersionInfo oldRecord, VersionInfo newRecord) {
        versionInfoRepository.delete(oldRecord);
        versionInfoRepository.save(newRecord);
    }

    @Override
    public int saveRecords(List<VersionInfo> records) {
        records.forEach((record) -> versionInfoRepository.save(record));
        return records.size();
    }

    @Override
    public int deleteRecords(List<VersionInfo> records) {
        records.forEach((record) -> versionInfoRepository.delete(record));
        return records.size();
    }


    @Override
    public VO generateVO(Object... objs) {
        return null;
    }
}
