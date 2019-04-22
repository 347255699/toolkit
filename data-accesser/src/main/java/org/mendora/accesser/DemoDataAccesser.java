package org.mendora.accesser;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.sql.SQLConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mendora.facade.DataAccesser;
import org.mendora.facade.DataAccesserFactory;
import org.mendora.facade.DataAccessing;

import java.util.List;

@Slf4j
@DataAccesser
public class DemoDataAccesser implements DataAccesserFactory {

    @Data
    private static class User {
        private int id;

        private String name;
    }

    public static DemoDataAccesser newInstance() {
        return new DemoDataAccesser();
    }

    @DataAccessing(autoCommit = true)
    public void saveRecord(SQLConnection conn) {
        conn.query("select * from User", res -> {
            if (res.succeeded()) {
                List<JsonObject> rows = res.result().getRows();
                System.out.println(rows.size());
            }
        });
    }
}
