package org.mendora.verticle;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.mendora.LoggerApplication;

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
		ROUTER = Router.router(VERTX);
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		ROUTER.get("/text").handler(rtx -> {
			rtx.response().end("999dsfdsf");
			log.info("ok");
		});

		VERTX.createHttpServer().requestHandler(ROUTER).listen(8080);
	}

	public static void main(String[] args) {
		String[] args2 = {"/Users/pundix043/workbench/copy/toolkit"};
		LoggerApplication.run(args2);
		VERTX = Vertx.vertx();
		VERTX.deployVerticle(WebVerticle.class.getName());
	}
}
