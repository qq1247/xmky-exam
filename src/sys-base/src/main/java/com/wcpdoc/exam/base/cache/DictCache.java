package com.wcpdoc.exam.base.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;

import com.wcpdoc.exam.base.entity.Dict;
import com.wcpdoc.exam.base.service.DictService;
import com.wcpdoc.exam.cache.BaseEhCache;
import com.wcpdoc.exam.core.util.SpringUtil;

/**
 * 数据字典缓存
 * 
 * v1.0 zhanghc 2017年7月5日下午11:51:07
 */
public class DictCache extends BaseEhCache {
	private static final Logger log = LoggerFactory.getLogger(DictCache.class);
	private static final String CACHE_NAME = "MY_CACHE";
	private static final String DICT_INDEX_DICTLIST_MAP = "DICT_INDEX_DICTLIST_MAP";//{index : [dict, dict, dict]}
	private static final String DICT_INDEXKEY_DICT_MAP = "DICT_INDEXKEY_DICT_MAP";//{index+key : dict}
	private static final String DICT_INDEXKEY_VALUE_MAP = "DICT_INDEXKEY_VALUE_MAP";//{index+key : value}

	static {
		initCache();
	}

	/**
	 * 初始化缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午4:06:30 void
	 */
	private static void initCache() {
		log.info("缓存：数据字典初始化开始");
		Cache cache = getCache(CACHE_NAME);
		cache.put(DICT_INDEX_DICTLIST_MAP, new HashMap<String, List<Dict>>());
		cache.put(DICT_INDEXKEY_DICT_MAP, new HashMap<String, Dict>());
		cache.put(DICT_INDEXKEY_VALUE_MAP, new HashMap<String, String>());
		log.info("缓存：数据字典初始化成功");
	}

	/**
	 * 刷新缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午4:06:30 void
	 */
	public static void flushCache() {
		synchronized (DictCache.class) {
			log.info("缓存：刷新数据字典开始");
			clearCache();
			addCache();
			log.info("缓存：刷新数据字典成功");
		}
	}

	/**
	 * 清除缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午4:12:24 void
	 */
	private static void clearCache() {
		getIndexDictlistMap().clear();
		getIndexkeyDictMap().clear();
		getIndexkeyValueMap().clear();
	}

	/**
	 * 添加缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午4:14:01 
	 * void
	 */
	private static void addCache() {
		List<Dict> dictList = getDictList();
		for (Dict dict : dictList) {
			addCache(dict);
		}
	}

	/**
	 * 添加缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午4:14:01 
	 * void
	 */
	private static void addCache(Dict dict) {
		Dict source = dict;
		Dict target = new Dict();
		BeanUtils.copyProperties(source, target);

		Map<String, List<Dict>> indexDictlistMap = getIndexDictlistMap();
		List<Dict> dictKeyList = indexDictlistMap.get(dict.getDictIndex());
		if (dictKeyList == null) {
			dictKeyList = new ArrayList<Dict>();
			indexDictlistMap.put(dict.getDictIndex(), dictKeyList);
		}
		dictKeyList.add(target);
		Collections.sort(dictKeyList, new Comparator<Dict>() {
			@Override
			public int compare(Dict o1, Dict o2) {
				return o1.getNo() - o2.getNo();
			}
		});

		Map<String, Dict> indexkeyDictMap = getIndexkeyDictMap();
		indexkeyDictMap.put(target.getDictIndex() + "_" + target.getDictKey(), dict);

		Map<String, String> dictIndexkeyValueMap = getIndexkeyValueMap();
		dictIndexkeyValueMap.put(target.getDictIndex() + "_" + target.getDictKey(), dict.getDictValue());
	}

	/**
	 * 获取数据字典列表
	 * 
	 * v1.0 zhanghc 2017年7月7日下午4:19:22
	 * 
	 * @return List<Dict>
	 */
	private static List<Dict> getDictList() {
		return SpringUtil.getBean(DictService.class).getList();
	}

	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午2:33:58
	 * 
	 * @return Map<String,Dict>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Dict> getIndexkeyDictMap() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(DICT_INDEXKEY_DICT_MAP, Map.class);
	}

	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午2:33:58
	 * 
	 * @return Map<String,Dict>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Dict>> getIndexDictlistMap() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(DICT_INDEX_DICTLIST_MAP, Map.class);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午2:33:58
	 * 
	 * @return Map<String,Dict>
	 */
	public static List<Dict> getDictList(String index) {
		return getIndexDictlistMap().get(index);
	}

	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2017年7月7日下午2:33:58
	 * 
	 * @return Map<String,Dict>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getIndexkeyValueMap() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(DICT_INDEXKEY_VALUE_MAP, Map.class);
	}

	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2017年7月5日下午11:51:24
	 * 
	 * @param index
	 * @param key
	 * @return String
	 */
	public static Dict getDict(String index, String key) {
		return getIndexkeyDictMap().get(index + "_" + key);
	}

	/**
	 * 获取缓存值
	 * 
	 * v1.0 zhanghc 2017年7月7日下午2:29:21
	 * 
	 * @param index
	 * @param key
	 * @return String
	 */
	public static String getDictValue(String index, String key) {
		Dict dict = getDict(index, key);
		return dict == null ? null : dict.getDictValue();
	}
}
