package org.mendora.db.mysql;

import org.mendora.db.DbSources;
import org.mendora.db.DbDriver;
import org.mendora.db.DbFactory;

import java.sql.ResultSet;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: mysql数据工厂
 */
public class MysqlDbFactory implements DbFactory {

    @Override
    public DbDriver<ResultSet> driver(DbSources dbSources) {
        return new JdbcDriver(dbSources);
    }
}
