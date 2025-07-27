package com.wcpdoc.exam.core.dao;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExamHis;

/**
 * 我的考试历史数据访问层接口
 * 
 * v1.0 zhanghc 2025年7月12日下午1:53:07
 */
public interface MyExamHisDao extends RBaseDao<MyExamHis> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}
}
