package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
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
	 * v1.0 zhanghc 2023年1月30日下午6:32:13
	 * @param examId 
	 * @return List<MyMark>
	 */
	List<MyMark> getList(Integer examId);

	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2022年11月9日下午3:02:49
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);
}
