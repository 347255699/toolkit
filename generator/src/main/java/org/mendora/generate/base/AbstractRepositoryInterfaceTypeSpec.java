package org.mendora.generate.base;

import com.squareup.javapoet.TypeSpec;
import lombok.Data;

@Data
public abstract class AbstractRepositoryInterfaceTypeSpec implements BaseTypeSpec {

    public String interfaceName;

    public String comment;

    public String fullSuperClassName;

    public String fullPojoClassName;

    public String keyType;
    
    public abstract TypeSpec generate();
}
