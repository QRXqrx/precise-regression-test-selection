package edu.pa.web.prts.service;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.CancelException;
import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.jpa.VersionInfoRepository;
import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.service.impl.InvocationOperation;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import edu.pa.web.prts.util.WalaUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */

@RunWith(SpringRunner.class)
@SpringBootTest
class InvocationOperationTest {

    private final String TARGET = "target";

    private String groupID = "com.headius.invokebinder";
    private String version1 = "1.0";
    private String version2 = "1.1";
    private String folder1 = "invokebinder-invokebinder-1.0";
    private String folder2 = "invokebinder-invokebinder-1.1";

    @Autowired
    UploadProperties uploadProperties;

    // 用来生成项目的target目录路径
    private String targetPath(String rootFolderPath) {
        StringBuilder targetPathBuidler = new StringBuilder(uploadProperties.getUploadFolder());
        if(!targetPathBuidler.toString().endsWith("/")) {
            targetPathBuidler.append("/");
        }
        targetPathBuidler.append(rootFolderPath);
        if(!targetPathBuidler.toString().endsWith("/")) {
            targetPathBuidler.append("/");
        }
        targetPathBuidler.append(TARGET);
        return targetPathBuidler.toString();
    }

    private CHACallGraph buildCHACG(String targetPath, String exPath, boolean appOnly) throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(targetPath, exPath, this.getClass().getClassLoader());
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        CHACallGraph chaCallGraph = new CHACallGraph(cha, appOnly);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
        chaCallGraph.init(entrypoints);
        return chaCallGraph;
    }

    private CHACallGraph buildCHACG(String targetPath, String exPath) throws ClassHierarchyException, CancelException, IOException {
        return buildCHACG(targetPath, exPath, false);
    }

    @Autowired
    VersionInfoRepository versionInfoRepository;

    @Autowired
    InvocationOperation invocationOperation;

    private String exPath = "C:/Users/QRX/IdeaProjects/precise-regression-test-selection/src/main/resources/Java60RegressionExclusions.txt";

    private boolean isArtifact(IMethod method, String groupID) { // 一定要按照原生signature来判定
        return method.getSignature().contains(groupID);
    }

    private List<Invocation> generateInvocations(VersionInfo versionInfo, String exPath) throws ClassHierarchyException, CancelException, IOException {
        String targetPath = targetPath(versionInfo.getRootFolder());
        CHACallGraph fullCHACG = buildCHACG(targetPath, exPath);
        CHACallGraph appCHACG = buildCHACG(targetPath, exPath, true);

        List<Invocation> invocations = new ArrayList<>();
        appCHACG.forEach((node) -> {
            IMethod caller = node.getMethod();
            String callerName = WalaUtil.signatureToFullName(caller.getSignature());
            if(isArtifact(caller, groupID)) {
                Iterator<CGNode> succNodes = fullCHACG.getSuccNodes(node);
                while(succNodes.hasNext()) {
                    IMethod callee = succNodes.next().getMethod();
                    String calleeName = WalaUtil.signatureToFullName(callee.getSignature());
                    // 默认情况
                    invocations.add(new Invocation(callerName, calleeName, groupID, false, true, versionInfo.getVersion()));
                }
            }
        });

        return invocations;
    }

    @Test
    void testSaveVersion2() throws ClassHierarchyException, IOException, CancelException {
        VersionInfo versionInfo2 = new VersionInfo(System.currentTimeMillis(), version2, folder2, groupID);
        versionInfoOperation.updateTable(versionInfo2);
        Assert.assertEquals(2, versionInfoRepository.findAllByGroupID(groupID).size());

        // 点击之后传递的就应该是一个json对象，这个对象代表的就是versionInfo。

        // 更新都应该找最新的versionInfo
        VersionInfo latestVersionInfo = versionInfoOperation.findLatestVersionInfo(groupID);
        Assert.assertEquals(versionInfo2, latestVersionInfo);

        List<Invocation> invocations = generateInvocations(latestVersionInfo, exPath);
        Map<String, Integer> updateResult = invocationOperation.updateTable(invocations);
        System.out.println(updateResult);
    }


    @Test
    void testSaveVersion1() throws ClassHierarchyException, IOException, CancelException {
        VersionInfo versionInfo1 = new VersionInfo(System.currentTimeMillis(), version1, folder1, groupID);
        versionInfoOperation.updateTable(versionInfo1);
        Assert.assertEquals(1, versionInfoRepository.findAllByGroupID(groupID).size());

        // 点击之后传递的就应该是一个json对象，这个对象代表的就是versionInfo1。

        VersionInfo latestVersionInfo = versionInfoOperation.findLatestVersionInfo(groupID);
        Assert.assertEquals(versionInfo1, latestVersionInfo);

        List<Invocation> invocations = generateInvocations(latestVersionInfo, exPath);
        Map<String, Integer> updateResult = invocationOperation.updateTable(invocations);
        System.out.println(updateResult);
    }




    @Autowired
    VersionInfoOperation versionInfoOperation;

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Test
    void prepareVersionInfo(){
        List<VersionInfo> versionInfos = new ArrayList<>();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), version1, folder1, groupID));
        sleep();
        versionInfos.add(new VersionInfo(System.currentTimeMillis(), version2, folder2, groupID));

        versionInfoOperation.updateTable(versionInfos);
    }

    
}
