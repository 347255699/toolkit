package org.mendora.facade;

import io.vertx.reactivex.ext.sql.SQLConnection;
import org.mendora.DataAccessInvocationHandler;

import java.lang.reflect.Proxy;

public interface DataAccessFactory {
    SQLConnection conn = null;

    static <T> T get(T t) {
        Class<?>[] interfaces = t.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(interfaces[0].getClassLoader(), new Class<?>[]{interfaces[0]}, new DataAccessInvocationHandler<>(t));
    }
}
