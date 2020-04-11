package edu.pa.web.prts.service.impl;

import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.key.InvocationKey;
import edu.pa.web.prts.jpa.InvocationRepository;
import edu.pa.web.prts.service.DBOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Invocation相关的数据库复杂操作的实现类
 *
 * @see Invocation
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@Slf4j
@Service
public class InvocationOperation implements DBOperationService<Invocation> {

    private InvocationRepository invocationRepository;
    private VersionInfoOperation versionInfoOperation;

    @Autowired
    public InvocationOperation(InvocationRepository invocationRepository, VersionInfoOperation versionInfoOperation) {
        this.invocationRepository = invocationRepository;
        this.versionInfoOperation = versionInfoOperation;
    }

    /**
     * 获取组别号对应的所有最新版本调用关系。而且这些调用关系是用户关心的组件信息(is_artifact=true)
     * @param groupID 组别号
     * @return 最新版本的调用关系
     */
    public List<Invocation> findLatestInvocations(String groupID) {
        String latestVersionID = versionInfoOperation.findLatestVersionID(groupID);
        return invocationRepository.findAllByGroupIDAndVersion(groupID, latestVersionID);
    }

    /**
     * 显示对应组别号下面的所有最新版本的调用关系
     * @param groupID 组别号
     */
    public void showLatestInvocations(String groupID) {
        List<Invocation> latestInvocations = findLatestInvocations(groupID);
        log.debug("展示" + groupID + "对应的全部最新Artifact调用关系");
        latestInvocations.forEach((invocation) -> log.debug(invocation.toString()));
    }

    @Override
    public void update(Invocation oldRecord, Invocation newRecord) {
        invocationRepository.delete(oldRecord);
        invocationRepository.save(newRecord);
    }

    @Override
    public Map<String, Integer> updateTable(Invocation newRecord) {
        List<Invocation> list = new ArrayList<>();
        list.add(newRecord);
        return updateTable(list);
    }

    @Override
    public Map<String, Integer> updateTable(List<Invocation> newRecords) {
        int deleteCnt = 0;
        int addCnt = 0;
        List<String> groupIDs = newRecords.stream().map(Invocation::getGroupID).distinct().collect(Collectors.toList());
        // 清除所有遗留的、已经删除的调用关系
        for (String groupID : groupIDs) {
            List<Invocation> allInvocationsByGroupID = invocationRepository.findAllByGroupID(groupID);
            List<Invocation> allDeletedInvocations =
                    allInvocationsByGroupID.stream().filter(Invocation::getIsDeleted).collect(Collectors.toList());
            deleteRecords(allDeletedInvocations);
            deleteCnt = allDeletedInvocations.size();
        }
        // 逐条更新记录
        for (Invocation newRecord : newRecords) {
            // 根据key确认之前这条invocation记录是否存在
            InvocationKey key = new InvocationKey(newRecord.getCaller(), newRecord.getCallee(), newRecord.getGroupID());
            Optional<Invocation> op = invocationRepository.findById(key);
            if(op.isPresent()) {
                newRecord.setIsAdded(false); // 这条invocation不是新添加的，而是一条需要更新的、旧的invocation
                update(op.get(), newRecord); // 更新这条记录
                addCnt++;
                deleteCnt++;
            } else {
                invocationRepository.save(newRecord); // 没查到就直接插入这条invocation记录
                addCnt++;
            }
        }
        // 将旧版本的invocation记录标记为已被删除
        for (String groupID : groupIDs) {
            if(versionInfoOperation.onlyOneVersion(groupID)) {
                continue;
            }
            if(!versionInfoOperation.onlyOneVersion(groupID)) { // 该groupID对应的项目已经存在多个版本
                // 获取到旧版本
                String oldestVersionID = versionInfoOperation.findOldestVersionID(groupID);
                // 获取到所有当前版本还位旧版本的Invocation记录。这些记录在本次显示中不会被选中，并且会在第三个版本进入后被删除
                List<Invocation> allInvocationsWithOldVersion =
                        invocationRepository.findAllByGroupIDAndVersion(groupID, oldestVersionID);
                for (Invocation oldInvocation : allInvocationsWithOldVersion) {
                    Invocation newInvocation = new Invocation(
                            oldInvocation.getCaller(),
                            oldInvocation.getCallee(),
                            oldInvocation.getGroupID(),
                            true, // 这条记录已经被抛弃了
                            false,
                            oldInvocation.getVersion() // 暂定是保留老版本
                    );
                    update(oldInvocation, newInvocation); // 更新为船新版本
                }
            }
        }

        Map<String, Integer> updateResult = new HashMap<>();
        updateResult.put("添加的Invocation总数", addCnt);
        updateResult.put("删除的Invocation总数", deleteCnt);
        return updateResult;
    }

    @Override
    public void saveRecord(Invocation record) {
        Map<String, Integer> updateResult = updateTable(record);
        log.debug("将" + record + "存入数据库...");
        updateResult.forEach((k, v) -> log.debug(k + ": " + v));
    }

    @Override
    public int saveRecords(List<Invocation> records) {
        Map<String, Integer> updateResult = updateTable(records);
        log.debug("将" + records + "存入数据库...");
        updateResult.forEach((k, v) -> log.debug(k + ": " + v));
        return updateResult.get("添加的Invocation总数");
    }

    @Override
    public int deleteRecords(List<Invocation> records) {
        int cnt = 0;
        for (Invocation record : records) {
            if(invocationRepository.findById(new InvocationKey(record.getCaller(), record.getCallee(), record.getGroupID())).isPresent()) {
                cnt++;
                invocationRepository.delete(record);
            }
        }
        return cnt;
    }

}
