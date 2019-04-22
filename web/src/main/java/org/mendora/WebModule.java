package org.mendora;

import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.Builder;
import org.mendora.facade.Route;
import org.mendora.facade.RouteFactory;
import org.mendora.scan.PackageScanner;
import org.mendora.scan.PackageScannerImpl;
import org.mendora.verticle.WebVerticle;

import java.util.List;

@Builder
public class WebModule {
	private boolean worker;

	private int port;

	public void run(Vertx vertx, String basePackageName) {
		PackageScanner<RouteFactory> scanner = new PackageScannerImpl<>(basePackageName, WebModule.class.getClassLoader());
		List<RouteFactory> routeFactories = scanner.newInstances(scanner.fullClassNames(RouteFactory.class.getName()), Route.class);
		WebVerticle webVerticle = new WebVerticle(routeFactories, port == 0? 8080:port);
		DeploymentOptions deploymentOptions = new DeploymentOptions()
			.setWorker(worker);
		vertx.deployVerticle(webVerticle, deploymentOptions);
	}
}
