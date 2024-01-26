package com.wcpdoc.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.cache.UserCache;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.service.OrgExService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.service.FileService;

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
	private FileService fileService;

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
	 * 添加机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Org org) {
		try {
			UserCache.tryWriteLock("orgAdd", 5000);
			orgService.addEx(org);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("添加机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加机构错误：", e);
			return PageResult.err();
		} finally {
			UserCache.releaseWriteLock("orgAdd");
		}
	}

	/**
	 * 修改机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Org org) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(org.getName())) {
				throw new MyException("参数错误：name");
			}
			if (orgService.existName(org)) {
				throw new MyException("名称已存在");
			}
			if (orgService.existCode(org)) {
				throw new MyException("编码已存在");
			}

			// 修改机构
			Org entity = orgService.getById(org.getId());
			entity.setName(org.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setNo(org.getNo());
			entity.setCode(org.getCode());
			orgService.updateById(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 删除机构
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
			log.error("删除机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 获取机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Org org = orgService.getById(id);
			return PageResultEx.ok().addAttr("id", org.getId()).addAttr("name", org.getName())
					.addAttr("parentId", org.getParentId())
					.addAttr("parentName", (org.getParentId() == null || org.getParentId() == 0) ? null
							: orgService.getById(org.getParentId()).getName())
					.addAttr("no", org.getNo());
		} catch (MyException e) {
			log.error("获取机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 移动机构
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
			log.error("移动机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("移动机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 导入机构
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/import")
	public PageResult xlsImport(Integer fileId) {
		try {
			orgExService.xlsImport(fileId);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("机构列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("机构列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 导出模板
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/template")
	public void template() {
		try {
			fileService.exportTemplate("机构.xlsx");
		} catch (Exception e) {
			log.error("导出模板错误：", e);
		}
	}
}
