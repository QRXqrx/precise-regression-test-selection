package edu.pa.web.prts.service;

import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;

/**
 * 静态分析业务。实现该接口的类需要重写方法，以完成对目标项目的静态分析，并将分析得到的Method信息与
 * Invocation信息正确存入数据库。
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
     * 将分析得到的Invocation信息存入数据库
     */
    void saveInvocations();

    /**
     * 将分析得到的Method信息存入数据库
     */
    void saveMethods();

    /**
     * 执行静态分析，会调用上述的方法。
     */
    void analysis();

}
