package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
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
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_TYPE.*, "
				+ "IFNULL((select GROUP_CONCAT(user.NAME SEPARATOR ',') from sys_user user where user.state!=0 and EXISTS ( SELECT 1 FROM EXM_QUESTION_TYPE qt "
				+ "WHERE QUESTION_TYPE.ID = qt.ID and qt.WRITE_USER_IDS like CONCAT('%,',user.ID,',%'))),'') as 'WRITE_USER_NAMES',"
				+ "IFNULL((select GROUP_CONCAT(user.NAME SEPARATOR ',') from sys_user user where user.state!=0 and EXISTS ( SELECT 1 FROM EXM_QUESTION_TYPE qt "
				+ "WHERE QUESTION_TYPE.ID = qt.ID and qt.READ_USER_IDS like CONCAT('%,',user.ID,',%'))),'') as 'READ_USER_NAMES', "
				+ "IFNULL((select user.NAME from sys_user user where user.state!=0 and user.ID = QUESTION_TYPE.CREATE_USER_ID),'') as 'CREATE_USER_NAME'"
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "QUESTION_TYPE.NAME LIKE ?", String.format("%%%s%%", pageIn.get("name")))
				.addWhere("QUESTION_TYPE.STATE = 1")
				.addWhere("QUESTION_TYPE.ID != 1")
				.addOrder("QUESTION_TYPE.UPDATE_TIME", Order.DESC);
		
		if (pageIn.get("curUserId", Integer.class) != null) {
			User user = userDao.getEntity(pageIn.get("curUserId", Integer.class));
			StringBuilder partSql = new StringBuilder();
			List<Object> params = new ArrayList<>();
			partSql.append("(");
			partSql.append("QUESTION_TYPE.READ_USER_IDS LIKE ? ");
			params.add("%," + user.getId() + ",%");
			
			partSql.append("OR QUESTION_TYPE.WRITE_USER_IDS LIKE ? ");
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
	public PageOut authUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME "
				+ "FROM SYS_USER USER ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(pageIn.get("idWrite", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_QUESTION_TYPE Z WHERE Z.ID = ? AND Z.WRITE_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("idWrite", Integer.class))
				.addWhere(pageIn.get("idRead",  Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_QUESTION_TYPE Z WHERE Z.ID = ? AND Z.READ_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("idRead", Integer.class))
				.addWhere("USER.STATE = 1")
				.addOrder("USER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}