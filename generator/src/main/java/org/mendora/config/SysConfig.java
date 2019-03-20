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
    public static List<DbSources> dbSources;
}
