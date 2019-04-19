package org.mendora.db.mysql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mendora.bean.BeanUtil;
import org.mendora.db.DbSources;
import org.mendora.db.DbDriver;
import org.mendora.db.TableDesc;
import org.mendora.string.StringUtil;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/3/20
 * desc: jdbc驱动
 */
@Slf4j
@RequiredArgsConstructor
public class JdbcDriver implements DbDriver<ResultSet> {

    @NonNull
    private DbSources sources;

    private Connection conn;

    @Override
    public boolean connectTesting() {
        try {
            Class.forName(sources.getDriverClass());
            conn = DriverManager.getConnection(sources.getUrl(), sources.getUser(), sources.getPassword());
            if (conn != null && !conn.isClosed()) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    @Override
    public ResultSet query(String statement) throws Exception {
        final Statement stat = conn.createStatement();
        return stat.executeQuery(statement);
    }

    @Override
    public List<TableDesc> showFullColumns(String tableName) throws Exception {
        final ResultSet rs = query("show full columns from " + tableName);
        final List<MysqlTableDesc> mysqlTableDescs = parseObject(MysqlTableDesc.class, rs);
        return mysqlTableDescs.stream()
                .map(item -> (TableDesc) item)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> showTables() throws Exception {
        final ResultSet rs = query("show tables");
        final List<String> tableNames = new ArrayList<>();
        while (rs.next()) {
            String tableName = rs.getString(1);
            tableNames.add(tableName);
        }
        return tableNames;
    }

    @Override
    public <T> List<T> parseObject(Class<T> tClass, ResultSet rs) throws Exception {
        final BeanInfo bi = Introspector.getBeanInfo(tClass);
        final PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        final List<T> objs = new ArrayList<>();
        if (pds.length > 1) {
            while (rs.next()) {
                final T t = tClass.newInstance();
                // 填充
                BeanUtil.filling(t, k -> rs.getString(StringUtil.firstLetterUpper(k)));
                objs.add(t);
            }
        }
        return objs;
    }
}
