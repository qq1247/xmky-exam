package com.wcpdoc.base.cache;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.cache.Cache;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.cache.BaseEhCache;
import com.wcpdoc.core.exception.MyException;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统参数缓存
 * 
 * v1.0 chenyun 2021年11月12日下午1:25:42
 */
@Slf4j
public class ParmCache extends BaseEhCache {
	private static final String CACHE_NAME = "MY_CACHE";
	private static final String PARM_KEY = "PARM_KEY";

	/**
	 * 刷新缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:29 void
	 */
	public static void flushCache(Parm parm) {
		Cache cache = getCache(CACHE_NAME);
		Parm source = new Parm();
		try {
			BeanUtils.copyProperties(source, parm);// 如果在事务内，修改属性会同步到数据库。查阅TransactionConf
		} catch (Exception e) {// 测试，非业务层方法调用获取parm.getById(1)，修改属性，不会同步到数据库。事务已结束
			log.error("刷新缓存异常：参数拷贝失败", e);// 非业务层方法调用业务层方法，业务层获取parm.getById(1)，修改属性，会同步到数据库。有新事务则加入事务
			throw new MyException("刷新缓存异常：参数拷贝失败");
		}
		cache.put(PARM_KEY, source);
	}

	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:21
	 * 
	 * @return Parm
	 */
	public static Parm get() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(PARM_KEY, Parm.class);
	}
}
