package org.mendora;

import io.reactivex.Single;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import lombok.extern.slf4j.Slf4j;
import org.mendora.facade.DataAccessFactory;
import org.mendora.facade.DataAccessing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author menfre
 */
@Slf4j
public class DataAccessInvocationHandler implements InvocationHandler {
    private DataAccessFactory dataAccessFactory;
    private MySQLClient mySQLClient;

    public DataAccessInvocationHandler(DataAccessFactory dataAccessFactory, MySQLClient mySQLClient) {
        this.dataAccessFactory = dataAccessFactory;
        this.mySQLClient = mySQLClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(DataAccessing.class)) {
            DataAccessing annotation = method.getAnnotation(DataAccessing.class);
            return mySQLClient.rxGetConnection()
                    .map(conn -> conn.rxSetAutoCommit(annotation.autoCommit()).andThen(Single.just(conn)))
                    .map(Single::blockingGet)
                    .map(conn -> invokeMethod(dataAccessFactory, method, conn))
                    .blockingGet();
        }
        return method.invoke(dataAccessFactory, args);
    }

    private Object invokeMethod(DataAccessFactory daf, Method m, SQLConnection conn) {
        Object result = null;
        try {
            result = m.invoke(daf, conn);
        } catch (Exception e) {
            log.error("into method: {}, error occurred.", e.getMessage(), e);
        } finally {
            conn.close();
        }
        return result;
    }
}
