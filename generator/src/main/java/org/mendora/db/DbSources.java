package org.mendora.db;

import lombok.Data;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: 数据源
 */
@Data
public class DbSources {

    public static final String DB_SOURCES = "dbSources";

    private String name;

    private String user;

    private String password;

    private String url;

    private String driverClass;

    private boolean enable;
}
