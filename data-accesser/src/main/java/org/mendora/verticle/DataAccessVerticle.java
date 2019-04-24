package org.mendora.verticle;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mendora.facade.DataAccessFactory;
import org.mendora.facade.DataAccessing;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author menfre
 */
@Slf4j
public class DataAccessVerticle extends AbstractVerticle {
    private String username;

    private String password;

    private String host;

    private String database;

    private SQLClient mySQLClient;

    private List<DataAccessFactory> dataAccessFactories;

    private void invokeMethod(DataAccessFactory daf, Method m, SQLConnection conn) {
        try {
            m.invoke(daf, conn);
        } catch (Exception e) {
            log.error("into method: {}, error occurred.", e.getMessage(), e);
        } finally {
            conn.close();
        }
    }

    public DataAccessVerticle(String username, String password, String host, String database, List<DataAccessFactory> dataAccessFactories) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.database = database;
        this.dataAccessFactories = dataAccessFactories;
    }

    private void methodHandler(DataAccessFactory daf, Method m){
        DataAccessing annotation = m.getAnnotation(DataAccessing.class);
        mySQLClient.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection conn = res.result();
                conn.setAutoCommit(annotation.autoCommit(), res0 -> {
                    if (res0.succeeded()) {
                        invokeMethod(daf, m, conn);
                    } else {
                        log.error("into method: {}, setting auto commit failed.", m.getName());
                    }
                });
            } else {
                log.error("into method: {}, get sql connection failed.", m.getName());
            }
        });
    }

    @Override
    public void start() throws Exception {
        JsonObject mySQLClientConfig = new JsonObject()
                .put("username", StringUtils.isEmpty(username) ? "root" : username)
                .put("password", StringUtils.isEmpty(password) ? "123456" : password)
                .put("host", StringUtils.isEmpty(host) ? "localhost" : host)
                .put("database", StringUtils.isEmpty(database) ? "data_access" : database);
        mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);
        if (dataAccessFactories != null && dataAccessFactories.size() > 0) {
            dataAccessFactories.forEach(daf -> {
                Method[] methods = daf.getClass().getMethods();
                Arrays.stream(methods)
                        .filter(m -> m.isAnnotationPresent(DataAccessing.class))
                        .forEach(m -> {

                        });
            });
        }
    }

    @Override
    public void stop() throws Exception {
        if (mySQLClient != null) {
            mySQLClient.close();
        }
    }
}
