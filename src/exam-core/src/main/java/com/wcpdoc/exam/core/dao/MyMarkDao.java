package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;

/**
 * 我的阅卷数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkDao extends RBaseDao<MyMark> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = SpringUtil.getBean(ExamDao.class).selectJoinMapsPage(pageIn.toPage(), //
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
								"(SELECT COUNT(*) FROM EXM_MY_MARK A WHERE A.EXAM_ID = EXAM.ID) AS EXAM_MARK_USER_NUM")//
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
						.eq(pageIn.hasParm("subAdminUserId"), "EXAM.CREATE_USER_ID", pageIn.getParm("subAdminUserId"))//
						.exists(pageIn.hasParm("markUserId"),
								"SELECT 1 FROM EXM_MY_MARK Z WHERE Z.MARK_USER_ID = {0} AND Z.EXAM_ID = EXAM.ID",
								pageIn.getParm("markUserId")) // 阅卷用户看（管理或子管理）分配的
						.eq("EXAM.STATE", 1)//
						.orderByDesc("EXAM.START_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2022年11月9日下午3:02:49
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	default PageOut getUserListpage(PageIn pageIn) {

		Page<Map<String, Object>> page = SpringUtil.getBean(MyExamDao.class).selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyExam>().setAlias("MY_EXAM")//
						.select("USER.ID AS USER_ID", "USER.NAME AS USER_NAME", "ORG.ID AS ORG_ID",
								"ORG.NAME AS ORG_NAME", "MY_EXAM.STATE AS MY_EXAM_STATE",
								"MY_EXAM.MARK_STATE AS MY_EXAM_MARK_STATE")//
						.leftJoin("EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID")
						.leftJoin("SYS_USER USER ON MY_EXAM.USER_ID = USER.ID")
						.leftJoin("SYS_ORG ORG ON USER.ORG_ID = ORG.ID")
						.eq(pageIn.hasParm("examId"), "MY_EXAM.EXAM_ID", pageIn.getParm("examId"))//
						.like(pageIn.hasParm("userName"), "USER.NAME", pageIn.getParm("userName"))//
						.eq(pageIn.hasParm("curUserId"), "MY_EXAM.MARK_USER_ID", pageIn.getParm("curUserId"))//
						.eq(pageIn.hasParm("state"), "MY_EXAM.STATE", pageIn.getParm("state"))//
						.eq("EXAM.STATE", 1));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取我的阅卷列表
	 * 
	 * v1.0 zhanghc 2023年1月30日下午6:32:13
	 * 
	 * @param examId
	 * @return List<MyMark>
	 */
	default List<MyMark> getList(Integer examId) {
		return selectList(new LambdaQueryWrapper<MyMark>().eq(MyMark::getExamId, examId));
	}

}
