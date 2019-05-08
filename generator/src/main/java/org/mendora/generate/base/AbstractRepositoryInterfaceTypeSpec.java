package org.mendora.generate.base;

import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.mendora.db.TableDesc;

import java.util.List;

/**
 * @author menfre
 */
@Data
public abstract class AbstractRepositoryInterfaceTypeSpec implements BaseTypeSpec {

    public String interfaceName;

    public String comment;

    public String fullSuperClassName;

    public String fullPojoClassName;

    public String keyType;

    public List<TableDesc> tableDescs;
    
    public abstract TypeSpec generate();
}
