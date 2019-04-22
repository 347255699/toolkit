package org.mendora.scan;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * 类包扫描器
 *
 * @param <T>
 * @author menfre
 */
@RequiredArgsConstructor
public class PackageScannerImpl<T> implements PackageScanner<T> {
	@NonNull
	private String packagePath;
	@NonNull
	private ClassLoader classLoader;

	@Override
	public List<String> fullClassNames(String except) {
		return classNames(this.packagePath, new ArrayList<>(), name -> !except.equals(name));
	}

	@Override
	public List<String> fullClassNames() {
		return classNames(packagePath, new ArrayList<>(), name -> true);
	}

	private Optional<Class<?>> classForName(String className) {
		try {
			return Optional.of(Class.forName(className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	private Optional<T> newInstance(Class<?> clazz) {
		try {
			T t = (T) clazz.newInstance();
			return Optional.of(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<T> newInstances(List<String> classNames, Class<? extends Annotation> annotationClass) {
		return classNames.stream()
			.map(this::classForName)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(clazz -> !clazz.isInterface() && !clazz.isEnum())
			.filter(clazz -> clazz.isAnnotationPresent(annotationClass))
			.map(this::newInstance)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
	}

	/**
	 * 扫描包路径下的类名
	 *
	 * @param packagePath 包路径
	 * @param classNames  类名列表
	 * @return 类名列表
	 */
	private List<String> classNames(String packagePath, List<String> classNames, ScannerFilter filter) {
		// "." -> "/"
		String splashPath = dotToSplash(packagePath);
		URL url = classLoader.getResource(splashPath);
		if (url == null) {
			return new ArrayList<>();
		}
		String filePath = getRootPath(url);
		// get classes in that package.
		// normal file in the directory.
		// if the web server does not unzip the jar file, then classes will exist in sun.tools.jar.resources.jar file.
		List<String> names;
		// contains the name of the class file. e.g., Demo.class will be stored as "Demo"
		if (isJarFile(filePath)) {
			// jar file
			return readFromJarFile(filePath, splashPath, filter);
		} else {
			// directory
			names = readFromDirectory(filePath);
		}
		names.forEach(name -> {
			if (isClassFile(name)) {
				String fullyQualifiedName = toFullyQualifiedName(name, packagePath);
				if (filter.filter(fullyQualifiedName)) {
					classNames.add(fullyQualifiedName);
				}
			} else {
				// this is a directory,check this directory for more classes
				classNames(packagePath + "." + name, classNames, filter);
			}
		});
		return classNames;
	}

	/**
	 * 读取jar文件中的类名称列表
	 *
	 * @param jarPath             jar文件路径
	 * @param splashedPackageName jar中待扫描的包名称
	 * @return 类名称列表
	 */
	@SneakyThrows
	private List<String> readFromJarFile(String jarPath, String splashedPackageName, ScannerFilter filter) {
		JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
		JarEntry entry = jarIn.getNextJarEntry();
		List<String> classNames = new ArrayList<>();
		while (null != entry) {
			String name = entry.getName();
			if (name.startsWith(splashedPackageName) && isClassFile(name)) {
				String fullyQualifiedName = splashToDot(trimExtension(name));
				if (filter.filter(fullyQualifiedName)) {
					classNames.add(fullyQualifiedName);
				}
			}
			entry = jarIn.getNextJarEntry();
		}
		return classNames;
	}

	/**
	 * 读取路径下的class文件
	 *
	 * @param path 待读取的文件路径
	 */
	private List<String> readFromDirectory(String path) {
		File file = new File(path);
		String[] names = file.list();
		if (null == names || names.length == 0) {
			return new ArrayList<>();
		}
		return Arrays.asList(names);
	}

	/**
	 * 取得全限类名
	 *
	 * @param shortName   类名简称
	 * @param basePackage 包名称
	 * @return 全限类名
	 */
	private String toFullyQualifiedName(String shortName, String basePackage) {
		return basePackage + "." + trimExtension(shortName);
	}

	/**
	 * "Demo.class" -> "Demo"
	 */
	private String trimExtension(String name) {
		int pos = name.indexOf('.');
		if (-1 != pos) {
			return name.substring(0, pos);
		}
		return name;
	}

	/**
	 * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
	 * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
	 */
	private String getRootPath(URL url) {
		String fileUrl = url.getFile();
		int pos = fileUrl.indexOf('!');
		if (-1 == pos) {
			return fileUrl;
		}
		return fileUrl.substring(5, pos);
	}

	/**
	 * dot replace to splash, '.' -> '/'
	 */
	private String dotToSplash(String packagePath) {
		return packagePath.replaceAll("\\.", "/");
	}

	/**
	 * dot replace to splash, '/' -> '.'
	 */
	private String splashToDot(String packagePath) {
		return packagePath.replaceAll("/", "\\.");
	}

	private boolean isJarFile(String name) {
		return name.endsWith(FILE_SUFFIX_JAR);
	}

	private boolean isClassFile(String name) {
		return name.endsWith(FILE_SUFFIX_CLASS);
	}
}