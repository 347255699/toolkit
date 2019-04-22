package org.mendora;

import io.vertx.reactivex.core.Vertx;
import lombok.Builder;
import org.mendora.facade.DataAccesser;
import org.mendora.facade.DataAccesserFactory;
import org.mendora.scan.PackageScanner;
import org.mendora.scan.PackageScannerImpl;
import org.mendora.verticle.DataAccesserVerticle;

import java.util.List;

@Builder
public class DataAccesserModule {
    private String basePackageName;

    private String username;

    private String password;

    private String host;

    private String database;

    public Vertx run(Vertx vertx) {
        PackageScanner<DataAccesserFactory> scanner = new PackageScannerImpl<>(basePackageName, DataAccesserModule.class.getClassLoader());
        List<DataAccesserFactory> dataAccesserFactories= scanner.newInstances(scanner.fullClassNames(DataAccesserFactory.class.getName()), DataAccesser.class);
        DataAccesserVerticle dataAccesserVerticle = new DataAccesserVerticle(username, password, host, database, dataAccesserFactories);
        vertx.deployVerticle(dataAccesserVerticle);
        return vertx;
    }
}
