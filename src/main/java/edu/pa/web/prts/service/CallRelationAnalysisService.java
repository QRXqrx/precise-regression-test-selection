package edu.pa.web.prts.service;

import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import edu.pa.web.prts.bean.Invocation;
import edu.pa.web.prts.bean.Method;

import java.util.List;

/**
 * 静态分析业务。实现该接口的类需要重写方法，以完成对目标项目的静态分析。
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */
public interface CallRelationAnalysisService {

    /**
     * 用于生成一个CHA调用图
     *
     * @param appOnly 是否只针对Application节点构建CHA调用图
     * @return CHA调用图
     */
    CHACallGraph buildCHACG(boolean appOnly);

    /**
     * Wala静态分析生成一系列调用关系
     *
     * @return 一组调用关系
     */
    List<Invocation> generateInvocations();


    /**
     * Wala静态分析生成一系列方法
     *
     * @return 一组方法
     */
    List<Method> generateMethods();



    /**
     * 执行静态分析，会调用上述的方法
     */
    void analysis();

}
