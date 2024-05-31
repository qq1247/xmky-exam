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
import com.wcpdoc.exam.core.entity.MyExam;

/**
 * 我的考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDao extends RBaseDao<MyExam> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyExam>().setAlias("MY_EXAM")//
						.leftJoin("EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID")
						.leftJoin("SYS_USER USER ON MY_EXAM.USER_ID = USER.ID")
						.leftJoin("SYS_USER MARK_USER ON MY_EXAM.MARK_USER_ID = MARK_USER.ID")
						.select("EXAM.ID AS EXAM_ID", "EXAM.NAME AS EXAM_NAME", "EXAM.START_TIME AS EXAM_START_TIME",
								"EXAM.END_TIME AS EXAM_END_TIME", "EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME",
								"EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME", "EXAM.MARK_STATE AS EXAM_MARK_STATE",
								"EXAM.SCORE_STATE AS EXAM_SCORE_STATE", "EXAM.RANK_STATE AS EXAM_RANK_STATE", // 考试相关字段（用于控制是否显示成绩和排名，控制层用）
								"EXAM.PASS_SCORE AS EXAM_PASS_SCORE", "EXAM.TOTAL_SCORE AS EXAM_TOTAL_SCORE",
								"USER.ID AS USER_ID", "USER.NAME AS USER_NAME", "MY_EXAM.EXAM_START_TIME",
								"MY_EXAM.EXAM_END_TIME", "MY_EXAM.TOTAL_SCORE", "MY_EXAM.STATE", "MY_EXAM.MARK_STATE",
								"MY_EXAM.ANSWER_STATE", "MY_EXAM.NO",
								"(SELECT COUNT(1) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = MY_EXAM.EXAM_ID) AS USER_NUM")// 用户数量（排名使用）
						.like(pageIn.hasParm("examName"), "EXAM.NAME", pageIn.getParm("examName"))//
						.eq(pageIn.hasParm("curUserId"), "MY_EXAM.USER_ID", pageIn.getParm("curUserId"))//
						.and(pageIn.hasParm("startTime") && pageIn.hasParm("endTime"),
								i -> i.ge("EXAM.START_TIME", pageIn.getParm("startTime"))
										.le("EXAM.START_TIME", pageIn.getParm("endTime"))//
										.or()//
										.ge("EXAM.END_TIME", pageIn.getParm("startTime"))
										.le("EXAM.END_TIME", pageIn.getParm("endTime"))//
										.or()//
										.le("EXAM.START_TIME", pageIn.getParm("startTime"))
										.ge("EXAM.END_TIME", pageIn.getParm("endTime")))//
						.and(pageIn.hasParm("todo"),
								i -> i.eq("MY_EXAM.STATE", 1).ne("MY_EXAM.MARK_STATE", 3).or().eq("MY_EXAM.STATE", 2))
						.eq("EXAM.STATE", 1)// 已发布（不含冻结）
						.orderByDesc("EXAM.START_TIME")); // 按考试开始时间倒序排列
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:00:50
	 * 
	 * @param examId
	 * @return List<MyExam>
	 */
	default List<MyExam> getList(Integer examId) {
		return selectList(new LambdaQueryWrapper<MyExam>().eq(MyExam::getExamId, examId));
	}

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2020年9月30日上午11:00:50
	 * 
	 * @param examId
	 * @return List<MyExam>
	 */
	default List<MyExam> getList2(Integer userId) {
		return selectList(new LambdaQueryWrapper<MyExam>().eq(MyExam::getUserId, userId));
	}

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 chenyun 2021年7月30日下午3:49:53
	 * 
	 * @param examId
	 * @param userId
	 * @return MyExam
	 */
	default MyExam getMyExam(Integer examId, Integer userId) {
		return selectOne(new LambdaQueryWrapper<MyExam>().eq(MyExam::getExamId, examId).eq(MyExam::getUserId, userId));
	}

	/**
	 * 我的考试清理
	 * 
	 * v1.0 zhanghc 2023年3月22日下午5:38:23
	 * 
	 * @param id void
	 */
	default void clear(Integer examId) {
		delete(new LambdaQueryWrapper<MyExam>().eq(MyExam::getExamId, examId));
	}

	/**
	 * 未阅卷列表
	 * 
	 * v1.0 zhanghc 2024年5月12日下午8:50:45
	 * 
	 * @return List<MyExam>
	 */
	default List<MyExam> getUnMarkList() {
		return selectList(
				new LambdaQueryWrapper<MyExam>().eq(MyExam::getState, 2).le(MyExam::getExamEndTime, new Date()));
	}

}
