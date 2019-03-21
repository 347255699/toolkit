package org.mendora.generate;

import com.squareup.javapoet.JavaFile;
import lombok.extern.slf4j.Slf4j;
import org.mendora.config.AnnotationConfig;
import org.mendora.config.ClassConfig;
import org.mendora.config.SysConfig;
import org.mendora.db.DbDirector;
import org.mendora.db.TableDesc;
import org.mendora.util.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/21
 * desc:
 */
@Slf4j
public class GenerateDirector {

    private DbDirector dbDirector;

    private List<String> table;

    private ClassConfig classConfig;

    private List<AnnotationConfig> annotationConfig;

    private GenerateDirector(DbDirector dbDirector) {
        this.dbDirector = dbDirector;
        table = SysConfig.table;
        annotationConfig = SysConfig.annotationConfig;
        classConfig = SysConfig.classConfig
                .stream()
                .filter(ClassConfig::isEnable)
                .collect(Collectors.toList())
                .get(0);
    }

    public static GenerateDirector getInstance(DbDirector dbDirector) {
        return new GenerateDirector(dbDirector);
    }

    private String pojoClassName(String tableName) {
        return StringUtils.firstLetterToUpperCase(StringUtils.lineToHump(tableName));
    }

    private void buildPojo(List<String> tables, Map<String, List<TableDesc>> tableDescs) {
        tables.stream()
                .map(t ->
                        PojoTypeSpec.builder()
                                .comment("")
                                .pojoName(pojoClassName(t))
                                .annotationSpecs(annotationConfig)
                                .dbDirector(dbDirector)
                                .tableDescs(tableDescs.get(t))
                                .build()
                                .generate()
                )
                .map(typeSpec -> JavaFile.builder(classConfig.getBasePackage() + ".vo", typeSpec).build())
                .forEach(javaFile -> {
                    try {
                        javaFile.writeTo(Paths.get(SysConfig.targetPath));
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }

    private void buildRepositoryInterface(List<String> tables) {
        tables.stream()
                .map(t ->
                        RepositoryInterfaceTypeSpec
                                .builder()
                                .comment("")
                                .fullSuperClassName(classConfig.getSuperRepoInterface())
                                .fullPojoClassName(classConfig.getBasePackage() + ".vo." + pojoClassName(t))
                                .keyType(classConfig.getPrimaryKey())
                                .interfaceName(pojoClassName(t) + "Repository")
                                .build()
                                .generate()
                )
                .map(typeSpec -> JavaFile.builder(classConfig.getBasePackage() + ".repository", typeSpec).build())
                .forEach(javaFile -> {
                    try {
                        javaFile.writeTo(Paths.get(SysConfig.targetPath));
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }

    private void buildRepositoryImpl(List<String> tables) {
        tables.stream()
                .map(t ->
                        PundixRepositoryImplTypeSpec
                                .builder()
                                .comment("")
                                .fullSuperClassName(classConfig.getSuperRepoImpl())
                                .fullPojoClassName(classConfig.getBasePackage() + ".vo." + pojoClassName(t))
                                .keyType(classConfig.getPrimaryKey())
                                .fullSuperInterfaceName(classConfig.getBasePackage() + ".repository." + pojoClassName(t) + "Repository")
                                .implName(pojoClassName(t) + "RepositoryImpl")
                                .annotationSpecs(annotationConfig)
                                .table(t)
                                .build()
                                .generate()
                )
                .map(typeSpec -> JavaFile.builder(classConfig.getBasePackage() + ".repository.impl", typeSpec).build())
                .forEach(javaFile -> {
                    try {
                        javaFile.writeTo(Paths.get(SysConfig.targetPath));
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }

    /**
     * 开始构造
     */
    public void constuct() {
        List<String> tables;
        try {
            tables = dbDirector.tables()
                    .stream()
                    .filter(t -> {
                        boolean isWeNeed = false;
                        for (String t0 : table) {
                            if (t0.equals(t) || t.startsWith(t0)) {
                                isWeNeed = true;
                                break;
                            }
                        }
                        return isWeNeed;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }

        final Map<String, List<TableDesc>> tableDescs = dbDirector.tableDesc(tables);

        // 构建repository接口
        buildRepositoryInterface(tables);

        // 构建repository实现
        buildRepositoryImpl(tables);

        // 构建pojo
        buildPojo(tables, tableDescs);

    }
}
