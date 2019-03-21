package org.mendora.generate.px;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.mendora.config.AnnotationConfig;
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

    @Override
    public TypeSpec generate() {
        TypeSpec.Builder pojoBuilder = TypeSpec.classBuilder(pojoName)
                .addModifiers(Modifier.PUBLIC);

        tableDescs.forEach(td -> {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(dbDirector.toJavaType(td.type()), StringUtils.lineToHump(td.field()))
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc(td.comment());

            pojoBuilder.addField(fieldBuilder.build());
        });

        annotationSpecs.stream()
                .sorted(Comparator.comparing(AnnotationConfig::getSort))
                .filter(item -> "lombok".equals(item.getMode()))
                .filter(AnnotationConfig::isEnable)
                .map(item -> lombok(item.getName()))
                .forEach(pojoBuilder::addAnnotation);
        return javaDoc(pojoBuilder, comment).build();
    }
}
