package com.wcpdoc.search.service;

import java.util.List;

import com.wcpdoc.search.entity.DocSummary;

/**
 * 文档检索服务层接口
 * 
 * v1.0 zhanghc 2026年1月7日下午11:33:10
 */
public interface DocIndexService {

	/**
	 * 文档检索添加
	 * 
	 * v1.0 zhanghc 2026年1月7日下午11:33:10
	 * 
	 * @param bizId
	 * @param fileId
	 */
	void add(Integer bizId, Integer fileId);

	/**
	 * 文档检索删除
	 * 
	 * v1.0 zhanghc 2026年1月7日下午11:33:10
	 * 
	 * @param bizId
	 * @param fileId void
	 */
	void del(Integer bizId, Integer fileId);

	/**
	 * 文档检索查询
	 * 
	 * v1.0 zhanghc 2026年1月7日下午11:33:10
	 * 
	 * @param bizId
	 * @param content
	 * @param pageSize
	 * @return List<DocSummary>
	 */
	List<DocSummary> search(Integer bizId, String content, Integer pageSize);

	/**
	 * 文档索引删除
	 * 
	 * v1.0 zhanghc 2026年1月10日上午9:54:00 void
	 */
	void delAll();
}
