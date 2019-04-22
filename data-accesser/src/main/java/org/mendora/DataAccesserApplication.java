package org.mendora;

import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.Level;

public class DataAccesserApplication {
    public static void main(String[] args) {
        LoggerModule.builder()
                .logLevel(Level.DEBUG)
                .build()
                .run();
        DataAccesserModule.builder()
                .basePackageName("org.mendora.accesser")
                .database("supervisor_console")
                .host("localhost")
                .password("123456")
                .username("root")
                .build()
                .run(Vertx.vertx());
    }
}
