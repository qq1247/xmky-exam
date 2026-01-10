package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionBank;

/**
 * 题库服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionBankService extends BaseService<QuestionBank> {

	/**
	 * 题库删除
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @param id void
	 */
	void del(Integer id);

	/**
	 * 题库清空
	 * 
	 * v1.0 zhanghc 2022年9月15日上午9:28:44
	 * 
	 * @param id
	 * @return PageResult
	 */
	void clear(Integer id);

	/**
	 * 题库资料添加
	 * 
	 * v1.0 zhanghc 2026年1月9日上午10:23:24
	 * 
	 * @param id
	 * @param docFileId
	 * @return PageResult
	 */
	void docAdd(Integer id, Integer docFileId);

	/**
	 * 题库资料删除
	 * 
	 * v1.0 zhanghc 2026年1月9日上午11:21:31
	 * 
	 * @param id
	 * @param fileId void
	 */
	void docDel(Integer id, Integer fileId);

	/**
	 * 获取题库列表
	 * 
	 * v1.0 zhanghc 2026年1月10日上午9:46:18
	 * 
	 * @return List<QuestionBank>
	 */
	List<QuestionBank> getList();

}
