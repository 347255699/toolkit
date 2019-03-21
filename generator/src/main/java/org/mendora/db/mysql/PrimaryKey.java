package org.mendora.db.mysql;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author menfre
 * date: 2018/9/29
 * version: 1.0
 * desc: 主健
 */
public enum PrimaryKey {
    /**
     * 主健类型
     */
    INTEGER("Integer", Integer.class),

    STRING("String", String.class),
    ;

    final public String name;
    final public Type type;

    PrimaryKey(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public static Optional<PrimaryKey> valOf(String name) {
        for (PrimaryKey pk : values()) {
            if (pk.name.equals(name)) {
                return Optional.of(pk);
            }
        }
        return Optional.empty();
    }
}
