package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionType;
/**
 * 试题分类服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeService extends BaseService<QuestionType> {
	
	/**
	 * 添加试题分类
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType
	 * void
	 */
	void addAndUpdate(QuestionType questionType);

	/**
	 * 修改试题
	 * 
	 * v1.0 chenyun 2021年3月18日上午10:20:28
	 * @param questionType
	 * void
	 */
	void editAndUpdate(QuestionType questionType);
	
	/**
	 * 删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);

	/**
	 * 授权
	 * 
	 * v1.0 zhanghc 2020年9月8日上午10:06:53
	 * @param id
	 * @param writeUserIds
	 * void
	 */
	void auth(Integer id, Integer[] writeUserIds);

	/**
	 * 拥有写权限
	 * 
	 * v1.0 zhanghc 2021年7月1日下午2:13:57
	 * @param questionType
	 * @return boolean
	 */
	boolean hasWriteAuth(QuestionType questionType);
}
