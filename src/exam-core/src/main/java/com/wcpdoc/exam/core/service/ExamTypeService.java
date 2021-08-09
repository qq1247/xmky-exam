package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
/**
 * 考试分类服务层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeService extends BaseService<ExamType> {
	
	/**
	 * 添加考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param examType
	 * void
	 */
	void addAndUpdate(ExamType examType);
	
	/**
	 * 修改考试分类
	 * 
	 * v1.0 cY 2021年6月11日下午2:44:38
	 * @param examType void
	 */
	void editAndUpdate(ExamType examType);
	
	/**
	 * 删除考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 名称是否重复
	 * 
	 * v1.0 zhanghc 2020-09-05 10:12:16
	 * @param examType
	 * @return boolean
	 */
	boolean existName(ExamType examType);

	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:19:02
	 * @return List<ExamType>
	 */
	List<ExamType> getList();

	/**
	 * 完成授权
	 * 
	 * v1.0 zhanghc 2020年9月8日上午10:06:53
	 * @param id
	 * @param readUserIds
	 * @param writeUserIds
	 * void
	 */
	void doAuth(Integer id, String readUserIds, String writeUserIds);
	
	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut authUserListpage(PageIn pageIn);
}
