package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;

/**
 * 试题分类数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Repository
public class QuestionTypeDaoImpl extends RBaseDaoImpl<QuestionType> implements QuestionTypeDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_TYPE.ID, QUESTION_TYPE.NAME, QUESTION_TYPE.PARENT_ID, "
				+ "QUESTION_TYPE.PARENT_SUB, PARENT_QUESTION_TYPE.NAME AS PARENT_NAME, "
				+ "QUESTION_TYPE.NO, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_USER _A WHERE QUESTION_TYPE.USER_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS USER_NAMES, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_ORG _A WHERE QUESTION_TYPE.ORG_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS ORG_NAMES, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_POST _A WHERE QUESTION_TYPE.POST_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS POST_NAMES "
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE "
				+ "LEFT JOIN EXM_QUESTION_TYPE PARENT_QUESTION_TYPE ON QUESTION_TYPE.PARENT_ID = PARENT_QUESTION_TYPE.ID ";
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
		String sql = "SELECT ID, NAME, PARENT_ID, PARENT_SUB FROM EXM_QUESTION_TYPE WHERE STATE = 1 ORDER BY NO ASC";
		return getMapList(sql);
	}

	@Override
	public List<QuestionType> getList() {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE WHERE STATE = 1";
		return getList(sql);
	}

	@Override
	public List<QuestionType> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE WHERE STATE = 1 AND PARENT_ID = ?";
		return getList(sql, new Object[] { parentId });
	}
	
	@Override
	public boolean existName(String name, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM EXM_QUESTION_TYPE WHERE NAME = ? AND STATE = 1";
			return getCount(sql, new Object[] { name }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM EXM_QUESTION_TYPE WHERE NAME = ? AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { name, excludeId }) > 0;
	}

	@Override
	public PageOut getAuthUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "(USER.NAME LIKE ? OR ORG.NAME LIKE ?)", "%" + pageIn.getTwo() + "%", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_QUESTION_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.getTen())
				.addWhere("USER.STATE = 1")
				.addOrder("USER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getAuthPostListpage(PageIn pageIn) {
		String sql = "SELECT POST.ID, POST.NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_POST POST "
				+ "INNER JOIN SYS_ORG ORG ON POST.ORG_ID = ORG.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "(POST.NAME LIKE ? OR ORG.NAME LIKE ?)", "%" + pageIn.getTwo() + "%", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_QUESTION_TYPE Z WHERE Z.ID = ? AND Z.POST_IDS LIKE CONCAT('%,', POST.ID, ',%'))", pageIn.getTen())
				.addWhere("POST.STATE = 1")
				.addOrder("POST.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getAuthOrgListpage(PageIn pageIn) {
		String sql = "SELECT ORG.ID, ORG.NAME, PARENT_ORG.NAME AS PARENT_ORG_NAME " 
				+ "FROM SYS_ORG ORG "
				+ "LEFT JOIN SYS_ORG PARENT_ORG ON ORG.PARENT_ID = PARENT_ORG.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "ORG.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_QUESTION_TYPE Z WHERE Z.ID = ? AND Z.ORG_IDS LIKE CONCAT('%,', ORG.ID, ',%'))", pageIn.getTen())
				.addWhere("ORG.STATE = 1")
				.addOrder("ORG.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
	
}