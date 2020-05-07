package edu.pa.web.prts.service.impl;

import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.key.MethodKey;
import edu.pa.web.prts.jpa.InvocationRepository;
import edu.pa.web.prts.jpa.MethodRepository;
import edu.pa.web.prts.service.DBOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Method相关的数据库复杂操作的实现类
 *
 * @see Method
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */
@Slf4j
@Service
public class MethodOperation implements DBOperationService<Method> {

    private MethodRepository methodRepository;
    private InvocationRepository invocationRepository;

    public Method findByID(String groupID, String fullName) {
        return findByID(new MethodKey(groupID, fullName));
    }

    public Method findByID(MethodKey id) {
        Optional<Method> op = methodRepository.findById(id);
        return op.orElse(null); // 与下面几行等效
//        if(op.isPresent()) {
//            return op.get();
//        }
//        return null; // 表示未找到
    }

    /**
     * 查询某个项目中所有的组件节点
     * @param groupID 项目组别号
     * @return 所有的组件节点
     */
    public List<Method> findAllArtifacts(String groupID) {
        return methodRepository.findAllByGroupIDAndIsArtifact(groupID, true);
    }

    /**
     * 查询某个项目中所有与变更相关的测试方法
     * @param groupID 项目组别号
     * @return 所有与变更相关的测试方法
     */
    public List<Method> findSelectedTest(String groupID) {
        return methodRepository.findAllByGroupIDAndIsTestAndIsChanged(groupID, true, true);
    }

    public List<Method> findAllTest(String groupID) {
        return methodRepository.findAllByGroupIDAndIsTest(groupID, true);
    }

    @Autowired
    public MethodOperation(MethodRepository methodRepository, InvocationRepository invocationRepository) {
        this.methodRepository = methodRepository;
        this.invocationRepository = invocationRepository;
    }

    @Override
    public void update(Method oldRecord, Method newRecord) {
        methodRepository.delete(oldRecord);
        methodRepository.save(newRecord);
    }

    @Override
    public Map<String, Integer> updateTable(Method newRecord) {
        return null;
    }

    /**
     * 判断一条method记录是否与变更相关
     * @param methodFullName 一个方法全名
     * @param groupID 方法所在组别名
     * @return 是否与变更相关
     */
    private boolean isChangeRelated(String methodFullName, String groupID) {
        // 找到所有以这个方法为调用者的invocation记录
        List<Invocation> allCallerInvocations = invocationRepository.findAllByCallerAndGroupID(methodFullName, groupID);
        if(allCallerInvocations.isEmpty()) { // 这是一个出口位置。只有调用图最末端的方法才不会有调用记录（或者是Primordial的方法）
            return false;
        }
        for (Invocation invocation : allCallerInvocations) {
            if(invocation.getIsAdded() || invocation.getIsDeleted()) {
                // 与这个方法节点相关的一条调用是新添加的或者是新删除的，说明这个节点受变更影响
                return true;
            }

            String calleeFullName = invocation.getCallee();
            if(calleeFullName.equals(methodFullName)) { // 防止项目中有递归调用产生无限循环
                continue;
            }
            // 递归。如果这个method的callee受变更影响，那么这个方法节点也是受变更影响的
            if(isChangeRelated(calleeFullName, groupID)) {
                return true;
            }
        }
        // 经过所有筛查之后仍然没有返回的，就为与变更无关的方法节点
        return false;
    }

    @Override
    public Map<String, Integer> updateTable(List<Method> newRecords) {
        // 先拿到所有的groupID
        List<String> groupIDs = newRecords.stream().map(Method::getGroupID).collect(Collectors.toList());

        int deleteCnt = 0;
        int addCnt = 0;
        // 删除groupID对应的所有节点
        for (String groupID : groupIDs) {
            List<Method> allMethodsByGroupID = methodRepository.findAllByGroupID(groupID);
            if(!allMethodsByGroupID.isEmpty()) {
                deleteCnt += deleteRecords(allMethodsByGroupID);
            }
        }

        // 修改newRecords中的所有记录的is_changed位
        for (Method newRecord : newRecords) {
            newRecord.setIsChanged(isChangeRelated(newRecord.getFullName(), newRecord.getGroupID()));
            methodRepository.save(newRecord);

            log.debug("[Now Save]" + newRecord);

            addCnt++;
        }
        Map<String, Integer> updateResult = new HashMap<>();
        updateResult.put("添加的Method总数", addCnt);
        updateResult.put("删除的Method总数", deleteCnt);
        return updateResult;
    }

    @Override
    public void saveRecord(Method record) {
        Map<String, Integer> updateResult = updateTable(record);
        log.debug("将" + record + "存入数据库...");
        updateResult.forEach((k, v) -> log.debug(k + ": " + v));
    }

    @Override
    public int saveRecords(List<Method> records) {
        Map<String, Integer> updateResult = updateTable(records);
        log.debug("将" + records + "存入数据库...");
        updateResult.forEach((k, v) -> log.debug(k + ": " + v));
        return updateResult.get("添加的Method总数");
    }

    @Override
    public int deleteRecords(List<Method> records) {
        int cnt = 0;
        for (Method record : records) {
            // 这样做能够使得返回的cnt是正确的
            if(methodRepository.findById(new MethodKey(record.getGroupID(), record.getFullName())).isPresent()) {
                cnt++;
                methodRepository.delete(record);
            }
        }
        return cnt;
    }

}
