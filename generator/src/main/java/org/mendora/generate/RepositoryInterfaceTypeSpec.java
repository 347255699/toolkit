package org.mendora.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
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
public class RepositoryInterfaceTypeSpec implements BaseTypeSpec {

    private String interfaceName;

    private String comment;

    private String fullSuperClassName;

    private String fullPojoClassName;

    private String keyType;

    @Override
    public TypeSpec generate() {
        TypeSpec.Builder repoInterfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)
                .addModifiers(Modifier.PUBLIC);

        final String[] superClass = separateFullClassName(fullSuperClassName);
        final String[] pojoClass = separateFullClassName(fullPojoClassName);

        PrimaryKey.valOf(keyType).ifPresent(pk -> {
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                    ClassName.get(superClass[0], superClass[1]),
                    ClassName.get(pojoClass[0], pojoClass[1]),
                    ClassName.get(pk.type)
            );
            repoInterfaceBuilder.addSuperinterface(parameterizedTypeName);
        });

        return javaDoc(repoInterfaceBuilder, comment).build();
    }
}
