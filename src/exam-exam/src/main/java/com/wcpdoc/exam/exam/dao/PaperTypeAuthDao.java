package com.wcpdoc.exam.exam.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.exam.entity.PaperTypeAuth;

/**
 * 试卷分类权限数据访问层接口
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
public interface PaperTypeAuthDao extends BaseDao<PaperTypeAuth>{

	/**
	 * 获取试卷分类权限列表
	 * 
	 * v1.0 zhanghc 2018年6月3日下午2:02:16
	 * @return List<QuestionTypeAuth>
	 */
	List<PaperTypeAuth> getList();
}
