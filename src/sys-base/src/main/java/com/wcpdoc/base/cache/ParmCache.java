package com.wcpdoc.base.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.cache.BaseEhCache;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 系统参数缓存
 * 
 * v1.0 chenyun 2021年11月12日下午1:25:42
 */
public class ParmCache extends BaseEhCache {
	private static final Logger log = LoggerFactory.getLogger(ParmCache.class);
	private static final String CACHE_NAME = "MY_CACHE";
	private static final String PARM_VALUE = "PARM_VALUE";

	static {
		initCache();
	}

	/**
	 * 初始化缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:36 void
	 */
	private static void initCache() {
		log.info("缓存：系统参数初始化开始");
		Cache cache = getCache(CACHE_NAME);
		cache.put(PARM_VALUE, new HashMap<String, Parm>());
		log.info("缓存：系统参数初始化成功");
	}

	/**
	 * 刷新缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:29 void
	 */
	public static void flushCache() {
		synchronized (ParmCache.class) {
			log.info("缓存：刷新系统参数开始");
			clearCache();
			addCache();
			log.info("缓存：刷新系统参数成功");
		}
	}

	/**
	 * 清除缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:23 void
	 */
	private static void clearCache() {
		getParmMap().clear();
	}

	/**
	 * 添加缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:17 void
	 */
	private static void addCache() {
		Parm parm = SpringUtil.getBean(ParmService.class).get();
		//为空默认赋值
		if (parm == null) {
			parm = new Parm();
			parm.setUpdateUserId(1);
			parm.setUpdateTime(new Date());
			parm.setFileUploadDir(String.format("%s/%s", System.getProperty("user.dir"), "bak/file"));
			parm.setDbBakDir(String.format("%s/%s", System.getProperty("user.dir"), "bak/db"));
			parm.setPwdType(2);
			parm.setPwdValue("111111");
			SpringUtil.getBean(ParmService.class).add(parm);
		}
		
		Parm source = parm;
		Parm target = new Parm();
		BeanUtils.copyProperties(target, target);
		
		target.setFileUploadDir(String.format("%s/%s", System.getProperty("user.dir"), "bak/file"));
		if (ValidateUtil.isValid(source.getFileUploadDir())) {
			target.setFileUploadDir(String.format("%s/%s", source.getFileUploadDir(), "bak/file"));
		}
		java.io.File fileUploadDir = new java.io.File(target.getFileUploadDir());
		if (!fileUploadDir.isAbsolute()) {
			target.setFileUploadDir(String.format("%s/%s", System.getProperty("user.dir"), target.getFileUploadDir()));// 如果是相对路径，备份路径为当前war包启动路径+配置文件子目录
		}

		target.setDbBakDir(String.format("%s/%s", System.getProperty("user.dir"), "bak/db"));
		if (ValidateUtil.isValid(source.getDbBakDir())) {
			target.setDbBakDir(String.format("%s/%s", source.getDbBakDir(), "bak/db"));
		}
		java.io.File dbBakDir = new java.io.File(target.getDbBakDir());
		if (!dbBakDir.isAbsolute()) {
			target.setDbBakDir(String.format("%s/%s", System.getProperty("user.dir"), target.getDbBakDir()));// 如果是相对路径，备份路径为当前war包启动路径+配置文件子目录
		}
		
		Map<String, Parm> parmMap = getParmMap();
		parmMap.put("parm", target);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:10
	 * @return Map<String,Parm>
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Parm> getParmMap() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(PARM_VALUE, Map.class);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:54
	 * @return Parm
	 */
	private static Parm getParm() {
		return getParmMap().get("parm");
	}
	
	/**
	 * 获取缓存值
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:21
	 * @return Parm
	 */
	public static Parm get() {
		Parm parm = getParm();
		return parm == null ? null : parm;
	}
}
