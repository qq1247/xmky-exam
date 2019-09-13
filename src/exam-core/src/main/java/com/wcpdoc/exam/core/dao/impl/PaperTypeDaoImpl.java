package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试卷分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Repository
public class PaperTypeDaoImpl extends BaseDaoImpl<PaperType> implements PaperTypeDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER_TYPE.ID, PAPER_TYPE.NAME, PAPER_TYPE.PARENT_ID, "
				+ "PAPER_TYPE.PARENT_SUB, PARENT_PAPER_TYPE.NAME AS PARENT_NAME, "
				+ "PAPER_TYPE.NO, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_USER _A WHERE PAPER_TYPE.USER_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS USER_NAMES, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_ORG _A WHERE PAPER_TYPE.ORG_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS ORG_NAMES, "
				+ "(SELECT GROUP_CONCAT(_A.`NAME`) FROM SYS_POST _A WHERE PAPER_TYPE.POST_IDS LIKE (CONCAT(\"%,\", _A.ID, \",%\"))) AS POST_NAMES "
				+ "FROM EXM_PAPER_TYPE PAPER_TYPE "
				+ "LEFT JOIN EXM_PAPER_TYPE PARENT_PAPER_TYPE ON PAPER_TYPE.PARENT_ID = PARENT_PAPER_TYPE.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "PAPER_TYPE.PARENT_ID = ?", pageIn.getOne())//如果查询的是根目录，则查询所有。否则查询选中机构的子机构
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "PAPER_TYPE.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("PAPER_TYPE.STATE = ?", 1)
				.addOrder("PAPER_TYPE.NO", Order.ASC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		String sql = "SELECT PAPER_TYPE.ID, PAPER_TYPE.NAME, PAPER_TYPE.PARENT_ID, PAPER_TYPE.PARENT_SUB "
				+ "FROM EXM_PAPER_TYPE PAPER_TYPE "
				+ "WHERE PAPER_TYPE.STATE = 1";
		return getList(sql);
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		PaperType sourcePaperType = getEntity(sourceId);
		PaperType targetPaperType = getEntity(targetId);
		sourcePaperType.setParentId(targetId);
		flush();

		String sql = "UPDATE EXM_PAPER_TYPE PAPER_TYPE" //
				+ "	SET PAPER_TYPE.PARENT_SUB = REPLACE(PAPER_TYPE.PARENT_SUB, ?, ?)"
				+ "	WHERE PAPER_TYPE.PARENT_SUB LIKE ?";
		Object[] params = new Object[] { 
					sourcePaperType.getParentSub(), 
					targetPaperType.getParentSub() + sourcePaperType.getId() + "_",
					sourcePaperType.getParentSub() + "%" };
		update(sql, params);
	}
	
	@Override
	public List<PaperType> getAllSubPaperTypeList(Integer id) {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE PARENT_SUB LIKE ? AND STATE = 1";
		return getList(sql, new Object[]{"%\\_"+id+"\\_%"}, PaperType.class);
	}
	
	@Override
	public PaperType getPaperTypeByName(String name) {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{name}, PaperType.class);
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
						"EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", 
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
						"NOT EXISTS (SELECT 1 FROM EXM_PAPER_TYPE Z WHERE Z.ID = ? AND Z.USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", 
						pageIn.getTen())
				.addWhere("USER.STATE = ?", 1)//当前正常的用户
				.addWhere("USER.ID != ?", 1)//排除管理员
				.addWhere("ORG.STATE = ?", 1)//当前正常的机构
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<PaperType> getList() {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE STATE = 1";
		return getList(sql, PaperType.class);
	}

	@Override
	public List<PaperType> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE STATE = 1 AND PARENT_ID = ?";
		return getList(sql, new Object[]{parentId}, PaperType.class);
	}
}