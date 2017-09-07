package com.wcpdoc.exam.viewlog.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.viewlog.service.LogService;

/**
 * 日志服务层实现
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
@Service
public class LogServiceImpl implements LogService {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String dir = System.getProperty("exam.root");
		File logFile = new File(dir + File.separator + "WEB-INF" + File.separator + "log" + File.separator + "myLog.log");
		List<String> readLastLine = StringUtil.getLastLine(logFile, 200);
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		for(String lineStr : readLastLine){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LINE_STR", lineStr);
			resultList.add(map);
		}
		
		return new PageOut(resultList, resultList.size());
	}
}
