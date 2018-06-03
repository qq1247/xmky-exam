package com.wcpdoc.exam.sys.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.sys.dao.UserDao;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.entity.User;

/**
 * 用户数据访问层实现
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME, USER.LOGIN_NAME, ORG.NAME AS ORG_NAME, POST_USER.POST_NAMES "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN (SELECT POST_USER.USER_ID, GROUP_CONCAT(POST.NAME) AS POST_NAMES "
				+ "				FROM SYS_POST_USER POST_USER "
				+ "				INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "				GROUP BY POST_USER.USER_ID ) POST_USER ON USER.ID = POST_USER.USER_ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere("USER.STATE = ?", 1)
				.addWhere("ORG.STATE = ?", 1)
				.addWhere("USER.ID != ?", 1)
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public Org getOrg(Integer id) {
		String sql = "SELECT ORG.* "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE USER.ID = ?";
		return getUnique(sql, new Object[]{id}, Org.class);
	}

	@Override
	public List<Map<String, Object>> getPostUpdateTreeList(Integer orgId) {
		String sql = "SELECT POST.ID, POST.NAME "
				+ "FROM SYS_ORG ORG "
				+ "INNER JOIN SYS_ORG_POST ORG_POST ON ORG.ID = ORG_POST.ORG_ID "
				+ "INNER JOIN SYS_POST POST ON ORG_POST.POST_ID = POST.ID "
				+ "WHERE ORG.STATE = 1 AND POST.STATE = 1 AND ORG_POST.ORG_ID = ?";
		return getList(sql, new Object[]{orgId});
	}

	@Override
	public List<Org> getAllOrg(Integer[] ids) {
		StringBuilder params = new StringBuilder();
		for(int i = 0; i < ids.length; i++){
			if(i == 0){
				params.append("?");
				continue;
			}
			params.append(",?");
		}
		
		String sql = "SELECT DISTINCT ORG.* "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE ORG.STATE = 1 AND USER.STATE = 1 AND USER.ID IN (" + params + ")";
		return getList(sql, ids, Org.class);
	}

	@Override
	public List<User> getOrgUserList(Integer orgId) {
		String sql = "SELECT USER.* "
				+ "FROM SYS_USER USER "
				+ "WHERE USER.STATE = 1 AND USER.ORG_ID = ?";
		return getList(sql, new Object[]{orgId}, User.class);
	}

	@Override
	public User getUser(String loginName) {
		String sql = "SELECT * FROM SYS_USER WHERE LOGIN_NAME = ? AND STATE = 1";
		return getUnique(sql, new Object[]{loginName}, User.class);
	}

	@Override
	public List<Map<String, Object>> getAuthSum(Integer id) {
		String sql = "SELECT RESSUM.AUTH_POS, SUM(RESSUM.AUTH_CODE) AS SUM_AUTH_CODE "
				+ "FROM("
				+ "		SELECT DISTINCT RES.ID, RES.AUTH_POS, RES.AUTH_CODE "
				+ "		FROM SYS_POST_USER POST_USER "
				+ "		INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "		INNER JOIN SYS_POST_RES POST_RES ON POST.ID = POST_RES.POST_ID "
				+ "		INNER JOIN SYS_RES RES ON POST_RES.RES_ID = RES.ID "
				+ "		WHERE POST_USER.USER_ID = ? AND POST.STATE = 1 AND RES.PARENT_ID != 0 " 
				+ "	) RESSUM "
				+ "GROUP BY RESSUM.AUTH_POS";
		return getList(sql, new Object[]{id});
	}

	@Override
	public List<Post> getPostList(Integer id) {
		String sql = "SELECT POST.* "
				+ "FROM SYS_POST_USER POST_USER "
				+ "INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "WHERE POST.STATE = 1 AND POST_USER.USER_ID = ?";
		return getList(sql, new Object[]{id}, Post.class);
	}
}