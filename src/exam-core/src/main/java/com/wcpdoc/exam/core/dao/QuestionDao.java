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
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionDao extends RBaseDao<Question> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Question>().setAlias("QUESTION")//
						.leftJoin("EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID")
						.leftJoin("SYS_USER UPDATE_USER ON QUESTION.UPDATE_USER_ID = UPDATE_USER.ID")
						.select("QUESTION.ID", "QUESTION.TYPE", "QUESTION.TITLE", "QUESTION.MARK_TYPE",
								"QUESTION.STATE", "QUESTION.ANALYSIS", "QUESTION.QUESTION_TYPE_ID",
								"QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME", "QUESTION.SCORE", "QUESTION.MARK_OPTIONS",
								"UPDATE_USER.NAME AS UPDATE_USER_NAME")//
						.eq(pageIn.hasParm("questionTypeId"), "QUESTION.QUESTION_TYPE_ID",
								pageIn.getParm("questionTypeId"))//
						.like(pageIn.hasParm("questionTypeName"), "QUESTION_TYPE.NAME",
								pageIn.getParm("questionTypeName"))//
						.eq(pageIn.hasParm("id"), "QUESTION.ID", pageIn.getParm("id"))//
						.like(pageIn.hasParm("title"), "QUESTION.TITLE", pageIn.getParm("title"))//
						.eq(pageIn.hasParm("type"), "QUESTION.TYPE", pageIn.getParm("type"))//
						.eq(pageIn.hasParm("score"), "QUESTION.SCORE", pageIn.getParm("score"))//
						.eq(pageIn.hasParm("markType"), "QUESTION.MARK_TYPE", pageIn.getParm("markType"))//
						.notIn(pageIn.hasParm("exIds"), "QUESTION.ID",
								StringUtil.toIntList(pageIn.getParm("exIds", String.class)))//
						.eq(!pageIn.hasParm("state"), "QUESTION.STATE", 1)// 默认查询发布和草稿状态
						.and(pageIn.hasParm("state") && "0".equals(pageIn.getParm("state")),
								i -> i.eq("QUESTION.STATE", 0).gt("QUESTION.UPDATE_TIME",
										DateUtil.getNextDay(new Date(), -7)))// 查询已删除并且最近7天的试题（回收站使用）
						.eq(pageIn.hasParm("state") && !"0".equals(pageIn.getParm("state")), "QUESTION.STATE",
								pageIn.getParm("state"))//
						.eq(pageIn.hasParm("curUserId"), "QUESTION.CREATE_USER_ID", pageIn.getParm("curUserId"))//
						.orderByDesc("QUESTION.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 试题列表
	 * 
	 * v1.0 zhanghc 2024年1月18日上午10:26:45
	 * 
	 * @param ids
	 * @return List<Question>
	 */
	default List<Question> getList(List<Integer> ids) {
		return selectList(new LambdaQueryWrapper<Question>()//
				.in(Question::getId, ids));
	}

	/**
	 * 试题列表
	 * 
	 * v1.0 zhanghc 2021年12月28日下午2:56:17
	 * 
	 * @param questionTypeId
	 * @return List<Integer>
	 */
	default List<Integer> getIds(Integer questionTypeId) {
		return selectObjs(new LambdaQueryWrapper<Question>()//
				.select(Question::getId)//
				.eq(Question::getState, 1)//
				.eq(Question::getQuestionTypeId, questionTypeId));
	}

	/**
	 * 试题列表
	 * 
	 * v1.0 zhanghc 2021年12月28日下午2:56:17
	 * 
	 * @param questionTypeId
	 * @return List<Integer>
	 */
	default List<Question> getList(Integer questionTypeId) {
		return selectList(new LambdaQueryWrapper<Question>()//
				.eq(Question::getState, 1)//
				.eq(Question::getQuestionTypeId, questionTypeId));
	}

	/**
	 * 试题列表
	 * 
	 * v1.0 chenyun 2022年4月22日上午11:22:25
	 * 
	 * @return List<Question>
	 */
	default List<Question> getListByDel() {
		throw new MyException("暂未实现");
	}
}
