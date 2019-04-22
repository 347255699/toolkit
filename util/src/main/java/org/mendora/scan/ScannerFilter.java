package org.mendora.scan;

/**
 * 扫描过滤器
 *
 * @author menfre
 */
@FunctionalInterface
public interface ScannerFilter {
	/**
	 * 过滤动作
	 *
	 * @param fullyQualifiedName 过滤全限类名
	 * @return 过滤后的名称
	 */
	boolean filter(String fullyQualifiedName);
}