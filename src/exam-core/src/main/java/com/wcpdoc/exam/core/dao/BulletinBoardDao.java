package com.wcpdoc.exam.core.dao;

import com.wcpdoc.exam.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.BulletinBoard;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 公告栏数据访问层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface BulletinBoardDao extends RBaseDao<BulletinBoard> {
	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);
	
	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getOrgListpage(PageIn pageIn);
}
