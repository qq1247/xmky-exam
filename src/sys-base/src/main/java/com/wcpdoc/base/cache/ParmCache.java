package com.wcpdoc.base.cache;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.cache.BaseEhCache;
import com.wcpdoc.core.exception.MyException;

/**
 * 系统参数缓存
 * 
 * v1.0 chenyun 2021年11月12日下午1:25:42
 */
public class ParmCache extends BaseEhCache {
	private static final Logger log = LoggerFactory.getLogger(ParmCache.class);
	private static final String CACHE_NAME = "MY_CACHE";
	private static final String PARM_KEY = "PARM_KEY";

	/**
	 * 刷新缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:29 void
	 */
	public static void flushCache(Parm parm) {
		Cache cache = getCache(CACHE_NAME);
		Parm target = new Parm();
		try {
			BeanUtils.copyProperties(parm, target);// 如果在事务内，修改属性会同步到数据库
		} catch (Exception e) {
			log.error("刷新缓存异常：参数拷贝失败", e);
			throw new MyException("刷新缓存异常：参数拷贝失败");
		}
		cache.put(PARM_KEY, target);
	}

	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:21
	 * @return Parm
	 */
	public static Parm get() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(PARM_KEY, Parm.class);
	}
}
