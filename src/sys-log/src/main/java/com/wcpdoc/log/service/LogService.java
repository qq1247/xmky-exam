package com.wcpdoc.log.service;

import java.io.File;
import java.util.Map;

/**
 * 日志服务层接口
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
public interface LogService {

	/**
	 * 设置日志目录
	 * 
	 * v1.0 zhanghc 2020年8月25日上午11:26:47
	 * @param logDir void
	 */
	void setLogDir(File logDir);

	/**
	 * 获取日志内容
	 * 
	 * v1.0 zhanghc 2020年8月25日上午11:29:59
	 * @param logName 日志文件名称
	 * @param curReadLen  当前读取日志文件字节数
	 * @return Map<String, Object>
	 */
	Map<String, Object> getList(String logName, Long curReadLen);
}
