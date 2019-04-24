package org.mendora;

/**
 * 路径工具
 *
 * @author menfre
 */
public class PathUtil {
	/**
	 * 应用根目录
	 *
	 * @return 根目录路径
	 */
	public static String root(Class<?> clazz) {
		String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
		return path.substring(0, path.length() - 1);
	}
}
