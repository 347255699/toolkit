package org.mendora.verticle;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import org.apache.logging.log4j.core.lookup.ContextMapLookup;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/4/18
 * desc:
 */
public class WebVerticle extends AbstractVerticle {
    private static Vertx VERTX;
    private static Router ROUTER;

    @Override
    public void start() throws Exception {
        String root = PathUtil.root();
        ROUTER = Router.router(VERTX);
        System.setProperty("rootPath", root);
        System.setProperty("log4j.configurationFile", root + "log4j2.yaml");
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        ROUTER.get("/text").handler(rtx -> {
            Logger log = LoggerFactory.getLogger(WebVerticle.class);
            log.info("test");
            rtx.response().end("999dsfdsf");
        });

        VERTX.createHttpServer().requestHandler(ROUTER).listen(8080);
    }

    public static void main(String[] args) {
        VERTX = Vertx.vertx();
        VERTX.deployVerticle(WebVerticle.class.getName());
    }
}
