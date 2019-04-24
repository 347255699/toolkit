package org.mendora;

import org.apache.logging.log4j.Level;

/**
 * @author menfre
 */
public class DataAccessApplication {
    public static void main(String[] args) {
        LoggerModule.builder()
                .logLevel(Level.DEBUG)
                .build()
                .run();
        DataAccessModule.builder()
                .basePackageName("org.mendora.access")
                .password("123456")
                .username("root")
                .build();
    }
}
