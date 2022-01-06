package com.wcpdoc.log.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.log.service.LogService;

/**
 * 日志服务层实现
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
@Service
public class LogServiceImpl implements LogService {
	private File logDir;
	
	@Override
	public Map<String, Object> getList(String logName, Long curReadLen) {
		// 如果第一次访问，获取前一万个字节的字符串
		File logFile = new File(String.format("%s/%s", logDir.getAbsolutePath(), logName));
		Long fileLen = logFile.length();
		if (curReadLen == null) {
			curReadLen = fileLen - 10000;
			curReadLen = curReadLen < 0 ? 0 : curReadLen;
			
			List<String> strList = StringUtil.getString(logFile, curReadLen, fileLen);
			Map<String, Object> result = new HashMap<>();
			result.put("CUR_READ_LEN", fileLen);
			result.put("STR_LIST", strList);
			return result;
		}
		
		// 如果没有新数据，则直接返回
		if (curReadLen >= fileLen) {
			curReadLen = fileLen;
			Map<String, Object> result = new HashMap<>();
			result.put("CUR_READ_LEN", fileLen);
			result.put("STR_LIST", new ArrayList<>());
			return result;
		}
			
		// 读取参数字节长度到日志末尾字节长度的字符串
		List<String> strList = StringUtil.getString(logFile, curReadLen, fileLen);
		Map<String, Object> result = new HashMap<>();
		result.put("CUR_READ_LEN", fileLen);
		result.put("STR_LIST", strList);
		return result;
	}

	@Override
	public void setLogDir(File logDir) {
		this.logDir = logDir;
	}
}
