package edu.pa.web.prts.vo;

import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.service.impl.InvocationOperation;
import edu.pa.web.prts.service.impl.MethodOperation;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * VO工厂类。提供一系列静态方法，用于各类VO对象的构建
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@Slf4j
@Service
public class VOFactory {

    VersionInfoOperation versionInfoOperation;

    InvocationOperation invocationOperation;

    MethodOperation methodOperation;

    @Autowired
    public VOFactory(VersionInfoOperation versionInfoOperation, InvocationOperation invocationOperation, MethodOperation methodOperation) {
        this.versionInfoOperation = versionInfoOperation;
        this.invocationOperation = invocationOperation;
        this.methodOperation = methodOperation;
    }


    /**
     * 生成CallRelationVo，用于在页面上展示树状调用关系图
     * @param groupID 组别
     * @param fullName 方法全名
     * @return CallRelation 调用关系视图
     */
    public CallRelationVo makeCallRelation(String groupID, String fullName) {
        // 找到根节点
        Method rootMethod = methodOperation.findByID(groupID, fullName);

        if(rootMethod == null) { // 如果这个方法不存在
            return null;
        }

        CallRelationVo callRelation = new CallRelationVo();
        callRelation.setName(rootMethod.getSimpleName());
        callRelation.setValue(rootMethod.getFullName());

        if(rootMethod.getIsChanged()) {
            Map<String, String> itemStyle = new HashMap<>();
            itemStyle.put("borderColor", "#d71345");
            Map<String, String> lineStyle = new HashMap<>();
            lineStyle.put("color", "#d71345");

            callRelation.setItemStyle(itemStyle); // 设置成红色，表示这个节点与变更相关
            callRelation.setLineStyle(lineStyle); // 设置成红色，表示这条边与变更相关
        }

        // 找到所有以rootMethod方法为根的调用关系
        List<Invocation> invocations = invocationOperation.findAllByMethod(rootMethod);
        if(invocations.isEmpty()) { // 没有子节点
            return callRelation;
        }
        // 有子节点
        List<CallRelationVo> childrenNodes = new ArrayList<>();
        for(Invocation invocation : invocations) {
            CallRelationVo childrenVo = makeCallRelation(invocation.getGroupID(), invocation.getCallee());
            if(childrenVo == null) {
                continue;
            }
            childrenNodes.add(childrenVo);
        }

        callRelation.setChildren(childrenNodes);
        return callRelation;

    }


    public List<Method> makePaginationArtifactListVo(String groupID, int start, int end) {
        List<Method> allArtifacts = makeArtifactsList(groupID);
        List<Method> subArtifactList = new ArrayList<>();
        // 起始不能小于0，结束不能超过allArtifacts的长度
        if(start < 0 ) {
            start = 0;
        }
        if(end > allArtifacts.size()) {
            end = allArtifacts.size();
        }

        for(int i = start; i < end ; i++) {
            subArtifactList.add(allArtifacts.get(i));
        }
        return subArtifactList;
    }


    public List<Method> makeArtifactsList(String groupID) {
        return methodOperation.findAllArtifacts(groupID);
    }

    /**
     * 每个groupID一条记录，返回所有的ProjectVersionVO
     * @return projectVersionVo列表
     */
    public List<ProjectVersionVo> makeProjectVersionVoList() {
        List<VersionInfo> versionInfos = versionInfoOperation.findAll();
        List<String> groupIDs = versionInfos.stream().map(VersionInfo::getGroupID).distinct().collect(Collectors.toList());

        List<ProjectVersionVo> projectVersionVoList = new ArrayList<>();
        for (String groupID : groupIDs) {
            projectVersionVoList.add(makeProjectVersionVo(groupID));
        }

        return projectVersionVoList;
    }

    /**
     * 按照groupID找到对应的version信息，组合成ProjectVersionVo
     * @param groupID 组别号
     * @return 组合完成的ProjectVersionVo
     */
    public ProjectVersionVo makeProjectVersionVo(String groupID) {
        String presentVersion = versionInfoOperation.findLatestVersionID(groupID);
        String previousVersion = versionInfoOperation.findOldestVersionID(groupID);
        boolean upToDate = versionInfoOperation.isUpToDate(groupID);
        return new ProjectVersionVo(groupID, presentVersion, previousVersion, upToDate);
    }

}
