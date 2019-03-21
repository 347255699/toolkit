package org.mendora;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mendora.config.AnnotationConfig;
import org.mendora.config.ClassConfig;
import org.mendora.config.SysConfig;
import org.mendora.db.DbDirector;
import org.mendora.db.DbSources;
import org.mendora.generate.GenerateDirector;
import org.mendora.util.PathUtil;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author menfre
 */
@Slf4j
public class MainApplication {

    private static final String DEFAULT_FILE = "config.json";

    private static void loadConfig() throws Exception {
        RandomAccessFile file = new RandomAccessFile(PathUtil.root() + DEFAULT_FILE, "r");
        FileChannel channel = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(2048);
        channel.read(bb);
        JSONObject config = JSONObject.parseObject(new String(bb.array()));

        /**
         * 拆分配置信息，方便提取
         */
        SysConfig.dbSources = config.getJSONArray(DbSources.DB_SOURCES).toJavaList(DbSources.class);
        SysConfig.classConfig = config.getJSONArray(ClassConfig.CLASS_CONFIG).toJavaList(ClassConfig.class);
        SysConfig.annotationConfig = config.getJSONArray(AnnotationConfig.ANNOTATION_CONFIG).toJavaList(AnnotationConfig.class);
        SysConfig.table = config.getJSONArray(SysConfig.TABLE).toJavaList(String.class);
        SysConfig.targetPath = config.getString(SysConfig.TARGET_PATH);
    }

    public static void main(String[] args) throws Exception {
        loadConfig();
        DbDirector dbDirector = DbDirector.getInstance();

        if (!dbDirector.connectTest()) {
            log.error("connect failed.");
            return;
        }
        final GenerateDirector generateDirector = GenerateDirector.getInstance(dbDirector);

        // 构造
        generateDirector.constuct();
    }
}
