package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExer;

/**
 * 我的练习数据访问层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyExerDao extends RBaseDao<MyExer> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyExer>().setAlias("MY_EXER")//
						.select("MY_EXER.TYPE",
								"(SELECT COUNT(*) FROM EXM_MY_EXER_QUESTION MEQ WHERE MY_EXER.EXER_ID = MEQ.EXER_ID AND MY_EXER.USER_ID = MEQ.USER_ID AND MY_EXER.TYPE = MEQ.TYPE) AS QUESTION_NUM",
								"(SELECT COUNT(*) FROM EXM_MY_EXER_QUESTION MEQ WHERE MY_EXER.EXER_ID = MEQ.EXER_ID AND MY_EXER.USER_ID = MEQ.USER_ID AND MY_EXER.TYPE = MEQ.TYPE AND MEQ.USER_SCORE >= 0) AS ANSWER_NUM",
								"(SELECT COUNT(*) FROM EXM_MY_EXER_QUESTION MEQ WHERE MY_EXER.EXER_ID = MEQ.EXER_ID AND MY_EXER.USER_ID = MEQ.USER_ID AND MY_EXER.TYPE = MEQ.TYPE AND MEQ.USER_SCORE = MEQ.SCORE) AS CORRECT_ANSWER_NUM")
						.eq(pageIn.hasParm("curUserId"), "MY_EXER.USER_ID", pageIn.getParm("curUserId"))//
						.eq(pageIn.hasParm("exerId"), "MY_EXER.EXER_ID", pageIn.getParm("exerId"))//
						.orderByDesc("MY_EXER.UPDATE_TIME"));//
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月23日下午3:23:16
	 * 
	 * @param exerId
	 * @param userId
	 * @return List<MyExer>
	 */
	default List<MyExer> getList(Integer exerId, Integer userId) {
		return selectList(new LambdaQueryWrapper<MyExer>().eq(MyExer::getExerId, exerId).eq(MyExer::getUserId, userId));
	}
}
