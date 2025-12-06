package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyComment;

/**
 * 我的评论数据访问层接口
 * 
 * v1.0 chenyun 2021年8月31日上午9:59:36
 */
public interface MyCommentDao extends RBaseDao<MyComment> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyComment>().setAlias("MY_COMMENT")//
						.select("MY_COMMENT.ID", "MY_COMMENT.CONTENT", "MY_COMMENT.LIKE_NUM",
								"MY_COMMENT.LIKE_USER_IDS", // LIKE_USER_IDS用于当前用户已点赞时，选中点赞图标
								"MY_COMMENT.UPDATE_USER_ID", "MY_COMMENT.UPDATE_TIME")
						.eq(pageIn.hasParm("questionId"), "EXER_RMK.QUESTION_ID", pageIn.getParm("questionId"))//
						.eq("EXER_RMK.STATE", 1)// 只看有效数据
						.orderByDesc("EXER_RMK.LIKE_NUM", "EXER_RMK.UPDATE_TIME"));// 按评论数倒序，在按更新时间倒序
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 评论列表
	 * 
	 * v1.0 zhanghc 2025年12月3日上午9:51:46
	 * 
	 * @param questionId
	 * @return List<QuestionComment>
	 */
	default List<MyComment> getList(Integer questionId) {
		return selectList(new LambdaQueryWrapper<MyComment>()//
				.eq(MyComment::getQuestionId, questionId)//
				.eq(MyComment::getState, 1)//
				.orderByDesc(MyComment::getUpdateTime));
	}
}
