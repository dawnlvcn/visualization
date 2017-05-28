package com.citi.vdict.utils;

import java.io.FileInputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class PropertiesUtils {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	private static String getFilePath(String filePath) {
		String localFilePath = filePath.replace("\\\\", "/").replace("\\", "/");
		URL url = PropertiesUtils.class.getClassLoader().getResource(localFilePath);
		return url.getFile();
	}

	public static Properties loadPropertiesFile(String filePath) {
		logger.info("start load property file: " + filePath);
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(getFilePath(filePath)));
		} catch (Exception e) {
			properties = null;
			logger.error(e.getMessage());
		}
		return properties;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadYamlFile(String filePath) {
		logger.info("start load yaml file: " + filePath);
		Yaml yaml = new Yaml();
		Map<String, Object> result = null;
		try {
			result = (LinkedHashMap<String, Object>) yaml.load(new FileInputStream(getFilePath(filePath)));
		} catch (Exception e) {
			logger.error(filePath + " cannot be load", e.getMessage());
			result = null;
		}
		return result;
	}

	public static void main(String[] args) {
		Map<String, Object> result = loadYamlFile("view\\oracle.default.view.yaml");
		for (String key : result.keySet()) {
			System.out.println(key + " type: " + result.get(key).getClass().getTypeName());
			System.out.println(key + "=" + result.get(key));
		}
	}
}
