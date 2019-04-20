
package org.mendora.scan;

import java.util.List;

/**
 * Scanner for instantiation a class set blow target package path.
 * Created by kam on 2018/2/4.
 */
public interface PackageScanner<T> {
    String FILE_SUFFIX_CLASS = ".class";
    String FILE_SUFFIX_JAR = ".jar";

    /**
     * Find target element blow package except single class name.
     *
     * @param except
     * @return
     */
    List<String> classNames(String except);

    /**
     * Find target element blow package
     *
     * @return
     */
    List<String> classNames();
}