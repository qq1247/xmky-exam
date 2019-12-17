package com.wcpdoc.exam.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Hibernate工具类
 * 
 * @author zhanghc 2015-7-21下午10:20:25
 */
public class HibernateUtil {
	/**
	 * 格式化时间</br>
	 * 
	 * v1.0 zhanghc 2015-3-30下午02:29:10
	 * 
	 * @param list
	 * @param params {"CREATETIME", "yyyy-MM-dd", "UPDATETIME", "yyyy-MM-dd HH:mm:ss"}
	 * 
	 * void
	 */
	public static void formatDate(List<Map<String, Object>> list, String... params) {
		SimpleDateFormat format = new SimpleDateFormat();

		for (Map<String, Object> map : list) {
			for (int i = 0; i < params.length; i++) {
				String key = params[i];
				String pattern = params[++i];
				Date value = (Date) map.get(key);
				if (value != null) {
					format.applyPattern(pattern);
					map.put(key + "_STR", format.format(value));
				}
			}
		}
	}

	/**
	 * 格式化数据字典
	 * 
	 * v1.0 zhanghc 2017年7月5日下午11:57:29
	 * 
	 * @param list
	 * @param dictMap {0:删除, 1:启用, 2:禁用}
	 * @param params {"字典项", "list[i].field", "字典项", "list[i].field"... ...} void
	 */
	public static void formatDict(List<Map<String, Object>> list, Map<String, String> dictMap, String... params) {
		for (Map<String, Object> map : list) {
			for (int i = 0; i < params.length; i++) {
				String index = params[i];
				String key = map.get(params[++i]).toString();
				String indexKey = index + "_" + key;
				String value = dictMap.get(indexKey);
				map.put(params[i] + "_NAME", value);
			}
		}
	}
}
