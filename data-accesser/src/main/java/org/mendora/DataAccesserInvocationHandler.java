package org.mendora;

import io.reactivex.Single;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import lombok.extern.slf4j.Slf4j;
import org.mendora.facade.DataAccesserFactory;
import org.mendora.facade.DataAccessing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class DataAccesserInvocationHandler implements InvocationHandler {
    private DataAccesserFactory dataAccesserFactory;
    private MySQLClient mySQLClient;

    public DataAccesserInvocationHandler(DataAccesserFactory dataAccesserFactory, MySQLClient mySQLClient) {
        this.dataAccesserFactory = dataAccesserFactory;
        this.mySQLClient = mySQLClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(DataAccessing.class)) {
            DataAccessing annotation = method.getAnnotation(DataAccessing.class);
            return mySQLClient.rxGetConnection()
                    .map(conn -> conn.rxSetAutoCommit(annotation.autoCommit()).andThen(Single.just(conn)))
                    .map(Single::blockingGet)
                    .map(conn -> invokeMethod(dataAccesserFactory, method, conn))
                    .blockingGet();
        }
        return method.invoke(dataAccesserFactory, args);
    }

    private Object invokeMethod(DataAccesserFactory daf, Method m, SQLConnection conn) {
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
