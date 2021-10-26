package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.entity.PaperType;

/**
 * 试卷分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Repository
public class PaperTypeDaoImpl extends RBaseDaoImpl<PaperType> implements PaperTypeDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER_TYPE.*, "
				+ "IFNULL((select GROUP_CONCAT(user.NAME SEPARATOR ',') from sys_user user where user.state!=0 and EXISTS ( SELECT 1 FROM EXM_PAPER_TYPE pt "
				+ "WHERE PAPER_TYPE.ID = pt.ID and	pt.WRITE_USER_IDS like CONCAT('%,',user.ID,',%'))),'') as 'WRITE_USER_NAMES',"
				+ "IFNULL((select GROUP_CONCAT(user.NAME SEPARATOR ',') from sys_user user where user.state!=0 and EXISTS ( SELECT 1 FROM EXM_PAPER_TYPE pt "
				+ "WHERE PAPER_TYPE.ID = pt.ID and	pt.READ_USER_IDS like CONCAT('%,',user.ID,',%'))),'') as 'READ_USER_NAMES', "
				+ "IFNULL((select user.NAME from sys_user user where user.state!=0 and user.ID = PAPER_TYPE.CREATE_USER_ID),'') as 'CREATE_USER_NAME'"
				+ "FROM EXM_PAPER_TYPE PAPER_TYPE ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "PAPER_TYPE.NAME LIKE ?", String.format("%%%s%%", pageIn.get("name")))
				.addWhere("PAPER_TYPE.STATE = 1")
				.addWhere("PAPER_TYPE.ID != 1")
				.addOrder("PAPER_TYPE.UPDATE_TIME", Order.DESC);
		
		if (pageIn.get("curUserId", Integer.class) != null) {
			User user = userDao.getEntity(pageIn.get("curUserId", Integer.class));
			StringBuilder partSql = new StringBuilder();
			List<Object> params = new ArrayList<>();
			partSql.append("(");
			partSql.append("PAPER_TYPE.READ_USER_IDS LIKE ? ");
			params.add("%," + user.getId() + ",%");
			
			partSql.append("OR PAPER_TYPE.WRITE_USER_IDS LIKE ? ");
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
		String sql = "SELECT ID, NAME, PARENT_ID, PARENT_SUB FROM EXM_PAPER_TYPE WHERE STATE = 1 ORDER BY NO ASC";
		return getMapList(sql);
	}

	@Override
	public List<PaperType> getList() {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE STATE = 1";
		return getList(sql);
	}

	@Override
	public List<PaperType> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE STATE = 1 AND PARENT_ID = ?";
		return getList(sql, new Object[] { parentId });
	}
	
	@Override
	public boolean existName(String name, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM EXM_PAPER_TYPE WHERE NAME = ? AND STATE = 1";
			return getCount(sql, new Object[] { name }) > 0;
		}

		String sql = "SELECT COUNT(*) AS NUM FROM EXM_PAPER_TYPE WHERE NAME = ? AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { name, excludeId }) > 0;
	}

	@Override
	public PageOut getAuthUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("Two")), "(USER.NAME LIKE ? OR ORG.NAME LIKE ?)", "%" + pageIn.get("Two") + "%", "%" + pageIn.get("Two") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("Ten")), "EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("Ten"))
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
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("Two")), "(POST.NAME LIKE ? OR ORG.NAME LIKE ?)", "%" + pageIn.get("Two") + "%", "%" + pageIn.get("Two") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("Ten")), "EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.POST_IDS LIKE CONCAT('%,', POST.ID, ',%'))", pageIn.get("Ten"))
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
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("Two")), "ORG.NAME LIKE ?", "%" + pageIn.get("Two") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("Ten")), "EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.ORG_IDS LIKE CONCAT('%,', ORG.ID, ',%'))", pageIn.get("Ten"))
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
		sqlUtil.addWhere(pageIn.get("idw", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.WRITE_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("idw", Integer.class))
				.addWhere(pageIn.get("idr", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.READ_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("idr", Integer.class))
				.addWhere("USER.STATE = 1")
				.addOrder("USER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}