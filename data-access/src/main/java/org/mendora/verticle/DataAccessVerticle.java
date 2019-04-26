package org.mendora.verticle;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author menfre
 */
@Slf4j
public class DataAccessVerticle extends AbstractVerticle {
    private String username;

    private String password;

    private String host;

    private String database;

    public static SQLClient MYSQL_CLIENT;

    public DataAccessVerticle(String username, String password, String host, String database) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.database = database;
    }

    @Override
    public void start() throws Exception {
        JsonObject mySQLClientConfig = new JsonObject()
                .put("username", StringUtils.isEmpty(username) ? "root" : username)
                .put("password", StringUtils.isEmpty(password) ? "123456" : password)
                .put("host", StringUtils.isEmpty(host) ? "localhost" : host)
                .put("database", StringUtils.isEmpty(database) ? "data_accesser" : database);
        MYSQL_CLIENT = MySQLClient.createShared(vertx, mySQLClientConfig);
    }

    @Override
    public void stop() throws Exception {
        if (MYSQL_CLIENT != null) {
            MYSQL_CLIENT.close();
        }
    }
}
