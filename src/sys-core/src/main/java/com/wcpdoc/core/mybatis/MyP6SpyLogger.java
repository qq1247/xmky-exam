package com.wcpdoc.core.mybatis;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.wcpdoc.core.util.ValidateUtil;

public class MyP6SpyLogger implements MessageFormattingStrategy {
	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared,
			String sql, String url) {
		if (!ValidateUtil.isValid(sql)) {
			return "";
		}

		return String.format("%s; %s毫秒", sql.replaceAll("[\\s]+", " "), elapsed);
	}
}