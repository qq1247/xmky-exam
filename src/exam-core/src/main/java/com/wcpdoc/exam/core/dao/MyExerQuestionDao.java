package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExerQuestion;

/**
 * 我的练习试题数据访问层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyExerQuestionDao extends RBaseDao<MyExerQuestion> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyExerQuestion>().setAlias("MY_EXER_QUESTION")//
						.leftJoin("EXM_QUESTION QUESTION ON MY_EXER_QUESTION.QUESTION_ID = QUESTION.ID")
						.select("QUESTION.ID AS QUESTION_ID", "QUESTION.TITLE AS QUESTION_TITLE",
								"MY_EXER_QUESTION.WRONG_ANSWER_NUM")//
						.eq("MY_EXER_QUESTION.EXER_ID", pageIn.getParm("exerId"))//
						.eq("MY_EXER_QUESTION.USER_ID", pageIn.getParm("userId"))//
						.gt("MY_EXER_QUESTION.WRONG_ANSWER_NUM", pageIn.getParm("wrongAnswerNum"))
						.orderByDesc("MY_EXER_QUESTION.WRONG_ANSWER_NUM"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月15日下午2:57:26
	 * 
	 * @param exerId
	 * @param userId
	 * @return List<MyExer>
	 */
	default List<MyExerQuestion> getList(Integer exerId, Integer userId) {
		return selectList(new LambdaQueryWrapper<MyExerQuestion>().eq(MyExerQuestion::getExerId, exerId)
				.eq(MyExerQuestion::getUserId, userId));
	}

	/**
	 * 我的练习获取
	 * 
	 * v1.0 zhanghc 2025年6月16日下午9:11:49
	 * 
	 * @param exerId
	 * @param userId
	 * @param questionId
	 * @return MyExerQuestion
	 */
	default MyExerQuestion getMyExerQuestion(Integer exerId, Integer userId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyExerQuestion>().eq(MyExerQuestion::getExerId, exerId)
				.eq(MyExerQuestion::getUserId, userId).eq(MyExerQuestion::getQuestionId, questionId));
	}

	/**
	 * 答错数量列表
	 * 
	 * v1.0 zhanghc 2025年9月15日上午9:40:30
	 * 
	 * @param exerId
	 * @return List<Map<String, Object>>
	 */
	default List<Map<String, Object>> getWrongAnswerNumList(Integer exerId) {
		return selectMaps(new MPJQueryWrapper<MyExerQuestion>()
				.select("USER_ID", "COUNT(*) AS TOTAL_QUESTION_NUM",
						"SUM(CASE WHEN WRONG_ANSWER_NUM > 0 THEN 1 ELSE 0 END) AS WRONG_ANSWER_NUM", 
						"SUM(CASE WHEN ANSWER_TIME IS NOT NULL THEN 1 ELSE 0 END) AS ANSWERED_NUM")
				.eq("EXER_ID", exerId).groupBy("USER_ID"));
	}

}
