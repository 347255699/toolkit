package org.mendora.generate.base;

/**
 * @author menfre
 */
public interface TypeSpecFactory {

    /**
     * pojo类型构造
     *
     * @return pojo构造类
     */
    AbstractPojoTypeSpec pojoTypeSpec();

    /**
     * repo接口构造
     *
     * @return repo接口构造
     */
    AbstractRepositoryInterfaceTypeSpec repositoryInterfaceTypeSpec();

    /**
     * repo实现构造
     *
     * @return repo实现构造
     */
    AbstractRepositoryImplTypeSpec repositoryImplTypeSpec();
}
