package org.mendora.generate;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import java.time.LocalDateTime;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc: 基础模版
 */
public interface BaseTypeSpec {
    String LOMBOK_PACKAGE = "lombok";
    String LOMBOK_EXTERN_SLF4J_PACKAGE = "lombok.extern.slf4j";
    String SLF4J = "Slf4j";

    /**
     * 生成类描述
     *
     * @return 类描述
     */
    TypeSpec generate();

    /**
     * 添加注释
     *
     * @param typeSpecBuilder 待添加类
     * @param comment         注释
     */
    default TypeSpec.Builder javaDoc(TypeSpec.Builder typeSpecBuilder, String comment) {
        return typeSpecBuilder
                .addJavadoc("@author from toolkit created by menfre\n")
                .addJavadoc("@version 1.0\n")
                .addJavadoc("date: " + LocalDateTime.now() + "\n")
                .addJavadoc("desc: " + comment);
    }

    /**
     * 添加lombok注解
     *
     * @param name lombok注解名称
     * @return 注解描述者
     */
    default AnnotationSpec lombok(String name) {
        String packageName = name.equals(SLF4J) ? LOMBOK_EXTERN_SLF4J_PACKAGE : LOMBOK_PACKAGE;
        return AnnotationSpec.builder(ClassName.get(packageName, name))
                .build();
    }

    /**
     * 添加lombok注解
     *
     * @param name lombok注解名称
     * @return 注解描述者
     */
    default AnnotationSpec spring(String name) {
        return AnnotationSpec.builder(ClassName.get("org.springframework.stereotype", name))
                .build();
    }

    /**
     * 分隔全限类名
     *
     * @param fullClassName 全限类名
     * @return 包名 + 简单类名
     */
    default String[] separateFullClassName(String fullClassName) {
        int i = fullClassName.lastIndexOf(".");
        String packageName = fullClassName.substring(0, i);
        String simpleClassName = fullClassName.substring(i + 1);
        final String[] names = new String[2];
        names[0] = packageName;
        names[1] = simpleClassName;
        return names;
    }
}
