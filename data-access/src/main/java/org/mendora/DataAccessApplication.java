package org.mendora;

import org.apache.logging.log4j.Level;
import org.mendora.access.DemoDataAccess;
import org.mendora.access.DemoDataAccessImpl;
import org.mendora.facade.DataAccessFactory;

/**
 * @author menfre
 */
public class DataAccessApplication extends VertxApplicationInit{
    public static void main(String[] args) {
        LoggerModule.builder()
                .logLevel(Level.DEBUG)
                .build()
                .run();
        DataAccessModule.builder()
                .password("123456")
                .username("root")
                .uri("jdbc:mysql://localhost/Demo")
                .build()
                .run(vertx(false, 10));

        DemoDataAccess demoDataAccess = DataAccessFactory.get(new DemoDataAccessImpl());
        demoDataAccess.findById();
    }
}
