package com.wcpdoc.exam.exam.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.exam.entity.ExamTypeAuth;

/**
 * 考试分类权限数据访问层接口
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
public interface ExamTypeAuthDao extends BaseDao<ExamTypeAuth>{

	/**
	 * 获取考试分类权限列表
	 * 
	 * v1.0 zhanghc 2018年6月3日下午2:02:16
	 * @return List<ExamTypeAuth>
	 */
	List<ExamTypeAuth> getList();
}
