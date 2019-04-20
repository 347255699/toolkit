package org.mendora;

import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.Builder;
import org.apache.logging.log4j.Level;
import org.mendora.facade.Route;
import org.mendora.facade.RouteFactory;
import org.mendora.scan.PackageScanner;
import org.mendora.scan.PackageScannerImpl;
import org.mendora.verticle.WebVerticle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
public class WebModule {
    private String basePackageName;

    private String logFileName;

    private Level level;

    private boolean fileCachingEnabled;

    private int workerPoolSize;

    private static final int WORKER_POOL_ZISE = 10;

    private int port;

    private Optional<Class<?>> classForName(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<RouteFactory> newInstance(Class<?> clazz) {
        try {
            return Optional.of((RouteFactory)clazz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void run() {
        LoggerModule.builder()
                .logFileName(logFileName)
                .logLevel(level)
                .build()
                .run();
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        VertxOptions vertxOptions = new VertxOptions()
                .setFileSystemOptions(new FileSystemOptions().setFileCachingEnabled(fileCachingEnabled))
                .setWorkerPoolSize(workerPoolSize == 0 ? WORKER_POOL_ZISE : workerPoolSize);
        Vertx vertx = Vertx.vertx(vertxOptions);

        PackageScanner<RouteFactory> scanner = new PackageScannerImpl<>(basePackageName, WebModule.class.getClassLoader());
        List<RouteFactory> routeFactories= scanner.classNames(RouteFactory.class.getName())
                .stream()
                .map(this::classForName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(clazz -> !clazz.isInterface() && !clazz.isEnum())
                .filter(clazz -> clazz.isAnnotationPresent(Route.class))
                .map(this::newInstance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        WebVerticle webVerticle = new WebVerticle(routeFactories, port);
        vertx.deployVerticle(webVerticle);
    }
}
