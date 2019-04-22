
package org.mendora.scan;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 包扫描器
 *
 * @author menfre
 */
public interface PackageScanner<T> {
	String FILE_SUFFIX_CLASS = ".class";
	String FILE_SUFFIX_JAR = ".jar";

	/**
	 * 取得一组全限类名除了指定的类名外
	 *
	 * @param except 指定类名
	 * @return 类名列表
	 */
	List<String> fullClassNames(String except);

	/**
	 * 取得一组全限类名
	 *
	 * @return 类名列表
	 */
	List<String> fullClassNames();

	/**
	 * 创建一组类实例
	 *
	 * @param classNames      待实例化类名列表
	 * @param annotationClass 筛选依据
	 * @return 类名列表
	 */
	List<T> newInstances(List<String> classNames, Class<? extends Annotation> annotationClass);
}