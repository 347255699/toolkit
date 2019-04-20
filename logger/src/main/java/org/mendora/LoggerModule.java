package org.mendora;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/4/19
 * desc:
 */
@Builder
public class LoggerModule {
    private String logFileName;

    private String logFilePattern;

    private Level logLevel;

    private String layoutPattern;

    private String rollingSize;

    private int maxRollingFileBound;

    private static final String LOG_FILE_NAME = "logs/application.log";

    private static final String LAYOUT_PATTERN = "[%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n";

    private static final String ROLLING_SIZE = "30MB";

    private static final int MAX_ROLLING_FILE_BOUND = 30;

    private Configuration buildConfiguration() {
        ConfigurationBuilder<BuiltConfiguration> configurationBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

        // building layout
        LayoutComponentBuilder standardLayoutBuilder = configurationBuilder.newLayout("PatternLayout")
                .addAttribute("pattern", StringUtils.isEmpty(layoutPattern) ? LAYOUT_PATTERN : layoutPattern);

        // building console appender
        AppenderComponentBuilder consoleAppenderBuilder = configurationBuilder.newAppender("stdout", "Console")
                .add(standardLayoutBuilder)
                .addAttribute("target", "SYSTEM_OUT");
        configurationBuilder.add(consoleAppenderBuilder);

        // building rolling file appender
        ComponentBuilder sizeBasedTriggeringPolicy = configurationBuilder.newComponent("SizeBasedTriggeringPolicy")
                .addAttribute("size", StringUtils.isEmpty(rollingSize) ? ROLLING_SIZE : rollingSize);
        ComponentBuilder policiesBuilder = configurationBuilder.newComponent("Policies")
                .addComponent(sizeBasedTriggeringPolicy);
        ComponentBuilder defaultRollOverStrategyBuilder = configurationBuilder.newComponent("DefaultRollOverStrategy")
                .addAttribute("max", maxRollingFileBound == 0 ? MAX_ROLLING_FILE_BOUND : maxRollingFileBound);
        AppenderComponentBuilder rollingFileAppenderBuilder = configurationBuilder.newAppender("rolling", "RollingFile")
                .addAttribute("fileName", StringUtils.isEmpty(logFileName) ? LOG_FILE_NAME : logFileName)
                .add(standardLayoutBuilder)
                .addComponent(policiesBuilder)
                .addComponent(defaultRollOverStrategyBuilder);
        if (StringUtils.isNoneEmpty(logFilePattern)) {
            rollingFileAppenderBuilder.addAttribute("filePattern", logFilePattern);
        } else if (StringUtils.isNoneEmpty(logFileName)) {
            rollingFileAppenderBuilder.addAttribute("filePattern", logFileName + ".%d{yyyy-MM-dd-hh-mm}.gz");
        } else {
            rollingFileAppenderBuilder.addAttribute("filePattern", LOG_FILE_NAME + ".%d{yyyy-MM-dd-hh-mm}.gz");
        }
        configurationBuilder.add(rollingFileAppenderBuilder);

        // building root logger
        RootLoggerComponentBuilder rootLogger = configurationBuilder.newRootLogger(logLevel == null ? Level.INFO : logLevel)
                .add(configurationBuilder.newAppenderRef("stdout"))
                .add(configurationBuilder.newAppenderRef("rolling"));

        return configurationBuilder.add(rootLogger).build();
    }

    public void run() {
        Configurator.initialize(buildConfiguration());
    }
}
