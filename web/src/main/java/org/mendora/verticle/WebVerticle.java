package org.mendora.verticle;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
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
import org.mendora.vo.Resp;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
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
	private String webRoot;

	public WebVerticle(List<RouteFactory> routeFactories, int port, String webRoot) {
		this.routeFactories = routeFactories;
		this.port = port;
		this.webRoot = webRoot;
	}

	private void handleRoute(RouteFactory rf, Method m) {
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

	private void customStaticHandler() {
		router.route("/html/*").blockingHandler(rxt -> {
			HttpServerRequest request = rxt.request();
			HttpServerResponse response = rxt.response();
			String path = request.path();
			String fileName = path.substring(path.indexOf("/html/") + 5);
			String absoluteFileName = webRoot.concat(fileName);
			try {
				File file = new File(absoluteFileName);
				if (!file.exists()) {
					Resp resp = new Resp<>(404, null, "resources not found.");
					response.end(JsonObject.mapFrom(resp).toString());
				}
				ByteBuffer bf = ByteBuffer.allocate((int) file.length());
				final RandomAccessFile rFile = new RandomAccessFile(file, "r");
				rFile.getChannel().read(bf);
				response.end(Buffer.buffer(bf.array()));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	@Override
	public void start() throws Exception {
		router = Router.router(vertx);
		router.route().handler(LoggerHandler.create());
		BodyHandler bodyHandler = BodyHandler.create();
		if (StringUtils.isNoneEmpty(System.getProperty("uploadDir"))) {
			bodyHandler.setUploadsDirectory(System.getProperty("uploadDir"));
		}
		router.route().handler(bodyHandler);
		if (StringUtils.isNoneEmpty(webRoot)) {
			customStaticHandler();
		} else {
			router.route("/html/*").handler(StaticHandler.create("/static").setCachingEnabled(false));
		}
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
