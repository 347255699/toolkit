package org.mendora.generate.px;

import com.squareup.javapoet.*;
import org.mendora.config.AnnotationConfig;
import org.mendora.db.mysql.PrimaryKey;
import org.mendora.generate.base.AbstractRepositoryImplTypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Comparator;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
public class PxRepositoryImplTypeSpec extends AbstractRepositoryImplTypeSpec {

    @Override
    public TypeSpec generate() {
        final String[] superInterface = separateFullClassName(fullSuperInterfaceName);
        final String[] pojoClass = separateFullClassName(fullPojoClassName);
        final String[] superClass = separateFullClassName(fullSuperClassName);

        TypeSpec.Builder repoImplBuilder = TypeSpec.classBuilder(implName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(superInterface[0], superInterface[1]));

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
        repoImplBuilder.addMethod(getBeanClassMethod);

        MethodSpec getTableName = MethodSpec.methodBuilder("getTableName")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class)
                .addCode("return TABLE_NAME;")
                .build();
        repoImplBuilder.addMethod(getTableName);

        annotationSpecs.stream()
                .sorted(Comparator.comparing(AnnotationConfig::getSort))
                .filter(item -> "spring".equals(item.getMode()))
                .filter(AnnotationConfig::isEnable)
                .map(item -> spring(item.getName()))
                .forEach(repoImplBuilder::addAnnotation);

        return javaDoc(repoImplBuilder, "").build();
    }
}
