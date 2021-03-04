package com.wcpdoc.exam.core.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;
/**
 * 试题分类服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeService extends BaseService<QuestionType> {
	
	/**
	 * 添加试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType
	 * void
	 */
	void addAndUpdate(String name, MultipartFile file);

	/**
	 * 删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 名称是否重复
	 * 
	 * v1.0 zhanghc 2020-09-05 10:12:16
	 * @param questionType
	 * @return boolean
	 */
	boolean existName(QuestionType questionType);

	/**
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:19:02
	 * @return List<QuestionType>
	 */
	List<QuestionType> getList();

	/**
	 * 完成授权
	 * 
	 * v1.0 zhanghc 2020年9月8日上午10:06:53
	 * @param id
	 * @param readUserIds
	 * @param writeUserIds
	 * void
	 */
	void doAuth(Integer id, Integer[] readUserIds, Integer[] writeUserIds, boolean rwState);

	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);

}
