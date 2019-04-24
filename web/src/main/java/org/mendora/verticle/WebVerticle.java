package org.mendora.verticle;

import io.vertx.core.Handler;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
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

	public WebVerticle(List<RouteFactory> routeFactories, int port) {
		this.routeFactories = routeFactories;
		this.port = port;
	}

	private void handleRoute(RouteFactory rf, Method m){
        String rootPath = rf.getClass().getAnnotation(Route.class).value();
        RequestRouting annotation = m.getAnnotation(RequestRouting.class);
        io.vertx.reactivex.ext.web.Route route = router.route(rootPath.concat(annotation.value())).method(annotation.method());
        Handler<RoutingContext> requestHandler = rtx -> {
            try {
                m.invoke(rf, rtx);
            } catch (Exception e) {
                log.error("Please check the method:{} is normal.", m.getName());
            }
        };
        if (annotation.blocked()) {
            route.blockingHandler(requestHandler);
        } else {
            route.handler(requestHandler);
        }
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
				Method[] declaredMethods = rf.getClass().getDeclaredMethods();
				Arrays.stream(declaredMethods)
					.filter(m -> m.isAnnotationPresent(RequestRouting.class))
					.forEach(m -> handleRoute(rf, m));
			});
		}
		vertx.createHttpServer().requestHandler(router).listen(port);
		log.info("web server listen at {} successfully.", port);
	}
}
