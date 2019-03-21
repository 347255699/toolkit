package org.mendora.config;

import lombok.Data;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
@Data
public class ClassConfig {

    public static final String CLASS_CONFIG = "classConfig";

    private String name;

    private String basePackage;

    private String superRepoInterface;

    private String superRepoImpl;

    private String primaryKey;

    private boolean enable;
}
