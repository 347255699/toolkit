package org.mendora.db.mysql;

import lombok.Setter;
import org.mendora.db.TableDesc;

/**
 * @author menfre
 * @version 1.0
 * date: 2018/9/26
 * desc: mysql表结构描述
 */
public class MysqlTableDesc implements TableDesc {

    @Setter
    private String field;

    @Setter
    private String type;

    @Setter
    private String comment;

    @Override
    public String field() {
        return field;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String comment() {
        return comment;
    }
}
