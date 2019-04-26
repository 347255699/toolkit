package org.mendora.access;

import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author menfre
 */
@Slf4j
public class DemoDataAccessImpl implements DemoDataAccess {
    @Override
    public void findById() {
        conn.rxQuery("select * from admin")
                .subscribe(rs -> {
                    List<JsonObject> rows = rs.getRows();
                    rows.stream()
                            .map(r -> r.getString("id"))
                            .forEach(log::info);
                })
                .dispose();
    }
}
