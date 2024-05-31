package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Exam;

/**
 * 考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 * 
 * @param <T>
 */
public interface ExamDao extends RBaseDao<Exam> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Exam>().setAlias("EXAM")//
						.select("EXAM.ID", "EXAM.NAME", "EXAM.START_TIME", "EXAM.END_TIME", "EXAM.MARK_START_TIME",
								"EXAM.MARK_END_TIME", "EXAM.PASS_SCORE", "EXAM.TOTAL_SCORE", "EXAM.STATE",
								"EXAM.MARK_STATE", "EXAM.SCORE_STATE", "EXAM.RANK_STATE", "EXAM.GEN_TYPE",
								"EXAM.MARK_TYPE", "EXAM.SXES", "EXAM.ANON_STATE",
								"(SELECT COUNT(*) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = EXAM.ID) AS USER_NUM", //
								"(SELECT COUNT(*) FROM EXM_MY_MARK A WHERE A.EXAM_ID = EXAM.ID) AS MARK_USER_NUM") //
						.like(pageIn.hasParm("name"), "EXAM.NAME", pageIn.getParm("name"))//
						.in(!pageIn.hasParm("state"), "EXAM.STATE", 1, 2)//
						.in(pageIn.hasParm("state") && "0".equals(pageIn.getParm("state")), "EXAM.STATE", 1, 2)// 如果传入0，会导致查询到已删除的
						.eq(pageIn.hasParm("state") && !"0".equals(pageIn.getParm("state")), "EXAM.STATE",
								pageIn.getParm("state"))
						.eq(pageIn.hasParm("subAdminUserId"), "EXAM.CREATE_USER_ID", pageIn.getParm("subAdminUserId"))
						.exists(pageIn.hasParm("markUserId"),
								"SELECT 1 FROM EXM_MY_MARK Z WHERE Z.MARK_USER_ID = {0} AND Z.EXAM_ID = EXAM.ID",
								pageIn.getParm("markUserId")) // 阅卷用户看（管理或子管理）分配的
						.orderByDesc("EXAM.UPDATE_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	default PageOut getMarkListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Exam>().setAlias("EXAM")//
						.select("EXAM.ID AS EXAM_ID", "EXAM.NAME AS EXAM_NAME", "EXAM.START_TIME AS EXAM_START_TIME",
								"EXAM.END_TIME AS EXAM_END_TIME", "EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME",
								"EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME", "EXAM.PASS_SCORE AS EXAM_PASS_SCORE",
								"EXAM.TOTAL_SCORE AS EXAM_TOTAL_SCORE", "EXAM.STATE AS EXAM_STATE",
								"EXAM.MARK_STATE AS EXAM_MARK_STATE", "EXAM.SCORE_STATE AS EXAM_SCORE_STATE",
								"EXAM.RANK_STATE AS EXAM_RANK_STATE", "EXAM.GEN_TYPE AS EXAM_GEN_TYPE",
								"EXAM.MARK_TYPE AS EXAM_MARK_TYPE", "EXAM.SXES AS EXAM_SXES",
								"EXAM.ANON_STATE AS EXAM_ANON_STATE",
								"(SELECT COUNT(*) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = EXAM.ID) AS EXAM_USER_NUM",
								"(SELECT COUNT(*) FROM EXM_MY_MARK A WHERE A.EXAM_ID = EXAM.ID) AS EXAM_MARK_USER_NUM") //
						.like(pageIn.hasParm("examName"), "EXAM.NAME", pageIn.getParm("examName"))//
						.and(pageIn.hasParm("startTime") && pageIn.hasParm("endTime"),
								i -> i.ge("EXAM.START_TIME", pageIn.getParm("startTime"))
										.le("EXAM.START_TIME", pageIn.getParm("endTime"))//
										.or()//
										.ge("EXAM.END_TIME", pageIn.getParm("startTime"))
										.le("EXAM.END_TIME", pageIn.getParm("endTime"))//
										.or()//
										.le("EXAM.START_TIME", pageIn.getParm("startTime"))
										.ge("EXAM.END_TIME", pageIn.getParm("endTime")))//
						.ne(pageIn.hasParm("todo"), "EXAM.MARK_STATE", 3)// 查找我的未完成的考试列表
						.eq(pageIn.hasParm("subAdminUserId"), "EXAM.CREATE_USER_ID", pageIn.hasParm("subAdminUserId"))// 子管理员看自己
						.exists(pageIn.hasParm("markUserId"),
								"SELECT 1 FROM EXM_MY_MARK Z WHERE Z.MARK_USER_ID = {0} AND Z.EXAM_ID = EXAM.ID",
								pageIn.getParm("markUserId")) // 阅卷用户看（管理或子管理）分配的
						.eq("EXAM.STATE", 1)// 已发布（不含冻结）
						.orderByDesc("EXAM.START_TIME"));// 按考试开始时间倒序排列
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 考试中列表
	 * 
	 * v1.0 zhanghc 2024年5月12日下午8:50:27
	 * 
	 * @return List<Exam>
	 */
	default List<Exam> getExamingList() {
		return selectList(new LambdaQueryWrapper<Exam>().eq(Exam::getState, 1).in(Exam::getMarkState, 1, 2));
	}

}
