package org.mendora;

import org.apache.logging.log4j.core.lookup.MainMapLookup;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/4/19
 * desc:
 */
public class LoggerApplication {
	public static void run(String[] args) {
		String confPath = LoggerApplication.class.getClassLoader().getResource("log4j2.yaml").getPath();
		if (confPath == null || confPath.length() == 0) {
			throw new RuntimeException("Could not found the log4j2.yaml file path.");
		}
		System.out.println("confPath: " + confPath);
		MainMapLookup.setMainArguments(args);
		System.setProperty("log4j.configurationFile", confPath);
	}
}
