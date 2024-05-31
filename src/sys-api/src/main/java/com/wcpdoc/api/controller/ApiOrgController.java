package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.OrgExService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

import lombok.extern.slf4j.Slf4j;

/**
 * 机构控制层
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@RestController
@RequestMapping("/api/org")
@Slf4j
public class ApiOrgController extends BaseController {

	@Resource
	private OrgService orgService;
	@Resource
	private OrgExService orgExService;
	@Resource
	private BaseCacheService baseCacheService;

	/**
	 * 机构列表
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(orgService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("机构列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 机构添加
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Org org) {
		try {
			orgService.addEx(org);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("机构添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("机构添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 机构修改
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Org org) {
		try {
			orgService.editEx(org);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("机构修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("机构修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 机构删除
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			orgService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("机构删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("机构删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 机构获取
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Org org = baseCacheService.getOrg(id);
			return PageResultEx.ok()//
					.addAttr("id", org.getId())//
					.addAttr("name", org.getName()).addAttr("parentId", org.getParentId())//
					.addAttr("parentName",
							(org.getParentId() == null || org.getParentId() == 0) ? null
									: baseCacheService.getOrg(org.getParentId()).getName())//
					.addAttr("no", org.getNo());
		} catch (MyException e) {
			log.error("机构获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("机构获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 机构移动
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/move")
	public PageResult move(Integer sourceId, Integer targetId) {
		try {
			orgService.doMove(sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("机构移动错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("机构移动错误：", e);
			return PageResult.err();
		}
	}
}
