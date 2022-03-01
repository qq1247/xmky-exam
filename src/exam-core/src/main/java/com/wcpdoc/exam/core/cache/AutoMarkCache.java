package com.wcpdoc.exam.core.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;

import com.wcpdoc.cache.BaseEhCache;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.service.ExamService;

/**
 * 自动阅卷缓存
 * 
 * v1.0 zhanghc 2021年10月25日下午3:10:12
 */
public class AutoMarkCache extends BaseEhCache {
	private static final String CACHE_NAME = "AUTO_MARK_CACHE";

	/**
	 * 放入缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param examId
	 * @param exam 
	 * void
	 */
	public static void put(Integer examId, Exam exam) {
		Cache cache = getCache(CACHE_NAME);
		cache.put(examId, exam);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param examId
	 * @param Exam 
	 * void
	 */
	public static Exam get(Integer examId) {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(examId, Exam.class);
	}
	
	/**
	 * 删除缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param examId
	 * @param value 
	 * void
	 */
	public static void del(Integer examId) {
		Cache cache = getCache(CACHE_NAME);
		cache.evict(examId);
	}
	
	/**
	 * 缓存列表
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value 
	 * void
	 */
	@SuppressWarnings("unchecked")
	public static List<Exam> getList() {
		Cache cache = getCache(CACHE_NAME);
		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
		List<Integer> examIdList = nativeCache.getKeys();
		List<Exam> examList = new ArrayList<Exam>();
		for (Integer examId : examIdList) {
			examList.add(get(examId));
		}
		return examList;
	}

	/**
	 * 重新加载缓存
	 * 
	 * v1.0 zhanghc 2022年2月24日上午10:47:16
	 * void
	 */
	public static void reloadCache() {
		Cache cache = getCache(CACHE_NAME);
		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
		nativeCache.removeAll();
		
		List<Exam> examList = SpringUtil.getBean(ExamService.class).getUnMarkList();
		for (Exam exam : examList) {
			cache.put(exam.getId(), exam);
		}
	}
}
