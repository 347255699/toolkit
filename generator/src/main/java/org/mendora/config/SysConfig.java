package org.mendora.config;

import lombok.Data;
import org.mendora.db.DbSources;

import java.util.List;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: 系统参数
 */
@Data
public class SysConfig {

    public static final String TABLE = "table";

    public static final String TARGET_PATH = "targetPath";

    public static final String STATUS_KEYWORD = "statusKeyword";

    public static List<DbSources> dbSources;

    public static List<String> table;

    public static List<String> statusKeyword;

    public static List<ClassConfig> classConfig;

    public static List<AnnotationConfig> annotationConfig;

    public static String targetPath;
}
