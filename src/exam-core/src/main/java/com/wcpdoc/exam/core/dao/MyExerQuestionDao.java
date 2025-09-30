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
	 * 我的练习试题列表
	 * 
	 * v1.0 zhanghc 2025年6月15日下午2:57:26
	 * 
	 * @param myExerId
	 * @return List<MyExer>
	 */
	default List<MyExerQuestion> getList(Integer myExerId) {
		return selectList(new LambdaQueryWrapper<MyExerQuestion>().eq(MyExerQuestion::getMyExerId, myExerId));
	}

	/**
	 * 我的练习试题获取
	 * 
	 * v1.0 zhanghc 2025年6月16日下午9:11:49
	 * 
	 * @param myExerId
	 * @param questionId
	 * @return MyExerQuestion
	 */
	default MyExerQuestion getMyExerQuestion(Integer myExerId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyExerQuestion>().eq(MyExerQuestion::getMyExerId, myExerId)
				.eq(MyExerQuestion::getQuestionId, questionId));
	}
}
