package org.mendora.db.mysql;

import org.mendora.db.TypeConverter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;

public class MysqlTypeConverter implements TypeConverter {
    @Override
    public Type toJavaType(String sqlType) {
        return MysqlType.valOf(sqlType)
                .map(mysqlType ->  mysqlType.javaType)
                .orElse(null);
    }

    @Override
    public String toSqlType(Type type) {
        return MysqlType.valOf(type)
                .map(mysqlType ->  mysqlType.sqlType)
                .orElse(null);
    }

    public enum MysqlType {

        INT_TINYINT(int.class, "tinyint"),
        LONG_BIGINT(long.class, "bigint"),
        BIG_DECIMAL_DECIMAL(BigDecimal.class, "decimal"),
        STRING_CHAR(String.class, "char"),
        STRING_VARCHAR(String.class, "varchar"),
        STRING_TEXT(String.class, "text"),
        BOOLEAN_BOOLEAN(boolean.class, "boolean"),
        FLOAT_FLOAT(float.class, "float"),
        DATE_DATE(Date.class, "date"),
        TIMESTAMP_DATETIME(Timestamp.class, "datetime"),
        ;

        public final Type javaType;
        public final String sqlType;

        MysqlType(Type javaType, String sqlType) {
            this.javaType = javaType;
            this.sqlType = sqlType;
        }

        public static Optional<MysqlType> valOf(String sqlType) {
            for (MysqlType mysqlType : values()) {
                if (mysqlType.sqlType.equals(sqlType) || mysqlType.sqlType.startsWith(sqlType)) {
                    return Optional.of(mysqlType);
                }
            }
            return Optional.empty();
        }

        public static Optional<MysqlType> valOf(Type type) {
            for (MysqlType mysqlType : values()) {
                if (mysqlType.javaType == type) {
                    return Optional.of(mysqlType);
                }
            }
            return Optional.empty();
        }
    }
}
