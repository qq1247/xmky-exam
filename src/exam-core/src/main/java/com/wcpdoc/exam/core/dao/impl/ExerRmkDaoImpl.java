package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerRmkDao;
import com.wcpdoc.exam.core.entity.ExerRmk;

/**
 * 练习评论数据访问层实现
 * 
 * v1.0 chenyun 2021年8月31日上午10:01:50
 */
@Repository
public class ExerRmkDaoImpl extends RBaseDaoImpl<ExerRmk> implements ExerRmkDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXER_RMK.ID, EXER_RMK.CONTENT, EXER_RMK.LIKE_NUM, EXER_RMK.LIKE_USER_IDS, "// LIKE_USER_IDS用于当前用户已点赞时，选中点赞图标
				+ "EXER_RMK.UPDATE_USER_ID, USER.NAME AS UPDATE_USER_NAME, "// UPDATE_USER_ID, UPDATE_USER_NAME，页面显示是谁，没有就是匿名
				+ "EXER_RMK.UPDATE_TIME "
				+ "FROM EXM_EXER_RMK EXER_RMK "
				+ "LEFT JOIN SYS_USER USER ON USER.ID = EXER_RMK.UPDATE_USER_ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("questionId")), "EXER_RMK.QUESTION_ID = :QUESTION_ID", pageIn.get("questionId"))
				.addWhere("EXER_RMK.STATE = 1")// 只看有效数据
				.addOrder("EXER_RMK.LIKE_NUM", Order.DESC)// 按评论数倒序
				.addOrder("EXER_RMK.UPDATE_TIME", Order.DESC);// 在按更新时间倒序
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "updateTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}
}