package edu.pa.web.prts.service.impl;

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
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.bean.VersionInfo;
import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.service.CallRelationAnalysisService;
import edu.pa.web.prts.util.WalaUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 静态调用分析实现类。需要传入项目的根目录路径rootPath以及项目的组别号groupID。在调用analysis方法后
 * 静态调用分析服务将启动并准备好Invocation和Method信息。save方法可以在实现中定义也可以在Controller
 * 定义。
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-05
 */

@Slf4j
@Service
@Data
public class CallRelationAnalysis implements CallRelationAnalysisService {

    // 必须外部传入的属性
    // 05-07 更新，直接传入一个versionInfo
    private VersionInfo versionInfo; // 不是默认分析的就是最新版本，也有可能连续上传，但是没有分析

    // 自动注入的属性
    private InvocationOperation invocationOperation;
    private MethodOperation methodOperation;
    private UploadProperties uploadProperties; // 用于定位到分析目录

    // 可选外部传入的属性，默认为Java60RegressionExclusions.txt
    private String exPath = "src/main/resources/Java60RegressionExclusions.txt";

    // 内部分析后获得的属性
    private CHACallGraph fullCHACG;
    private CHACallGraph appCHACG;
    private List<Invocation> invocations;
    private List<Method> methods;

    @Autowired
    public CallRelationAnalysis(InvocationOperation invocationOperation, MethodOperation methodOperation, UploadProperties uploadProperties) {
        // 这四个部件需要自动注入
        this.invocationOperation = invocationOperation;
        this.methodOperation = methodOperation;
        this.uploadProperties = uploadProperties;
    }


    /**
     * 返回maven项目的target目录
     *
     * @param rootFolderPath 待分析的项目根目录
     * @return 待分析的maven项目的target文件夹目录
     */
    private String targetPath(String rootFolderPath) {
        final String TARGET = "target";
        StringBuilder targetPathBuilder = new StringBuilder(uploadProperties.getUploadFolder());
        if(!targetPathBuilder.toString().endsWith("/")) {
            targetPathBuilder.append("/");
        }
        targetPathBuilder.append(rootFolderPath);
        if(!targetPathBuilder.toString().endsWith("/")) {
            targetPathBuilder.append("/");
        }
        targetPathBuilder.append(TARGET);

        log.debug("[targetPath]" + targetPathBuilder.toString());

        return targetPathBuilder.toString();
    }


    @Override
    public CHACallGraph buildCHACG(boolean appOnly) {
        String targetPath = targetPath(versionInfo.getRootFolder());
        CHACallGraph chaCallGraph = null;
        try {
            AnalysisScope scope = WalaUtil.getDynamicScope(targetPath, exPath, this.getClass().getClassLoader());
            ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
            chaCallGraph = new CHACallGraph(cha, appOnly);
            Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
            chaCallGraph.init(entrypoints);
        } catch (IOException | ClassHierarchyException | CancelException e) {
            System.out.println("生成调用图时出错！");
            e.printStackTrace();
        }
        // 生成调用图出错时就会返回null
        return chaCallGraph;
    }

    /**
     * 生成完整CHA调用图的简单方式
     *
     * @return 完整的CHA调用图
     */
    public CHACallGraph buildCHACG() {
        return buildCHACG(false);
    }


    @Override
    public List<Invocation> generateInvocations() {

        log.info("[Generate invocations for]" + versionInfo);

        List<Invocation> invocations = new ArrayList<>();
        appCHACG.forEach((node) -> {
            IMethod caller = node.getMethod();
            String callerName = WalaUtil.signatureToFullName(caller.getSignature());
            if(WalaUtil.isArtifact(caller, versionInfo.getGroupID())) {
                Iterator<CGNode> succNodes = fullCHACG.getSuccNodes(node);
                while(succNodes.hasNext()) {
                    IMethod callee = succNodes.next().getMethod();
                    String calleeName = WalaUtil.signatureToFullName(callee.getSignature());
                    // 默认情况
                    invocations.add(
                            new Invocation(
                                    callerName,
                                    calleeName,
                                    versionInfo.getGroupID(),
                                    false,
                                    true,
                                    versionInfo.getVersion()
                            ));
                }
            }
        });
        return invocations;
    }

    @Override
    public List<Method> generateMethods() {

        log.info("[Generate Methods for]" + versionInfo);

        List<Method> methods = new ArrayList<>();
        fullCHACG.forEach((node) -> {
            IMethod walaMethod = node.getMethod();

            String fullName = WalaUtil.signatureToFullName(walaMethod.getSignature());
            // is_changed默认一开始是false
            boolean isArtifact = WalaUtil.isArtifact(walaMethod, versionInfo.getGroupID());
            boolean isTest = WalaUtil.isTestMethodNode(node);
            String simpleName = walaMethod.getName().toString();
            String className = walaMethod.getDeclaringClass().toString();

            methods.add(
                    new Method(
                            versionInfo.getGroupID(),
                            fullName,
                            false,
                            isTest,
                            versionInfo.getVersion(), // 当前的版本进行分析
                            simpleName,
                            className,
                            isArtifact
                    ));
        });

        return methods;
    }

    @Override
    public void analysis() {
        this.fullCHACG = buildCHACG();
        this.appCHACG = buildCHACG(true);
        this.invocations = generateInvocations();
        this.methods = generateMethods();
    }

    public void saveResult() {
        log.info("[Save invocations for]" + versionInfo);
        Map<String, Integer> invocationUpdateResult = invocationOperation.updateTable(invocations);
        log.debug(invocationUpdateResult.toString());

        log.info("[Save methods for]" + versionInfo);
        Map<String, Integer> methodUpdateResult = methodOperation.updateTable(methods);
        log.debug(methodUpdateResult.toString());
    }

    public void analysisAndSave() {
        analysis();
        saveResult();
    }
}
