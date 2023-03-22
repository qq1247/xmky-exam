package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.wcpdoc.exam.core.dao.QuestionTypeOpenDao;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;

/**
 * 题库开放数据访问层实现
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
				+ "(SELECT USER.NAME FROM SYS_USER USER WHERE USER.STATE!=0 AND USER.ID = QUESTION_TYPE_OPEN.UPDATE_USER_ID) AS 'UPDATE_USER_NAME', "
				+ "(SELECT GROUP_CONCAT(USER.NAME SEPARATOR ',') FROM SYS_USER USER WHERE USER.STATE!=0 AND EXISTS ( SELECT 1 FROM EXM_QUESTION_TYPE_OPEN ET "
				+ "WHERE QUESTION_TYPE_OPEN.ID = ET.ID AND ET.USER_IDS LIKE CONCAT('%,',USER.ID,',%'))) AS 'USER_NAMES', "
				+ "(SELECT GROUP_CONCAT(ORG.NAME SEPARATOR ',') FROM SYS_ORG ORG WHERE ORG.STATE!=0 AND EXISTS ( SELECT 1 FROM EXM_QUESTION_TYPE_OPEN ET "
				+ "WHERE QUESTION_TYPE_OPEN.ID = ET.ID AND ET.ORG_IDS LIKE CONCAT('%,',ORG.ID,',%'))) AS 'ORG_NAMES', "
				+ "QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, QUESTION_TYPE_OPEN.USER_IDS, QUESTION_TYPE_OPEN.ORG_IDS "
				+ "FROM EXM_QUESTION_TYPE_OPEN QUESTION_TYPE_OPEN "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION_TYPE_OPEN.QUESTION_TYPE_ID = QUESTION_TYPE.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(pageIn.get("questionTypeId", Integer.class) != null , "QUESTION_TYPE_OPEN.QUESTION_TYPE_ID = :QUESTION_TYPE_ID", pageIn.get("questionTypeId", Integer.class))
			   .addWhere(pageIn.get("state") != null, "QUESTION_TYPE_OPEN.STATE = :QUESTION_TYPE_OPEN_STATE", pageIn.get("state", Integer.class))
			   .addWhere(pageIn.get("curUserId", Integer.class)!= null , "QUESTION_TYPE_OPEN.UPDATE_USER_ID = :UPDATE_USER_ID", pageIn.get("curUserId", Integer.class))
			   .addWhere(ValidateUtil.isValid(pageIn.get("startTime")) && ValidateUtil.isValid(pageIn.get("endTime")), 
						"((QUESTION_TYPE_OPEN.START_TIME <= :START_TIME1 AND :END_TIME1 <= QUESTION_TYPE_OPEN.END_TIME) "
						+ "OR (QUESTION_TYPE_OPEN.START_TIME <= :START_TIME2 AND :END_TIME2 <= QUESTION_TYPE_OPEN.END_TIME) "
						+ "OR (QUESTION_TYPE_OPEN.START_TIME >= :START_TIME3 AND QUESTION_TYPE_OPEN.END_TIME <= :END_TIME3))", 
						pageIn.get("startTime"), pageIn.get("startTime"),
						pageIn.get("endTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime")
						)
			   .addOrder("QUESTION_TYPE_OPEN.UPDATE_TIME", Order.DESC);

	      if (pageIn.get("readUserIds", Integer.class) != null) {
		      User user = userDao.getEntity(pageIn.get("readUserIds", Integer.class));
		      StringBuilder partSql = new StringBuilder();
		      List<Object> params = new ArrayList<>();
		      partSql.append("(");
		      partSql.append("QUESTION_TYPE_OPEN.USER_IDS LIKE :USER_IDS");
		      params.add("%," + user.getId() + ",%");
		      
		      partSql.append(" OR QUESTION_TYPE_OPEN.ORG_IDS LIKE :ORG_IDS");
		      params.add("%," + user.getOrgId() + ",%");
		      
		      partSql.append(" OR (QUESTION_TYPE_OPEN.USER_IDS IS NULL AND QUESTION_TYPE_OPEN.ORG_IDS IS NULL) ");
		      
		      partSql.append(")");
		      sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
	      }
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
				HibernateUtil.formatDate(pageOut.getList(), "startTime", DateUtil.FORMAT_DATE_TIME);
				HibernateUtil.formatDate(pageOut.getList(), "endTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<QuestionTypeOpen> getList(Date startTime, Date endTime, Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE_OPEN "
				+ "WHERE  QUESTION_TYPE_ID = :QUESTION_TYPE_ID "
				+ "AND STATE = 1 "
				+ "AND (((START_TIME <= :START_TIME1 AND :END_TIME1 <= END_TIME) "
						+ "OR (START_TIME <= :START_TIME2 AND :END_TIME2 <= END_TIME) "
						+ "OR (START_TIME >= :START_TIME3 AND END_TIME <= :END_TIME3))) ";
		return getList(sql, new Object[] { questionTypeId, startTime, startTime, endTime, endTime, startTime, endTime });
	}
}