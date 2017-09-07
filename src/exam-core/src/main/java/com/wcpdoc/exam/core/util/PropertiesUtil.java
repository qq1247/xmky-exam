package com.wcpdoc.exam.core.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
	private static final Properties properties = new Properties();
	static {
		try {
			properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (Exception e) {
			log.error("初始化config配置文件失败：", e);
		}
	}

	public static String getValue(String key) {
		Object valueObj = properties.get(key);
		if (valueObj == null) {
			return null;
		}

		return valueObj.toString();
	}
}
