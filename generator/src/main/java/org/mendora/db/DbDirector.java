package org.mendora.db;

import lombok.extern.slf4j.Slf4j;
import org.mendora.config.SysConfig;
import org.mendora.db.mysql.MysqlDbFactory;

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
public class DbDirector<T> {

    private DbDriver<ResultSet> dbDriver;

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
    }

    public static DbDirector getInstance() {
        return new DbDirector();
    }

    public List<String> tables() throws Exception {
        return dbDriver.showTables();
    }

    public Map<String, List<TableDesc>> tableDesc() throws Exception {
        final List<String> tables = tables();
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
}
