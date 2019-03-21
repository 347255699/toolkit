package org.mendora.config;

import lombok.Data;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
@Data
public class AnnotationConfig {

    public static final String ANNOTATION_CONFIG = "annotationConfig";

    private String name;

    private String mode;

    private boolean enable;

    private int sort;
}
