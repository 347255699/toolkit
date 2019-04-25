package org.mendora;

import io.reactivex.Single;
import io.vertx.reactivex.ext.sql.SQLConnection;
import lombok.extern.slf4j.Slf4j;
import org.mendora.facade.DataAccessing;
import org.mendora.verticle.DataAccessVerticle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author menfre
 */
@Slf4j
public class DataAccessInvocationHandler<T> implements InvocationHandler {
    private T t;

    public DataAccessInvocationHandler(T t) {
        this.t = t;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        boolean autoCommit = method.isAnnotationPresent(DataAccessing.class)
                && method.getAnnotation(DataAccessing.class).autoCommit();
        return DataAccessVerticle.MYSQL_CLIENT.rxGetConnection()
                .map(conn -> conn.rxSetAutoCommit(autoCommit).andThen(Single.just(conn)))
                .map(Single::blockingGet)
                .map(conn -> invokeMethod(t, method, conn))
                .blockingGet();
    }

    private Object invokeMethod(T t, Method m, SQLConnection conn) {
        Object result = null;
        try {
            Field sqlConn = t.getClass().getDeclaredField("conn");
            sqlConn.set(t, conn);
            result = m.invoke(t, conn);
        } catch (Exception e) {
            log.error("into method: {}, error occurred.", e.getMessage(), e);
        }
        return result;
    }
}
