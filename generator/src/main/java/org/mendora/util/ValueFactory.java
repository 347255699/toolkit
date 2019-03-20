package org.mendora.util;

/**
 * @author menfre
 * @version 1.0
 * date: 2018/9/26
 * desc: 数值生成工厂
 */
@FunctionalInterface
public interface ValueFactory {
    /**
     * 根据字段产生数值
     *
     * @param field 字段名称
     * @return 数值
     * @throws Exception 产生数值时出现的异常
     */
    Object val(String field) throws Exception;
}
