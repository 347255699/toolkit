package org.mendora.verticle;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mendora.facade.RequestRouting;
import org.mendora.facade.Route;
import org.mendora.facade.RouteFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/4/18
 * desc:
 */
@Slf4j
public class WebVerticle extends AbstractVerticle {
    private Router router;
    private List<RouteFactory> routeFactories;
    private int port;
    private static final int DEFAULT_PORT = 8080;

    public WebVerticle(List<RouteFactory> routeFactories, int port) {
        super();
        this.routeFactories = routeFactories;
        this.port = port;
    }

    @Override
    public void start() throws Exception {
        router = Router.router(vertx);
        router.route().handler(LoggerHandler.create());
        router.route("/html/*").handler(StaticHandler.create("static/").setCachingEnabled(false));
        BodyHandler bodyHandler = BodyHandler.create();
        if (StringUtils.isNoneEmpty(System.getProperty("uploadDir"))) {
            bodyHandler.setUploadsDirectory(System.getProperty("uploadDir"));
        }
        router.route().handler(bodyHandler);
        if (routeFactories != null && routeFactories.size() > 0) {
            routeFactories.forEach(rf -> {
                String rootPath = rf.getClass().getAnnotation(Route.class).value();
                Method[] declaredMethods = rf.getClass().getDeclaredMethods();
                Arrays.stream(declaredMethods)
                        .filter(m -> m.isAnnotationPresent(RequestRouting.class))
                        .forEach(m -> {
                            RequestRouting annotation = m.getAnnotation(RequestRouting.class);
                            io.vertx.reactivex.ext.web.Route route = router.route(rootPath.concat(annotation.value())).method(annotation.method());
                            if (annotation.blocked()) {
                                route.blockingHandler(rtx -> {
                                    try {
                                        m.invoke(rf, rtx);
                                    } catch (Exception e) {
                                        log.error("Please check the method:{} is normal.", m.getName());
                                    }
                                });
                            } else {
                                route.handler(rtx -> {
                                    try {
                                        m.invoke(rf, rtx);
                                    } catch (Exception e) {
                                        log.error("Please check the method:{} is normal.", m.getName());
                                    }
                                });
                            }
                        });
            });
        }
        int port = this.port == 0 ? DEFAULT_PORT : this.port;
        vertx.createHttpServer().requestHandler(router).listen(port);
        log.info("web server listen at {} successfully.", port);
    }
}
