package org.test.agf.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	private static final Logger log = LoggerFactory.getLogger(Configuration.class);
	
	static Properties prop = null;
	private static AtomicBoolean loaded = new AtomicBoolean(false);
	
	public static void loadConfiguration()  {
		String dir = System.getProperty("movicatt.config.file");
		log.info("Trying to load configuration from java property (-Dmovicatt.config.file)=" + dir);
		if (dir != null && !dir.isEmpty()) {
			loadConfiguration(dir);
		} else {
			log.warn("No external configuration file. Using default values");
		}
		loaded.set(true);
	}
	
	public static void loadConfiguration(String path) {
		prop = new Properties();
		File file = new File(path);
		if (file.exists()) {
			try {
				FileInputStream input = new FileInputStream(path);
				prop.load(input);
				input.close();
			} catch (Throwable th) {
				log.info("Error loading configuration external file. Detail:" + th, th);
			}
		}
	}

	public static String getValue(ConfigParam param) {
		String value = null;
		if (!loaded.get()) {
			loadConfiguration();
		}
		
		if (prop !=  null) {
			value = (String) prop.get(param.getProperty());
		}
		if (value == null) {
			value = param.getDefaultValue();
		}
		return value;
	}

	public static long getLongValue(ConfigParam param) {
		long value = 0; 
		try {
			value = Long.parseLong(getValue(param));
		} catch (Throwable th ){
			value = Long.parseLong(param.getDefaultValue());
		}
		return value;
	}
	
	public static boolean getBoolValue(ConfigParam param) {
		boolean value = false; 
		try {
			value = Boolean.parseBoolean(getValue(param));
		} catch (Throwable th ){
			value = Boolean.parseBoolean(param.getDefaultValue());
		}
		return value;
	}

	public static int getIntValue(ConfigParam param) {
		int value = 0; 
		try {
			value = Integer.parseInt(getValue(param));
		} catch (Throwable th ){
			value = Integer.parseInt(param.getDefaultValue());
		}
		return value;
	}
	
	public static String[] getArrayValues(ConfigParam param) {
		String[] ret = new String[]{};
		String value = getValue(param);
		if (value != null && !value.isEmpty()) {
			ret = value.split("\\,");
		}
		return ret;
	}
}
