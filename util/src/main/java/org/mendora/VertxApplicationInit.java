package org.mendora;

import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.reactivex.core.Vertx;

public class VertxApplicationInit {
    protected static Vertx vertx(boolean fileCashingEnabled, int workerPoolSize){
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        VertxOptions vertxOptions = new VertxOptions()
                .setFileSystemOptions(new FileSystemOptions().setFileCachingEnabled(fileCashingEnabled))
                .setWorkerPoolSize(workerPoolSize);
        return Vertx.vertx(vertxOptions);
    }
}
