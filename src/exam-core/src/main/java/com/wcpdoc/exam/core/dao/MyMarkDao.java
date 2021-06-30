package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyMark;

/**
 * 我的阅卷数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkDao extends BaseDao<MyMark>{

	/**
	 * 获取我的阅卷列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:03:04
	 * @param examId
	 * @return List<MyMark>
	 */
	List<MyMark> getList(Integer examId);
	
}
