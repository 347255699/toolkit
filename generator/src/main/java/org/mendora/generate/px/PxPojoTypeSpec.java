package org.mendora.generate.px;

import com.squareup.javapoet.*;
import org.mendora.config.AnnotationConfig;
import org.mendora.config.SysConfig;
import org.mendora.db.TableDesc;
import org.mendora.generate.base.AbstractPojoTypeSpec;
import org.mendora.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.Comparator;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
public class PxPojoTypeSpec extends AbstractPojoTypeSpec {

    private static String MODE = "pojo";

    private void buildEnum(TableDesc td, TypeSpec.Builder pojoBuilder) {
        String enumName0 = StringUtils.lineToHump(td.field());
        String enumName = StringUtils.firstLetterToUpperCase(enumName0);
        // 构造枚举
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(enumName)
                .addModifiers(Modifier.PUBLIC)
                .addField(int.class, "val", Modifier.PUBLIC, Modifier.FINAL)
                .addField(String.class, "msg", Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(int.class, "val")
                        .addParameter(String.class, "msg")
                        .addStatement("this.$N = $N", "val", "val")
                        .addStatement("this.$N = $N", "msg", "msg")
                        .build());

        String comment = td.comment();
        int index = comment.indexOf("(");
        String substring = comment.substring(index + 1, comment.length() - 1);
        comment = comment.substring(0, index);
        String[] status = substring.split("\\|");

        for (int i = 0; i < status.length; i++) {
            String[] str = status[i].split(":");
            String name = str[0].trim();
            int val = Integer.valueOf(str[1].trim());
            String msg = str[2].trim();
            TypeSpec.Builder builder = TypeSpec.anonymousClassBuilder(val + ", \"" + msg + "\"");
            if (0 == i) {
                builder.addJavadoc(comment);
            }
            enumBuilder.addEnumConstant(name, builder.build());
        }

        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get("java.util", "Optional"), ClassName.get("", enumName));
        MethodSpec methodSpec = MethodSpec.methodBuilder("valOf").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(int.class, "val").build())
                .returns(returnType)
                .addCode("\tfor ($N $N : values()){\n", enumName, enumName0)
                .addCode("\t\tif(val == $N.val){\n", enumName0)
                .addStatement("\t\t\treturn Optional.of($N)", enumName0)
                .addCode("\t\t}\n")
                .addCode("\t}\n")
                .addStatement("\treturn Optional.empty()")
                .build();
        enumBuilder.addMethod(methodSpec);

        pojoBuilder.addType(enumBuilder.build());
    }

    @Override
    public TypeSpec generate() {
        TypeSpec.Builder pojoBuilder = TypeSpec.classBuilder(pojoName)
                .addModifiers(Modifier.PUBLIC);

        tableDescs.forEach(td -> {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(dbDirector.toJavaType(td.type()), StringUtils.lineToHump(td.field()))
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc(td.comment());

            pojoBuilder.addField(fieldBuilder.build());

            SysConfig.statusKeyword.forEach(keyword -> {
                String keyword0 = StringUtils.firstLetterToUpperCase(keyword);
                if (td.field().equals(keyword) || td.field().contains(keyword0)) {
                    buildEnum(td, pojoBuilder);
                }
            });
        });

        annotationSpecs.stream()
                .sorted(Comparator.comparing(AnnotationConfig::getSort))
                .filter(item -> MODE.equals(item.getMode()))
                .filter(AnnotationConfig::isEnable)
                .map(item -> lombok(item.getName()))
                .forEach(pojoBuilder::addAnnotation);
        return javaDoc(pojoBuilder, comment).build();
    }
}
