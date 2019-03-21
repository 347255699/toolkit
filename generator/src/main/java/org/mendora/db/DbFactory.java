package org.mendora.db;

import java.sql.ResultSet;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: 数据库工厂
 */
public interface DbFactory {

    /**
     * 查询驱动
     *
     * @param dbSources 数据源
     * @return 驱动
     */
    DbDriver<ResultSet> driver(DbSources dbSources);

    /**
     * 类型转换器
     *
     * @return 转换器
     */
    TypeConverter typeConverter();
}
