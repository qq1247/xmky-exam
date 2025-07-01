package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Exer;

/**
 * 练习数据访问层接口
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
public interface ExerDao extends RBaseDao<Exer> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Exer>().setAlias("EXER")//
						.select("EXER.ID", "EXER.NAME", "EXER.RMK_STATE", "EXER.USER_IDS", "EXER.ORG_IDS", "EXER.STATE",
								"EXER.UPDATE_TIME", "CREATE_USER.NAME AS CREATE_USER_NAME",
								"QUESTION_BANK.ID AS QUESTION_BANK_ID", // QUESTION_BANK_ID：创建练习选择题库的时候，已存在的不可选
								"QUESTION_BANK.NAME AS QUESTION_BANK_NAME")//
						.leftJoin("EXM_QUESTION_BANK QUESTION_BANK ON EXER.QUESTION_BANK_ID = QUESTION_BANK.ID")//
						.leftJoin("SYS_USER CREATE_USER ON QUESTION_BANK.CREATE_USER_ID = CREATE_USER.ID")//
						.like(pageIn.hasParm("name"), "EXER.NAME", pageIn.getParm("name"))//
						.eq(pageIn.hasParm("questionBankId"), "EXER.QUESTION_BANK_ID", pageIn.getParm("questionBankId"))//
						.eq(pageIn.hasParm("subAdminUserId"), "QUESTION_BANK.CREATE_USER_ID",
								pageIn.getParm("subAdminUserId"))// 子管理员登录，各看各的创建的
						.and(pageIn.hasParm("examUserId") && pageIn.hasParm("examOrgId"),
								c -> c.like("EXER.USER_IDS", String.format(",%s,", pageIn.getParm("examUserId"))).or()
										.like("EXER.ORG_IDS", String.format(",%s,", pageIn.getParm("examOrgId"))))// 考试用户看（管理或子管理）分配给自己的
						.eq(pageIn.hasParm("state"), "EXER.STATE", pageIn.getParm("state"))//
						.ne("EXER.STATE", 0)//
						.orderByDesc("EXER.UPDATE_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 练习获取
	 * 
	 * v1.0 zhanghc 2025年6月7日下午2:05:56
	 * 
	 * @param questionBankId
	 * @return Exer
	 */
	default Exer getExer(Integer questionBankId) {
		return selectOne(new LambdaQueryWrapper<Exer>().eq(Exer::getQuestionBankId, questionBankId));
	}
}
