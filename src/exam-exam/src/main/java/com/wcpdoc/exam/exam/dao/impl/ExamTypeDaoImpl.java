package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.ExamTypeDao;
import com.wcpdoc.exam.exam.entity.ExamType;

/**
 * 考试分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Repository
public class ExamTypeDaoImpl extends BaseDaoImpl<ExamType> implements ExamTypeDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_TYPE.ID, EXAM_TYPE.NAME, EXAM_TYPE.PARENT_ID, "
				+ "EXAM_TYPE.PARENT_SUB, PARENT_EXAM_TYPE.NAME AS PARENT_NAME, "
				+ "EXAM_TYPE.NO, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_USER _A WHERE EXAM_TYPE.USER_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS USER_NAMES, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_ORG _A WHERE EXAM_TYPE.ORG_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS ORG_NAMES, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_POST _A WHERE EXAM_TYPE.POST_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS POST_NAMES "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE "
				+ "LEFT JOIN EXM_EXAM_TYPE PARENT_EXAM_TYPE ON EXAM_TYPE.PARENT_ID = PARENT_EXAM_TYPE.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "EXAM_TYPE.PARENT_ID = ?", pageIn.getOne())//如果查询的是根目录，则查询所有。否则查询选中机构的子机构
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "EXAM_TYPE.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("EXAM_TYPE.STATE = ?", 1)
				.addOrder("EXAM_TYPE.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT EXAM_TYPE.ID, EXAM_TYPE.NAME, EXAM_TYPE.PARENT_ID, EXAM_TYPE.PARENT_SUB "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE "
				+ "WHERE EXAM_TYPE.STATE = 1";
		return getList(sql);
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		ExamType sourceExamType = getEntity(sourceId);
		ExamType targetExamType = getEntity(targetId);
		sourceExamType.setParentId(targetId);
		flush();

		String sql = "UPDATE EXM_EXAM_TYPE EXAM_TYPE" 
				+ "	SET EXAM_TYPE.PARENT_SUB = REPLACE(EXAM_TYPE.PARENT_SUB, ?, ?)"
				+ "	WHERE EXAM_TYPE.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { 
					sourceExamType.getParentSub(), 
					targetExamType.getParentSub() + sourceExamType.getId() + "_",
					sourceExamType.getParentSub() + "%" };
		update(sql, params);
	}
	
	@Override
	public List<ExamType> getAllSubExamTypeList(Integer id) {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE PARENT_SUB LIKE ? AND STATE = 1";
		return getList(sql, new Object[]{"%\\_"+id+"\\_%"}, ExamType.class);
	}
	
	@Override
	public ExamType getExamTypeByName(String name) {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{name}, ExamType.class);
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
						"EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", 
						pageIn.getTen())
//				.addWhere("USER.STATE = ?", 1)//已添加过的可以显示
				.addWhere("USER.ID != ?", 1)//排除管理员
//				.addWhere("ORG.STATE = ?", 1)//已添加过的可以显示
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getAuthUserAddList(PageIn pageIn) {
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
						"NOT EXISTS (SELECT 1 FROM EXM_EXAM_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", 
						pageIn.getTen())
				.addWhere("USER.STATE = ?", 1)//当前正常的用户
				.addWhere("USER.ID != ?", 1)//排除管理员
				.addWhere("ORG.STATE = ?", 1)//当前正常的机构
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<ExamType> getList() {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE STATE = 1";
		return getList(sql, ExamType.class);
	}

	@Override
	public List<ExamType> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_EXAM_TYPE WHERE STATE = 1 AND PARENT_ID = ?";
		return getList(sql, new Object[]{parentId}, ExamType.class);
	}
}