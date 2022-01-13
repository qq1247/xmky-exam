package com.wcpdoc.file.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import com.wcpdoc.cache.BaseEhCache;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 附件ID缓存
 * 
 * v1.0 chenyun 2021年9月3日下午4:18:26
 */
public class FileIdCache extends BaseEhCache{
	private static final Logger log = LoggerFactory.getLogger(FileIdCache.class);
	private static final String CACHE_NAME = "FILE_ID_CACHE";
	
	/**
	 * 设置附件缓存
	 * 
	 * v1.0 chenyun 2021年9月3日下午4:17:51
	 * @param id(uuid)
	 * @param fileId
	 * @return String
	 */
	public static void setFileId(String id, String fileId) {
		log.debug("设置附件缓存：{}-{}", id, fileId);
		Cache cache = getCache(CACHE_NAME);
		String cacheFileId = cache.get(id, String.class);
		if (ValidateUtil.isValid(cacheFileId)) {
			log.debug("附件上传失败：{}-已存在", id);
			throw new MyException("上传失败");
		}
		cache.put(id, fileId);
	}
	
	/**
	 * 获取附件缓存
	 * 
	 * v1.0 chenyun 2021年9月3日下午4:17:45
	 * @param id(uuid)
	 * @return Integer fileId
	 */
	public static String getFileId(String id) {
		Cache cache = getCache(CACHE_NAME);
		String fileId = cache.get(id, String.class);
		if (fileId == null) {
			log.debug("获取附件缓存：{}-未找到", id);
			return fileId;
		}
			
		log.debug("获取附件缓存：{}-{}", id, fileId);
		return fileId;
	}
}
