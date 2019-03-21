package org.mendora.generate;

import com.squareup.javapoet.*;
import lombok.Builder;
import org.mendora.db.mysql.PrimaryKey;

import javax.lang.model.element.Modifier;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
@Builder
public class RepositoryImplTypeSpec implements BaseTypeSpec {

    private String implName;

    private String fullSuperInterfaceName;

    private String comment;

    private String fullSuperClassName;

    private String fullPojoClassName;

    private String keyType;

    private String table;

    @Override
    public TypeSpec generate() {
        final String[] superInterface = separateFullClassName(fullSuperInterfaceName);
        final String[] pojoClass = separateFullClassName(fullPojoClassName);
        final String[] superClass = separateFullClassName(fullSuperClassName);

        TypeSpec.Builder repoImplBuilder = TypeSpec.classBuilder(implName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(superInterface[0], superInterface[1]))
                .addAnnotation(AnnotationSpec.builder(ClassName.get("org.springframework.stereotype", "Repository")).build());

        PrimaryKey.valOf(keyType).ifPresent(pk -> {
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                    ClassName.get(superClass[0], superClass[1]),
                    ClassName.get(pojoClass[0], pojoClass[1]),
                    ClassName.get(pk.type)
            );
            repoImplBuilder.superclass(parameterizedTypeName);
        });

        FieldSpec tableName = FieldSpec.builder(String.class, "TABLE_NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("\"" + table + "\"")
                .build();

        repoImplBuilder.addField(tableName);

        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Class.class), ClassName.get(pojoClass[0], pojoClass[1]));

        MethodSpec getBeanClassMethod = MethodSpec.methodBuilder("getBeanClass")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(returnType)
                .addCode("return " + pojoClass[1] + ".class;")
                .build();

        MethodSpec getTableName = MethodSpec.methodBuilder("getTableName")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class)
                .addCode("return TABLE_NAME;")
                .build();

        return javaDoc(repoImplBuilder, "").addMethod(getBeanClassMethod).addMethod(getTableName).build();
    }
}
