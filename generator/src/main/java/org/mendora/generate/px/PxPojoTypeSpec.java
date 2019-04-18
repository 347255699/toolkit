package org.mendora.generate.px;

import com.squareup.javapoet.*;
import org.mendora.config.AnnotationConfig;
import org.mendora.config.SysConfig;
import org.mendora.db.TableDesc;
import org.mendora.generate.base.AbstractPojoTypeSpec;
import org.mendora.util.StringUtil;

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
        // 枚举名称
        String lowerCaseEnumName = StringUtil.lineToHump(td.field());
        String upperCaseEnumName = StringUtil.firstLetterToUpperCase(lowerCaseEnumName);

        // 构造枚举
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(upperCaseEnumName)
                .addModifiers(Modifier.PUBLIC)
                .addField(int.class, "val", Modifier.PUBLIC, Modifier.FINAL)
                .addField(String.class, "msg", Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(int.class, "val")
                        .addParameter(String.class, "msg")
                        .addStatement("this.$N = $N", "val", "val")
                        .addStatement("this.$N = $N", "msg", "msg")
                        .build());

        /**
         * 提取注释
         */
        String comment = td.comment();
        int index = comment.indexOf("(");
        String substring = comment.substring(index + 1, comment.length() - 1);
        comment = comment.substring(0, index);
        String[] status = substring.split("\\|");

        // 添加枚举静态成员
        for (int i = 0; i < status.length; i++) {
            String[] str = status[i].split(":");
            String name = str[0].trim();
            int val = Integer.valueOf(str[1].trim());
            String msg = str[2].trim();
            TypeSpec.Builder builder = TypeSpec.anonymousClassBuilder(val + ", \"" + msg + "\"");
            if (0 == i) {
                builder.addJavadoc(comment);
            }
            enumBuilder.addEnumConstant(name.toUpperCase(), builder.build());
        }

        // 添加静态方法
        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get("java.util", "Optional"), ClassName.get("", upperCaseEnumName));
        MethodSpec methodSpec = MethodSpec.methodBuilder("valOf").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(int.class, "val").build())
                .returns(returnType)
                .addCode("\tfor ($N $N : values()){\n", upperCaseEnumName, lowerCaseEnumName)
                .addCode("\t\tif(val == $N.val){\n", lowerCaseEnumName)
                .addStatement("\t\t\treturn Optional.of($N)", lowerCaseEnumName)
                .addCode("\t\t}\n")
                .addCode("\t}\n")
                .addStatement("\treturn Optional.empty()")
                .build();
        enumBuilder.addMethod(methodSpec);

        // 构造内部枚举
        pojoBuilder.addType(enumBuilder.build());
    }

    // 构建表字段信息
    private void buildTableField(TableDesc td, TypeSpec.Builder pojoBuilder) {
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(String.class, "TF_" + td.field().toUpperCase())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("\"" + td.field() + "\"");
        pojoBuilder.addField(fieldBuilder.build());
    }

    @Override
    public TypeSpec generate() {
        TypeSpec.Builder pojoBuilder = TypeSpec.classBuilder(pojoName)
                .addModifiers(Modifier.PUBLIC);

        tableDescs.forEach(td -> {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(dbDirector.toJavaType(td.type()), StringUtil.lineToHump(td.field()))
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc(td.comment());

            pojoBuilder.addField(fieldBuilder.build());
            buildTableField(td, pojoBuilder);

            SysConfig.statusKeyword.forEach(keyword -> {
                String javaField = StringUtil.lineToHump(td.field());
                String keyword0 = StringUtil.firstLetterToUpperCase(keyword);
                if (javaField.equals(keyword) || javaField.contains(keyword0)) {
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
