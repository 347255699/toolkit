package org.mendora.access;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.sql.SQLConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mendora.facade.DataAccess;
import org.mendora.facade.DataAccessFactory;
import org.mendora.facade.DataAccessing;

import java.util.List;

/**
 * @author menfre
 */
@Slf4j
@DataAccess
public class DemoDataAccess implements DataAccessFactory {

	@Data
	private static class User {
		private int id;

		private String name;
	}

	public static DemoDataAccess newInstance() {
		return new DemoDataAccess();
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
