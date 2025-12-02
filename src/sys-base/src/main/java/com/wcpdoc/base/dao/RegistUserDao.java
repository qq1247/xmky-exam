package com.wcpdoc.base.dao;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.base.entity.RegistUser;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 注册用户数据访问层接口
 * 
 * v1.0 zhanghc 2025年12月1日下午4:16:56
 */
public interface RegistUserDao extends RBaseDao<RegistUser> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<RegistUser>().setAlias("REGIST_USER")//
						.select("REGIST_USER.ID", "REGIST_USER.LOGIN_NAME", "REGIST_USER.NAME", "REGIST_USER.ORG_ID",
								"ORG.NAME AS ORG_NAME", "REGIST_USER.REGIST_TIME", "REGIST_USER.STATE",
								"REGIST_USER.REMARK", "REGIST_USER.UPDATE_TIME")//
						.leftJoin("SYS_ORG ORG ON REGIST_USER.ORG_ID = ORG.ID")
						.eq(pageIn.hasParm("state"), "REGIST_USER.STATE", pageIn.getParm("state"))//
						.and(pageIn.hasParm("name"),
								i -> i.like("REGIST_USER.NAME", pageIn.getParm("name")).or()
										.like("REGIST_USER.LOGIN_NAME", pageIn.getParm("name")))//
						.orderByDesc("REGIST_USER.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	default boolean existLoginName(String loginName) {
		return selectCount(new LambdaQueryWrapper<RegistUser>()//
				.in(RegistUser::getState, 1, 3)// 状态（1：通过；2：拒绝；3：待审核）
				.eq(RegistUser::getLoginName, loginName)//
		) > 0;
	}
}
