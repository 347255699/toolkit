package org.mendora;

import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.mendora.verticle.DataAccessVerticle;

/**
 * @author menfre
 */
@Builder
@Slf4j
public class DataAccessModule {
    private String username;

    private String password;

    private String uri;

    private boolean worker;

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
        DataAccessVerticle dataAccessVerticle = new DataAccessVerticle(username, password, host, database);
        DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setWorker(worker);
        vertx.rxDeployVerticle(dataAccessVerticle, deploymentOptions).blockingGet();
    }
}
