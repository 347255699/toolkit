package org.mendora.util;

import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

/**
 * @author menfre
 * @version 1.0
 * date: 2018/9/26
 * desc:
 */
@Slf4j
public class BeanUtils {
    private static final String FIELD_CLASS = "class";

    /**
     * 填充数据到pojo实例并提供字段映射器
     *
     * @param t  待填充实例
     * @param vf 填充数值构造器
     */
    public static <T> void filling(T t, ValueFactory vf) throws Exception {
        final BeanInfo bi = Introspector.getBeanInfo(t.getClass());
        final PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        Arrays.stream(pds)
                .filter(pd -> !FIELD_CLASS.equals(pd.getName()))
                .forEach(pd -> {
                    try {
                        pd.getWriteMethod().invoke(t, vf.val(pd.getName()));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }
}
