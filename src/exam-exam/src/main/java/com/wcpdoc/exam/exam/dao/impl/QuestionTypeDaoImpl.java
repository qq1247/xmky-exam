package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.exam.dao.QuestionTypeDao;
import com.wcpdoc.exam.exam.entity.QuestionType;

/**
 * 试题分类数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Repository
public class QuestionTypeDaoImpl extends BaseDaoImpl<QuestionType> implements QuestionTypeDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_TYPE.ID, QUESTION_TYPE.NAME, QUESTION_TYPE.PARENT_ID, "
				+ "QUESTION_TYPE.PARENT_SUB, PARENT_QUESTION_TYPE.NAME AS PARENT_NAME, "
				+ "QUESTION_TYPE.NO "
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE "
				+ "LEFT JOIN EXM_QUESTION_TYPE PARENT_QUESTION_TYPE ON QUESTION_TYPE.PARENT_ID = PARENT_QUESTION_TYPE.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "QUESTION_TYPE.PARENT_ID = ?", pageIn.getOne())//如果查询的是根目录，则查询所有。否则查询选中机构的子机构
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "QUESTION_TYPE.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("QUESTION_TYPE.STATE = ?", 1)
				.addOrder("QUESTION_TYPE.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT QUESTION_TYPE.ID, QUESTION_TYPE.NAME, QUESTION_TYPE.PARENT_ID, QUESTION_TYPE.PARENT_SUB "
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE "
				+ "WHERE QUESTION_TYPE.STATE = 1";
		return getList(sql);
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		QuestionType sourceQuestionType = getEntity(sourceId);
		QuestionType targetQuestionType = getEntity(targetId);
		sourceQuestionType.setParentId(targetId);
		flush();

		String sql = "UPDATE EXM_QUESTION_TYPE QUESTION_TYPE" //
				+ "	SET QUESTION_TYPE.PARENT_SUB = REPLACE(QUESTION_TYPE.PARENT_SUB, ?, ?)"
				+ "	WHERE QUESTION_TYPE.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { 
					sourceQuestionType.getParentSub(), 
					targetQuestionType.getParentSub() + sourceQuestionType.getId() + "_",
					sourceQuestionType.getParentSub() + "%" };
		update(sql, params);
	}
	
	@Override
	public List<QuestionType> getAllSubQuestionTypeList(Integer id) {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE WHERE PARENT_SUB LIKE ? AND STATE = 1";
		return getList(sql, new Object[]{"%\\_"+id+"\\_%"}, QuestionType.class);
	}
	
	@Override
	public QuestionType getQuestionTypeByName(String name) {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{name}, QuestionType.class);
	}

	@Override
	public PageOut getAuthUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS USER_NAME, USER.LOGIN_NAME, ORG.NAME AS ORG_NAME, POST_USER.POST_NAMES "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN (SELECT POST_USER.USER_ID, GROUP_CONCAT(POST.NAME) AS POST_NAMES "
				+ "				FROM SYS_POST_USER POST_USER "
				+ "				INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "				GROUP BY POST_USER.USER_ID ) POST_USER ON USER.ID = POST_USER.USER_ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), 
						"EXISTS (SELECT 1 FROM EXM_QUESTION_AUTH Z WHERE Z.QUESTION_TYPE_ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%')", 
						pageIn.getTen())
//				.addWhere("USER.STATE = ?", 1)//已添加过的可以显示
				.addWhere("USER.ID != ?", 1)
//				.addWhere("ORG.STATE = ?", 1)//已添加过的可以显示
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}