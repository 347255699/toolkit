package org.mendora.generate.base;

public interface TypeSpecFactory {

    AbstractPojoTypeSpec pojoTypeSpec();

    AbstractRepositoryInterfaceTypeSpec repositoryInterfaceTypeSpec();

    AbstractRepositoryImplTypeSpec repositoryImplTypeSpec();
}
