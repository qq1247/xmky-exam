package com.wcpdoc.base.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import com.wcpdoc.base.entity.ProgressBar;
import com.wcpdoc.cache.BaseEhCache;

/**
 * 进度条缓冲
 * 
 * v1.0 zhanghc 2017年7月5日下午4:22:50
 */
public class ProgressBarCache extends BaseEhCache {
	private static final Logger log = LoggerFactory.getLogger(ProgressBarCache.class);
	private static final String CACHE_NAME = "PROGRESS_BAR_CACHE";
	
	/**
	 * 设置进度条
	 * 
	 * v1.0 zhanghc 2020年10月13日下午12:00:50
	 * @param id 唯一ID
	 * @param curNum
	 * @param totalNum
	 * @param msg 
	 * @return String 唯一ID，用于前端显示当前进度
	 */
	public static String setProgressBar(String id, Double curNum, Double totalNum, String msg, Integer code) {
		log.debug("设置进度条：{}-{}-{}-{}", id, curNum, totalNum, msg);
		Cache cache = getCache(CACHE_NAME);
		ProgressBar progressBar = cache.get(id, ProgressBar.class);
		if (progressBar == null) {
			progressBar = new ProgressBar(id, curNum, totalNum, msg, code);
			cache.put(id, progressBar);
		}
		
		progressBar.setCurNum(curNum);
		progressBar.setTotalNum(totalNum);
		progressBar.setMsg(msg);
		progressBar.setCode(code);
		return id;
	}
	
	/**
	 * 获取进度条
	 * 
	 * v1.0 zhanghc 2020年10月13日下午12:12:12
	 * @param id
	 * @return ProgressBar
	 */
	public static ProgressBar getProgressBar(String id) {
		Cache cache = getCache(CACHE_NAME);
		ProgressBar progressBar = cache.get(id, ProgressBar.class);
		if (progressBar == null) {
			log.debug("获取进度条：{}-未找到", id);
			return progressBar;
		}
			
		log.debug("获取进度条：{}-{}-{}-{}", id, progressBar.getCurNum(), progressBar.getTotalNum(), progressBar.getMsg(), progressBar.getCode());
		return progressBar;
	}
}
