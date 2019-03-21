package org.mendora.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;
import org.mendora.config.AnnotationConfig;
import org.mendora.db.DbDirector;
import org.mendora.db.TableDesc;
import org.mendora.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.Comparator;
import java.util.List;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
@Builder
public class PojoTypeSpec implements BaseTypeSpec {

    private List<TableDesc> tableDescs;

    private String pojoName;

    private String comment;

    private List<AnnotationConfig> annotationSpecs;

    private DbDirector dbDirector;


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
