package org.mendora.db;


import java.lang.reflect.Type;

public interface TypeConverter {

    Type toJavaType(String sqlType);

    String toSqlType(Type type);

}
