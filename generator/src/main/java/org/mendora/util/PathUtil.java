package org.mendora.util;

/**
 * 路径工具
 */
public class PathUtil {
    /**
     * 应用根目录
     *
     * @return 根目录路径
     */
    public static String root() {
        return PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
}
