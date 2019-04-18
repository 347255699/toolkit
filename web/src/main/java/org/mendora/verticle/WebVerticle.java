package org.mendora.verticle;

import com.sun.javafx.runtime.SystemProperties;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/4/18
 * desc:
 */
@Slf4j
public class WebVerticle extends AbstractVerticle {
	private static Vertx VERTX;
	private static Router ROUTER;

	@Override
	public void start() throws Exception {
		String root = PathUtil.root();
		ROUTER = Router.router(VERTX);
		System.setProperty("log4j.configurationFile", root + "/log4j2.yaml");
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		ROUTER.get("/text").handler(rtx -> {
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
