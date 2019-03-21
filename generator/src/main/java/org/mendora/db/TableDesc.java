package org.mendora.db;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: 表结构描述
 */
public interface TableDesc {

    /**
     * 字段
     *
     * @return 字段
     */
    String field();

    /**
     * sql类型
     *
     * @return sql类型
     */
    String type();

    /**
     * 注释
     *
     * @return 注释
     */
    String comment();
}
