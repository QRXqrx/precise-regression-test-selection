package edu.pa.web.prts.service;

import edu.pa.web.prts.vo.VO;

import java.util.List;
import java.util.Map;

/**
 * 复杂数据库操作业务。由于三个表结构的更新条件不同，具体的实现细节需要到对应的类中去完成
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-11
 */
public interface DBOperationService<T> {

    /**
     * @param oldRecord 旧记录
     * @param newRecord 新纪录
     */
    void update(T oldRecord, T newRecord);

    /**
     * 一个重载的方法。写这个的主要目的是提示要记得重载下面那个updateTable
     * @param newRecord 待存入的记录
     * @return 一个包含两个键值对的map，分别表示本次更新添加（包含更新操作）和删除的记录数
     */
    Map<String, Integer> updateTable(T newRecord);

    /**
     * 按照一定的逻辑，更新T对应的表结构
     * @param newRecords 待存入的记录
     * @return 一个包含两个键值对的map，分别表示本次更新添加（包含更新操作）和删除的记录数
     */
    Map<String, Integer> updateTable(List<T> newRecords);

    /**
     * 按照某种逻辑插入记录
     * @param record 待存储的记录
     */
    void saveRecord(T record);

    /**
     * 批量插入记录
     * @param records 待存储的记录
     * @return 存储记录的条数
     */
    int saveRecords(List<T> records);

    /**
     * 批量删除记录
     * @param records 待删除的记录
     * @return 删除记录的条数
     */
    int deleteRecords(List<T> records);

    /**
     * @param objs 组合并生成VO的材料
     * @return 一个组合完成的VO对象
     */
    VO generateVO(Object ... objs);
}
