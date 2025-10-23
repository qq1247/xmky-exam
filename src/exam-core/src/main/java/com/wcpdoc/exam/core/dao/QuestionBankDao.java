package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionBank;

/**
 * 题库数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionBankDao extends RBaseDao<QuestionBank> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<QuestionBank>().setAlias("QUESTION_BANK")//
						.leftJoin("SYS_USER CREATE_USER ON QUESTION_BANK.CREATE_USER_ID = CREATE_USER.ID")
						.select("QUESTION_BANK.ID", "QUESTION_BANK.NAME", "CREATE_USER.NAME AS CREATE_USER_NAME",
								"QUESTION_BANK.QUESTION_NUM", "QUESTION_BANK.OBJECTIVE_NUM",
								"QUESTION_BANK.SUBJECTIVE_NUM", "QUESTION_BANK.SINGLE_NUM",
								"QUESTION_BANK.MULTIPLE_NUM", "QUESTION_BANK.JUDGE_NUM",
								"QUESTION_BANK.FILL_BLANK_OBJ_NUM", "QUESTION_BANK.FILL_BLANK_SUB_NUM",
								"QUESTION_BANK.QA_OBJ_NUM", "QUESTION_BANK.QA_SUB_NUM", "QUESTION_BANK.UPDATE_TIME")//
						.like(pageIn.hasParm("name"), "QUESTION_BANK.NAME", pageIn.getParm("name"))//
						.eq(pageIn.hasParm("id"), "QUESTION_BANK.ID", pageIn.getParm("id"))//
						.eq(pageIn.hasParm("curUserId"), "QUESTION_BANK.CREATE_USER_ID", pageIn.getParm("curUserId"))//
						.eq("QUESTION_BANK.STATE", 1)//
						.orderByDesc("QUESTION_BANK.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
