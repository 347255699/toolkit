package org.mendora.db;


import java.util.List;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: 数据库驱动
 */
public interface DbDriver<R> {

    /**
     * 测试连接
     *
     * @return 测试结果
     */
    boolean connectTesting();

    /**
     * 查询结
     *
     * @param statement 查询语句
     * @return 查询记过
     */
    R query(String statement) throws Exception;

    /**
     * 显示表结果连接信息
     *
     * @param tableName 表名称
     * @return 表字段信息
     */
    List<TableDesc> showFullColumns(String tableName) throws Exception;

    /**
     * 查询所有表名称
     *
     * @return 表名称
     */
    List<String> showTables() throws Exception;

    /**
     * 映射结果集为json对象
     *
     * @param r      结果集
     * @param tClass 待转换类型
     * @return json格式结果集
     */
    <T> List<T> parseObject(Class<T> tClass, R r) throws Exception;
}
