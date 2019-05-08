package org.mendora.generate.px;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.mendora.db.TableDesc;
import org.mendora.db.mysql.PrimaryKey;
import org.mendora.generate.base.AbstractRepositoryInterfaceTypeSpec;

import javax.lang.model.element.Modifier;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
public class PxRepositoryInterfaceTypeSpec extends AbstractRepositoryInterfaceTypeSpec {

    // 构建表字段信息
    private void buildTableField(TableDesc td, TypeSpec.Builder repoInterfaceBuilder) {
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(String.class, "TF_" + td.field().toUpperCase())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("\"" + td.field() + "\"");
        repoInterfaceBuilder.addField(fieldBuilder.build());
    }

    @Override
    public TypeSpec generate() {
        TypeSpec.Builder repoInterfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)
                .addModifiers(Modifier.PUBLIC);

        tableDescs.forEach(td -> buildTableField(td, repoInterfaceBuilder));

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
