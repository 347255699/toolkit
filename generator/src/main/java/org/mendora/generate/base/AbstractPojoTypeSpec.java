package org.mendora.generate.base;

import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.mendora.config.AnnotationConfig;
import org.mendora.db.DbDirector;
import org.mendora.db.TableDesc;

import java.util.List;

@Data
public abstract class AbstractPojoTypeSpec implements BaseTypeSpec {

    public List<TableDesc> tableDescs;

    public String pojoName;

    public String comment;

    public List<AnnotationConfig> annotationSpecs;

    public DbDirector dbDirector;

    /**
     * 生成类描述
     *
     * @return 类描述
     */
    public abstract TypeSpec generate();
}
