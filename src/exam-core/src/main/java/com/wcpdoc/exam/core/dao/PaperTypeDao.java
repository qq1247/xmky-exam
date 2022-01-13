package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PaperType;

/**
 * 试卷分类数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperTypeDao extends BaseDao<PaperType> {

	/**
	 * 获取试卷分类列表
	 * 
	 * v1.0 zhanghc 2018年6月6日下午10:03:33
	 * @param parentId
	 * @return List<PaperType>
	 */
	List<PaperType> getList(Integer parentId);
}
