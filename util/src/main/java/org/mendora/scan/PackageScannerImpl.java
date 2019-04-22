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
 * created by:xmf
 * date:2018/3/9
 * description:
 */
@RequiredArgsConstructor
public class PackageScannerImpl<T> implements PackageScanner<T> {
    @NonNull
    private String packagePath;
    @NonNull
    private ClassLoader cl;

    @Override
    public List<String> classNames(String except) {
        return classNames(this.packagePath, new ArrayList<>(), name -> {
            if (!except.equals(name))
                return name;
            return null;
        });
    }

    @Override
    public List<String> classNames() {
        return classNames(packagePath, new ArrayList<>(), name -> name);
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
            return Optional.of((T)clazz.newInstance());
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
     * scanning class names below package path.
     *
     * @param packagePath
     * @param nameList
     * @return
     */
    private List<String> classNames(String packagePath, List<String> nameList, ScannerFilter filter) {
        // "." -> "/"
        String splashPath = dotToSplash(packagePath);
        URL url = cl.getResource(splashPath);
        String filePath = getRootPath(url);
        /**
         * get classes in that package.
         * normal file in the directory.
         * if the web server does not unzip the jar file, then classes will exist in jar file.
         */
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
                fullyQualifiedName = filter.filte(fullyQualifiedName);
                if (fullyQualifiedName != null)
                    nameList.add(fullyQualifiedName);
            } else {
                /**
                 * this is a directory
                 * check this directory for more classes
                 */
                classNames(packagePath + "." + name, nameList, filter);
            }
        });
        return nameList;
    }

    /**
     * read class name list below jar.
     *
     * @param jarPath jar path
     * @param splashedPackageName
     * @return
     */
    @SneakyThrows
    private List<String> readFromJarFile(String jarPath, String splashedPackageName, ScannerFilter filter) {
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> classNames = new ArrayList<>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                String fullyQualifiedName = filter.filte(splashToDot(trimExtension(name)));
                if (fullyQualifiedName != null)
                    classNames.add(fullyQualifiedName);
            }
            entry = jarIn.getNextJarEntry();
        }
        return classNames;
    }

    /**
     * read class list below directory
     *
     * @param path
     * @return
     */
    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();
        if (null == names)
            return null;
        return Arrays.asList(names);
    }

    /**
     * turn to qualified class name.
     *
     * @param shortName
     * @param basePackage
     * @return
     */
    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(trimExtension(shortName));
        return sb.toString();
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