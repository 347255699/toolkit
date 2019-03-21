package org.mendora.generate.base;

import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.mendora.config.AnnotationConfig;

import java.util.List;

@Data
public abstract class AbstractRepositoryImplTypeSpec implements BaseTypeSpec {

    public String implName;

    public String fullSuperInterfaceName;

    public String comment;

    public String fullSuperClassName;

    public String fullPojoClassName;

    public String keyType;

    public String table;

    public List<AnnotationConfig> annotationSpecs;
    
    public abstract TypeSpec generate();
}
