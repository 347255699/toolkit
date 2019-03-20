package org.mendora;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mendora.config.SysConfig;
import org.mendora.db.*;
import org.mendora.db.mysql.MysqlDbFactory;
import org.mendora.util.PathUtil;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

@Slf4j
public class MainApplication {

    private static final String DEFAULT_FILE = "config.json";

    private static JSONObject loadConfig() throws Exception {
        RandomAccessFile file = new RandomAccessFile(PathUtil.root() + DEFAULT_FILE, "r");
        FileChannel channel = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(2048);
        channel.read(bb);
        return JSONObject.parseObject(new String(bb.array()));
    }

    public static void main(String[] args) throws Exception {
        DbFactory dbFactory = new MysqlDbFactory();

        TypeConverter typeConverter = dbFactory.typeConverter();

        final JSONObject config = loadConfig();
        SysConfig.dbSources = config.getJSONArray(DbSources.DB_SOURCES).toJavaList(DbSources.class);

        DbDirector dbDirector = DbDirector.getInstance();
        if (!dbDirector.test()) {
            log.error("connect failed.");
        }
        System.out.println(dbDirector.tables());
        Map<String, List<TableDesc>> tableDesc = dbDirector.tableDesc();
        for (String k : tableDesc.keySet()) {
            tableDesc.get(k)
                    .stream()
                    .map(TableDesc::type)
                    .map(typeConverter::toJavaType)
                    .forEach(System.out::println);
        }
    }
}
