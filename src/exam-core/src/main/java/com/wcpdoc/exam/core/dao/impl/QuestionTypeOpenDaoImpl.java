package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.dao.QuestionTypeOpenDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;

/**
 * 试题分类开放数据访问层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Repository
public class QuestionTypeOpenDaoImpl extends RBaseDaoImpl<QuestionTypeOpen> implements QuestionTypeOpenDao {
    @Resource
    private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_TYPE_OPEN.ID AS ID, QUESTION_TYPE_OPEN.START_TIME, QUESTION_TYPE_OPEN.END_TIME, "
				+ "QUESTION_TYPE_OPEN.QUESTION_TYPE_ID, QUESTION_TYPE_OPEN.COMMENT_STATE, QUESTION_TYPE_OPEN.STATE, QUESTION_TYPE_OPEN.UPDATE_USER_ID, "
				+ "IFNULL((select user.NAME from sys_user user where user.state!=0 and user.ID = QUESTION_TYPE_OPEN.UPDATE_USER_ID),'') as 'UPDATE_USER_NAME', "
				+ "IFNULL((select GROUP_CONCAT(user.NAME SEPARATOR ',') from sys_user user where user.state!=0 and EXISTS ( SELECT 1 FROM EXM_QUESTION_TYPE_OPEN et "
				+ "WHERE QUESTION_TYPE_OPEN.ID = et.ID and et.USER_IDS like CONCAT('%,',user.ID,',%'))),'') as 'USER_NAMES', "
				+ "IFNULL((select GROUP_CONCAT(org.NAME SEPARATOR ',') from sys_org org where org.state!=0 and EXISTS ( SELECT 1 FROM EXM_QUESTION_TYPE_OPEN et "
				+ "WHERE QUESTION_TYPE_OPEN.ID = et.ID and et.ORG_IDS like CONCAT('%,',org.ID,',%'))),'') as 'ORG_NAMES', "
				+ "QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, QUESTION_TYPE_OPEN.USER_IDS, QUESTION_TYPE_OPEN.ORG_IDS "
				+ "FROM EXM_QUESTION_TYPE_OPEN QUESTION_TYPE_OPEN "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION_TYPE_OPEN.QUESTION_TYPE_ID = QUESTION_TYPE.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(pageIn.get("questionTypeId", Integer.class) != null , "QUESTION_TYPE_OPEN.QUESTION_TYPE_ID = ?", pageIn.get("questionTypeId", Integer.class))
			   .addWhere(pageIn.get("state") != null, "QUESTION_TYPE_OPEN.STATE = ?", pageIn.get("state", Integer.class))
			   .addOrder("QUESTION_TYPE_OPEN.UPDATE_TIME", Order.DESC);

	      if (pageIn.get("curUserId", Integer.class) != null ) {
		      User user = userDao.getEntity(pageIn.get("curUserId", Integer.class));
		      StringBuilder partSql = new StringBuilder();
		      List<Object> params = new ArrayList<>();
		      partSql.append("(");
		      partSql.append("QUESTION_TYPE_OPEN.USER_IDS LIKE ? ");
		      params.add("%," + user.getId() + ",%");
		      
		      partSql.append("OR QUESTION_TYPE_OPEN.ORG_IDS LIKE ? ");
		      params.add("%," + user.getOrgId() + ",%");
		      partSql.append(")");
		      sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
	      }
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
				HibernateUtil.formatDate(pageOut.getList(), "startTime", DateUtil.FORMAT_DATE_TIME);
				HibernateUtil.formatDate(pageOut.getList(), "endTime", DateUtil.FORMAT_DATE_TIME);
				HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), "STATE_OPEN", "state");
		return pageOut;
	}

	@Override
	public List<QuestionTypeOpen> getList(Date startTime, Date endTime, Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE_OPEN WHERE  QUESTION_TYPE_ID = ? AND STATE = 1 AND (START_TIME <= ? AND END_TIME >= ? OR START_TIME <= ? AND END_TIME >= ? ) ";
		return getList(sql, new Object[] { questionTypeId, startTime, startTime, endTime, endTime });
	}
}