package edu.pa.web.prts.vo;

import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.service.impl.InvocationOperation;
import edu.pa.web.prts.service.impl.MethodOperation;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VO工厂类。提供一系列静态方法，用于各类VO对象的构建
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

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
        return new ProjectVersionVo(groupID, presentVersion, previousVersion);
    }

}
