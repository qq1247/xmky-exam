package com.wcpdoc.base.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 机构数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgDao extends RBaseDao<Org> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Org>().setAlias("ORG")//
						.select("ORG.ID", "ORG.NAME", "ORG.PARENT_ID", "PARENT_ORG.NAME AS PARENT_NAME", "ORG.NO")//
						.leftJoin("SYS_ORG PARENT_ORG ON ORG.PARENT_ID = PARENT_ORG.ID")
						.eq(pageIn.hasParm("parentId"), "ORG.PARENT_ID", pageIn.getParm("parentId"))//
						.like(pageIn.hasParm("name"), "ORG.NAME", pageIn.getParm("name"))//
						.eq("ORG.STATE", 1)//
						.orderByAsc("ORG.NO", "ORG.ID"));// no值一样，导致分页错误，添加id排序

		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取机构列表
	 * 
	 * v1.0 chenyun 2020-6-4 08:59:46
	 * 
	 * @return List<Dict>
	 */
	default List<Org> getList() {
		return selectList(new LambdaQueryWrapper<Org>().eq(Org::getState, 1));
	}

	/**
	 * 获取机构列表
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	default List<Org> getList(Integer parentId) {
		return selectList(new LambdaQueryWrapper<Org>().eq(Org::getState, 1).eq(Org::getParentId, parentId));
	}

	/**
	 * 名称是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	default boolean existName(String name, Integer excludeId) {
		if (excludeId == null) {
			return selectCount(new LambdaQueryWrapper<Org>().eq(Org::getState, 1).eq(Org::getName, name)) > 0;
		}

		return selectCount(new LambdaQueryWrapper<Org>().eq(Org::getState, 1).eq(Org::getName, name).ne(Org::getId,
				excludeId)) > 0;
	}

	/**
	 * 编码是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	default boolean existCode(String code, Integer excludeId) {
		if (excludeId == null) {
			return selectCount(new LambdaQueryWrapper<Org>().eq(Org::getState, 1).eq(Org::getCode, code)
					.isNotNull(Org::getCode)) > 0;
		}

		return selectCount(new LambdaQueryWrapper<Org>().eq(Org::getState, 1).eq(Org::getCode, code)
				.isNotNull(Org::getCode).notIn(Org::getId, excludeId)) > 0;
	}

}
