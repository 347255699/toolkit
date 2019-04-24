package org.mendora;

import io.vertx.reactivex.core.Vertx;
import lombok.Builder;
import org.mendora.facade.DataAccess;
import org.mendora.facade.DataAccessFactory;
import org.mendora.scan.PackageScanner;
import org.mendora.scan.PackageScannerImpl;
import org.mendora.verticle.DataAccessVerticle;

import java.util.List;

/**
 * @author menfre
 */
@Builder
public class DataAccessModule {
	private String basePackageName;

	private String username;

	private String password;

	private String uri;

	private static final String URI_MARK = "?";

	public void run(Vertx vertx) {
		String temp = uri.substring(uri.indexOf("//") + 2);
		String host = temp.substring(0, temp.indexOf("/"));
		String database;
		if (temp.contains(URI_MARK)) {
			database = temp.substring(temp.indexOf("/") + 1, temp.indexOf(URI_MARK));
		} else {
			database = temp.substring(temp.indexOf("/") + 1);
		}
		PackageScanner<DataAccessFactory> scanner = new PackageScannerImpl<>(basePackageName, DataAccessModule.class.getClassLoader());
		List<DataAccessFactory> dataAccessFactories = scanner.newInstances(scanner.fullClassNames(DataAccessFactory.class.getName()), DataAccess.class);
		DataAccessVerticle dataAccesserVerticle = new DataAccessVerticle(username, password, host, database, dataAccessFactories);
		vertx.deployVerticle(dataAccesserVerticle);
	}
}
