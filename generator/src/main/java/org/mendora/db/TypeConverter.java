package org.mendora.db;


import java.lang.reflect.Type;

/**
 * 类型转换器
 *
 * @author menfre
 */
public interface TypeConverter {

    /**
     * 取得java类型
     *
     * @param sqlType sql类型
     * @return java类型
     */
    Type toJavaType(String sqlType);

    /**
     * 取得sqlType类型
     *
     * @param type java类型
     * @return sql类型
     */
    String toSqlType(Type type);

}
