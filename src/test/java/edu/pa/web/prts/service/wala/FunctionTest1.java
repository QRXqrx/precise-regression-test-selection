package edu.pa.web.prts.service.wala;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.types.annotations.Annotation;
import com.ibm.wala.util.CancelException;
import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.Method;
import edu.pa.web.prts.jpa.InvocationRepository;
import edu.pa.web.prts.jpa.MethodRepository;
import edu.pa.web.prts.properties.UploadProperties;
import edu.pa.web.prts.util.WalaUtil;
import jdk.internal.org.objectweb.asm.ClassReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * 试验静态分析功能。静态分析模块应该能够定位到待分析文件字节码的所在位置，并实施静态分析，以获取调用图
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-07
 */

@SpringBootTest
class FunctionTest1 {

    @Autowired
    private UploadProperties uploadProperties;
    
    private String rootFolder = "invokebinder-invokebinder-1.0";
    private final String TARGET = "target";
    private String exPath = "C:/Users/QRX/IdeaProjects/precise-regression-test-selection/src/main/resources/Java60RegressionExclusions.txt";
    private ClassLoader classLoader = FunctionTest1.class.getClassLoader();

    @Autowired
    private InvocationRepository invocationRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Test
    void testGenerateMethods() throws CancelException, IOException, ClassHierarchyException {
        StringBuilder dirPathBuilder = new StringBuilder(uploadProperties.getUploadFolder());
        if(!dirPathBuilder.toString().endsWith("/")) {
            dirPathBuilder.append("/");
        }
        dirPathBuilder.append(rootFolder);
        if(!dirPathBuilder.toString().endsWith("/")) {
            dirPathBuilder.append("/");
        }
        dirPathBuilder.append(TARGET);

        AnalysisScope scope = WalaUtil.getDynamicScope(dirPathBuilder.toString(), exPath, classLoader);
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph fullCHAcg = new CHACallGraph(cha);
        fullCHAcg.init(entrypoints);

        String groupID = "com.headius.invokebinder";
        String version = "1.0";

        fullCHAcg.forEach((node) -> {
            IMethod method = node.getMethod();
            String fullName = method.getSignature();
            if(fullName.length() > 250) {
                fullName = fullName.substring(0, 250);
            }
            String className = method.getDeclaringClass().getName().toString();
            String simpleName = method.getName().toString();
            Collection<Annotation> annotations = method.getAnnotations();
            boolean isTest = WalaUtil.isTestMethodNode(node);
            boolean isChanged = false;
            boolean isArtifact = fullName.contains(groupID);

            Method methodEntity = new Method(
                    groupID,
                    fullName,
                    isChanged,
                    isTest,
                    version,
                    simpleName,
                    className,
                    isArtifact
            );

            System.out.println(methodEntity);
            methodRepository.save(methodEntity);
        });

    }


    @Test
    void testGenerateInvocations() throws IOException, ClassHierarchyException, CancelException {
        StringBuilder dirPathBuilder = new StringBuilder(uploadProperties.getUploadFolder());
        if(!dirPathBuilder.toString().endsWith("/")) {
            dirPathBuilder.append("/");
        }
        dirPathBuilder.append(rootFolder);
        if(!dirPathBuilder.toString().endsWith("/")) {
            dirPathBuilder.append("/");
        }
        dirPathBuilder.append(TARGET);

        AnalysisScope scope = WalaUtil.getDynamicScope(dirPathBuilder.toString(), exPath, classLoader);
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph smallCHAcg = new CHACallGraph(cha, true);
        CHACallGraph fullCHAcg = new CHACallGraph(cha);
        fullCHAcg.init(entrypoints);
        smallCHAcg.init(entrypoints);


        // 用于静态分析缩减范围的名称
        String groupID = "com.headius.invokebinder";
        String version = "1.0";
        smallCHAcg.forEach((node) -> {
            IMethod caller = node.getMethod();
            String callerName = caller.getSignature();
            if(callerName.length() > 300) {
                callerName = callerName.substring(0, 300);
            }
            if(caller.getSignature().contains(groupID)) { // 排除掉项目无关的
                Iterator<CGNode> iter = fullCHAcg.getSuccNodes(node);
                while(iter.hasNext()) {
                    IMethod callee = iter.next().getMethod();
                    String calleeName = callee.getSignature();
                    if(calleeName.length() > 300) {
                        calleeName = calleeName.substring(0, 300);
                    }
                    Invocation invocation = new Invocation(
                            callerName, // 用方法签名来表示方法全名
                            calleeName,
                            groupID,
                            false,
                            true,
                            version
                    );

                    System.out.println(invocation);
                    invocationRepository.save(invocation);
                }
            }
        });
    }


    @Test
    void  resetInvocationRepo() {
        invocationRepository.deleteAll();
    }

    @Test
    void resetMethodRepo() {
        methodRepository.deleteAll();
    }

}
