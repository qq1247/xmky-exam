package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyExamDetail;

/**
 * 我的考试详细数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDetailDao extends BaseDao<MyExamDetail>{
	
	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param myExamId
	 * @return List<MyExamDetail>
	 */
	List<MyExamDetail> getList(Integer myExamId);

	/**
	 * 删除我的考试详细
	 * 
	 * v1.0 zhanghc 2020年10月12日下午4:04:24
	 * @param myExamId void
	 */
	void delByMyExamId(Integer myExamId);

}
