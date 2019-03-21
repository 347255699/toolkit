package org.mendora.db;

import lombok.extern.slf4j.Slf4j;
import org.mendora.config.SysConfig;
import org.mendora.db.mysql.MysqlDbFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: 数据库指导者
 */
@Slf4j
public class DbDirector {

    private DbDriver<ResultSet> dbDriver;

    private TypeConverter typeConverter;

    private DbDirector() {
        final List<DbSources> dbSources = SysConfig.dbSources
                .stream()
                .filter(DbSources::isEnable)
                .collect(Collectors.toList());

        if (dbSources.isEmpty()) {
            log.error("db sources not found.");
            return;
        }

        final DbFactory factory = new MysqlDbFactory();
        dbDriver = factory.driver(dbSources.get(0));
        typeConverter = factory.typeConverter();
    }

    /**
     * 取得实例
     *
     * @return 当前对象实例
     */
    public static DbDirector getInstance() {
        return new DbDirector();
    }

    /**
     * 连接测试
     *
     * @return 测试结果
     */
    public boolean connectTest() {
        return dbDriver.connectTesting();
    }

    /**
     * 取得数据所有表名称
     *
     * @return 表名称列表
     * @throws Exception 异常
     */
    public List<String> tables() throws Exception {
        return dbDriver.showTables();
    }

    /**
     * 取得表结构信息
     *
     * @param tables 表名称
     * @return 表结构信息
     */
    public Map<String, List<TableDesc>> tableDesc(List<String> tables) {
        final Map<String, List<TableDesc>> tableDescs = new HashMap<>(tables.size());
        tables.forEach(tableName -> {
            try {
                tableDescs.put(tableName, dbDriver.showFullColumns(tableName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return tableDescs;
    }

    /**
     * 转换类型 sqlType -> javaType
     *
     * @param sqlType sql数据类型
     * @return java类型
     */
    public Type toJavaType(String sqlType) {
        return typeConverter.toJavaType(sqlType);
    }
}
