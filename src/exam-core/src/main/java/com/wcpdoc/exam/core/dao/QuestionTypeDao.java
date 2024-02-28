package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 题库数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeDao extends RBaseDao<QuestionType> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<QuestionType>().setAlias("QUESTION_TYPE")//
						.leftJoin("SYS_USER CREATE_USER ON QUESTION_TYPE.CREATE_USER_ID = CREATE_USER.ID")
						.select("QUESTION_TYPE.ID", "QUESTION_TYPE.NAME", "CREATE_USER.NAME AS CREATE_USER_NAME",
								"(SELECT COUNT(*) FROM EXM_QUESTION Z WHERE Z.QUESTION_TYPE_ID = QUESTION_TYPE.ID AND Z.STATE = 1) AS QUESTION_NUM")//
						.like(pageIn.hasParm("name"), "QUESTION_TYPE.NAME", pageIn.getParm("name"))//
						.eq(pageIn.hasParm("id"), "QUESTION_TYPE.ID", pageIn.getParm("id"))//
						.eq(pageIn.hasParm("curUserId"), "QUESTION_TYPE.CREATE_USER_ID", pageIn.getParm("curUserId"))//
						.orderByDesc("QUESTION_TYPE.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
