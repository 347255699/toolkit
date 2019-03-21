package org.mendora.generate.px;

import org.mendora.generate.base.AbstractPojoTypeSpec;
import org.mendora.generate.base.AbstractRepositoryImplTypeSpec;
import org.mendora.generate.base.AbstractRepositoryInterfaceTypeSpec;
import org.mendora.generate.base.TypeSpecFactory;

public class PxTypeSpecFactory implements TypeSpecFactory {

    @Override
    public AbstractPojoTypeSpec pojoTypeSpec() {
        return new PxPojoTypeSpec();
    }

    @Override
    public AbstractRepositoryInterfaceTypeSpec repositoryInterfaceTypeSpec() {
        return new PxRepositoryInterfaceTypeSpec();
    }

    @Override
    public AbstractRepositoryImplTypeSpec repositoryImplTypeSpec() {
        return new PxRepositoryImplTypeSpec();
    }
}
