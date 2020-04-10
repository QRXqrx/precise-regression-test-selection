package edu.pa.web.prts.service;

import edu.pa.web.prts.vo.VO;

import java.util.List;

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
