package com.wcpdoc.exam.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
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
						.select("EXER.ID", "EXER.NAME", "EXER.START_TIME", "EXER.END_TIME", "EXER.RMK_STATE",
								"EXER.USER_IDS", "QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME")//
						.leftJoin("EXM_QUESTION_TYPE QUESTION_TYPE ON EXER.QUESTION_TYPE_ID = QUESTION_TYPE.ID")//
						.eq(pageIn.hasParm("questionTypeId"), "EXER.QUESTION_TYPE_ID", pageIn.getParm("questionTypeId"))//
						.like(pageIn.hasParm("name"), "EXER.NAME", pageIn.getParm("name"))//
						.ge(pageIn.hasParm("todo"), "EXER.END_TIME", DateUtil.formatDateTime(new Date()))// 查找我的未完成的练习列表
						.eq(pageIn.hasParm("curUserId"), "EXER.CREATE_USER_ID", pageIn.getParm("curUserId"))// 子管理员登录，各看各的
						.like(pageIn.hasParm("examUserId"), "EXER.USER_IDS",
								String.format("%%,%s,%%", pageIn.getParm("examUserId")))//
						.eq("EXER.STATE", 1)//
						.and(pageIn.hasParm("startTime") && pageIn.hasParm("endTime"),
								i -> i.ge("EXER.START_TIME", pageIn.getParm("startTime"))
										.le("EXER.START_TIME", pageIn.getParm("endTime"))//
										.or()//
										.ge("EXER.END_TIME", pageIn.getParm("startTime"))
										.le("EXER.END_TIME", pageIn.getParm("endTime"))//
										.or()//
										.le("EXER.START_TIME", pageIn.getParm("startTime"))
										.ge("EXER.END_TIME", pageIn.getParm("endTime")))//
						.orderByDesc("EXER.START_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 练习列表
	 * 
	 * v1.0 chenyun 2021年9月17日上午11:19:21
	 * 
	 * @param questionTypeId
	 * @return List<Exer>
	 */
	default List<Exer> getList(Integer questionTypeId) {
		return selectList(
				new LambdaQueryWrapper<Exer>().eq(Exer::getQuestionTypeId, questionTypeId).eq(Exer::getState, 1));
	}
}
