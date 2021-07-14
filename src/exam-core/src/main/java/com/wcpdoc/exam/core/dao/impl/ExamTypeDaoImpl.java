package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.dao.ExamTypeDao;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 考试分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Repository
public class ExamTypeDaoImpl extends RBaseDaoImpl<ExamType> implements ExamTypeDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_TYPE.* "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "EXAM_TYPE.NAME LIKE ?", String.format("%%%s%%", pageIn.get("name")))
				.addWhere("EXAM_TYPE.STATE = 1")
				.addWhere("EXAM_TYPE.ID != 1")
				.addOrder("EXAM_TYPE.CREATE_TIME", Order.DESC);
		
		if (pageIn.get("curUserId", Integer.class) != null) {
			User user = userDao.getEntity(pageIn.get("curUserId", Integer.class));
			StringBuilder partSql = new StringBuilder();
			List<Object> params = new ArrayList<>();
			partSql.append("(");
			partSql.append("EXAM_TYPE.READ_USER_IDS LIKE ? ");
			params.add("%," + user.getId() + ",%");
			
			partSql.append("OR EXAM_TYPE.WRITE_USER_IDS LIKE ? ");
			params.add("%," + user.getId() + ",%");
			
			partSql.append(")");
			sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
		}
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"updateTime", DateUtil.FORMAT_DATE_TIME, 
				"createTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT ID, NAME, PARENT_ID, PARENT_SUB FROM EXM_EXAM_TYPE WHERE STATE = 1 ORDER BY NO ASC";
		return getMapList(sql);
	}

	@Override
	public List<ExamType> getList() {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE STATE = 1";
		return getList(sql);
	}

	@Override
	public List<ExamType> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE STATE = 1 AND PARENT_ID = ?";
		return getList(sql, new Object[] { parentId });
	}
	
	@Override
	public boolean existName(String name, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM EXM_EXAM_TYPE WHERE NAME = ? AND STATE = 1";
			return getCount(sql, new Object[] { name }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM EXM_EXAM_TYPE WHERE NAME = ? AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { name, excludeId }) > 0;
	}

	@Override
	public PageOut getAuthUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("Two").toString()), "(USER.NAME LIKE ? OR ORG.NAME LIKE ?)", "%" + pageIn.get("Two") + "%", "%" + pageIn.get("Two").toString() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("Ten").toString()), "EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("Ten").toString())
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
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("Two").toString()), "(POST.NAME LIKE ? OR ORG.NAME LIKE ?)", "%" + pageIn.get("Two") + "%", "%" + pageIn.get("Two").toString() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("Ten").toString()), "EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.POST_IDS LIKE CONCAT('%,', POST.ID, ',%'))", pageIn.get("Ten").toString())
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
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("Two").toString()), "ORG.NAME LIKE ?", "%" + pageIn.get("Two").toString() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("Ten").toString()), "EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.ORG_IDS LIKE CONCAT('%,', ORG.ID, ',%'))", pageIn.get("Ten").toString())
				.addWhere("ORG.STATE = 1")
				.addOrder("ORG.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
	
	@Override
	public PageOut authUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME "
				+ "FROM SYS_USER USER ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(pageIn.get("idw", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.WRITE_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("idw", Integer.class))
				.addWhere(pageIn.get("idr", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.READ_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("idr", Integer.class))
				.addWhere("USER.STATE = 1")
				.addOrder("USER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}