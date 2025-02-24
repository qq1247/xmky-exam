package com.wcpdoc.base.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.StringUtil;

/**
 * 用户数据访问层接口
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
public interface UserDao extends RBaseDao<User> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<User>().setAlias("USER")//
						.leftJoin("SYS_ORG ORG ON USER.ORG_ID = ORG.ID")
						.select("USER.ID", "USER.NAME", "USER.LOGIN_NAME", "USER.ORG_ID", "ORG.NAME AS ORG_NAME",
								"USER.STATE")//
						.and(pageIn.hasParm("name"),
								i -> i.like("USER.NAME", pageIn.getParm("name")).or().like("ORG.NAME",
										pageIn.getParm("name")))//
						.eq(pageIn.hasParm("state"), "USER.STATE", pageIn.getParm("state"))//
						.eq(pageIn.hasParm("type"), "USER.TYPE", pageIn.getParm("type"))//
						.in(pageIn.hasParm("ids"), "USER.ID", StringUtil.toIntList(pageIn.getParm("ids", String.class)))//
						.in(pageIn.hasParm("examUserIds"), "USER.ID", pageIn.getParm("examUserIds", List.class))//
						.eq(pageIn.hasParm("parentId"), "USER.PARENT_ID", pageIn.getParm("parentId"))//
						.ne("USER.STATE", 0)//
						.orderByDesc("USER.ID"));// bug：导入用户时，按时间倒序，分页错误
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取用户
	 * 
	 * v1.0 zhanghc 2016年7月13日下午5:12:23
	 * 
	 * @param loginName
	 * @return User
	 */
	default User getUser(String loginName) {
		return selectOne(new LambdaQueryWrapper<User>().ne(User::getState, 0).eq(User::getLoginName, loginName));
	}

	/**
	 * 登录账号是否存在
	 * 
	 * v1.0 zhanghc 2020年6月15日上午10:51:04
	 * 
	 * @param loginName
	 * @param excludeId
	 * @return boolean
	 */
	default boolean existLoginName(String loginName, Integer excludeId) {
		if (excludeId == null) {
			return selectCount(
					new LambdaQueryWrapper<User>().ne(User::getState, 0).eq(User::getLoginName, loginName)) > 0;
		}

		return selectCount(new LambdaQueryWrapper<User>().ne(User::getState, 0).eq(User::getLoginName, loginName)
				.ne(User::getId, excludeId)) > 0;
	}

	/**
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午6:29:50
	 * 
	 * @param orgId
	 * @return List<User>
	 */
	default List<User> getList(Integer orgId) {
		return selectList(new LambdaQueryWrapper<User>().ne(User::getState, 0).eq(User::getOrgId, orgId));
	}

	/**
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2022年5月10日下午4:55:22
	 * 
	 * @return List<User>
	 */
	default List<User> getList() {
		return selectList(new LambdaQueryWrapper<User>().ne(User::getState, 0));
	}

	/**
	 * 获取阅卷用户列表
	 * 
	 * v1.0 zhanghc 2023年9月22日下午3:58:47
	 * 
	 * @param parentId
	 * @return List<User>
	 */
	default List<User> getMarkUserlist(Integer parentId) {
		return selectList(new LambdaQueryWrapper<User>().ne(User::getState, 0));
	}
}
