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
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.service.impl.MethodOperation;
import edu.pa.web.prts.service.impl.VersionInfoOperation;
import edu.pa.web.prts.util.WalaUtil;
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
class MethodOperationTest {

    private String exPath = "C:/Users/QRX/IdeaProjects/precise-regression-test-selection/src/main/resources/Java60RegressionExclusions.txt";
    private final String TARGET = "target";
    private String groupID = "com.headius.invokebinder";

    @Autowired
    VersionInfoOperation versionInfoOperation;

    @Autowired
    MethodOperation methodOperation;

    @Autowired
    UploadProperties uploadProperties;

    @Test
    void testNumber(){
        System.out.println("Artifact number: " + methodOperation.findAllArtifacts(groupID).size());
        System.out.println("All Test number: " + methodOperation.findAllTest(groupID).size());
        System.out.println("Selected Test number: " + methodOperation.findSelectedTest(groupID).size());
    }

    @Test
    void testSaveMethods() throws ClassHierarchyException, CancelException, IOException {
        // 读出最新的version
        VersionInfo latestVersionInfo = versionInfoOperation.findLatestVersionInfo(groupID);
        List<Method> methods = generateMethods(latestVersionInfo, exPath);

        Map<String, Integer> resultMap = methodOperation.updateTable(methods);

        System.out.println(resultMap);
    }

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

    private String signatureToFullName(String methodSignature) {
        if(methodSignature.length() < 300) {
            return methodSignature;
        }
        return methodSignature.substring(0, 300);
    }

    private List<Method> generateMethods(VersionInfo versionInfo, String exPath) throws ClassHierarchyException, IOException, CancelException {
        String targetPath = targetPath(versionInfo.getRootFolder());
        CHACallGraph fullCHACG = buildCHACG(targetPath, exPath);

        List<Method> methods = new ArrayList<>();
        fullCHACG.forEach((node) -> {
            IMethod walaMethod = node.getMethod();

            String fullName = signatureToFullName(walaMethod.getSignature());
            // is_changed默认一开始是false
            boolean isArtifact = WalaUtil.isArtifact(walaMethod, groupID);
            boolean isTest = WalaUtil.isTestMethodNode(node);
            String simpleName = walaMethod.getName().toString();
            String className = walaMethod.getDeclaringClass().toString();

            methods.add(new Method(groupID, fullName, false, isTest, versionInfo.getVersion(), simpleName, className, isArtifact));
        });

        return methods;
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
}
