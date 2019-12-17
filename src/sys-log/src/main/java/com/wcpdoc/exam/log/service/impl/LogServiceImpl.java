package com.wcpdoc.exam.log.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.log.service.LogService;

/**
 * 日志服务层实现
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
@Service
public class LogServiceImpl implements LogService {
	@Value("${file.log.dir}")
	private String logDir;
	@Value("${file.log.name}")
	private String logName;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		File logFile = new File(logDir + logName);
		List<String> readLastLine = StringUtil.getLastLine(logFile, 200);

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (String lineStr : readLastLine) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LINE_STR", lineStr);
			resultList.add(map);
		}

		return new PageOut(resultList, resultList.size());
	}
}
