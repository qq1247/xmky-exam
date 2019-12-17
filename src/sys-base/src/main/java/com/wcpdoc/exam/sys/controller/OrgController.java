package com.wcpdoc.exam.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.User;
import com.wcpdoc.exam.sys.service.OrgService;

/**
 * 组织机构控制层
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Controller
@RequestMapping("/org")
public class OrgController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(OrgController.class);

	@Resource
	private OrgService orgService;

	/**
	 * 到达组织机构列表页面
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "sys/org/orgList";
		} catch (Exception e) {
			log.error("到达组织机构列表页面错误：", e);
			return "sys/org/orgList";
		}
	}

	/**
	 * 组织机构树
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public List<Map<String, Object>> treeList() {
		try {
			return orgService.getTreeList();
		} catch (Exception e) {
			log.error("组织机构树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 组织机构列表
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return orgService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("组织机构列表错误：", e);
			return new PageOut();
		}
	}

	/**
	 * 到达添加组织机构页面
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "sys/org/orgEdit";
		} catch (Exception e) {
			log.error("到达添加组织机构页面错误", e);
			return "sys/org/orgEdit";
		}
	}

	/**
	 * 完成添加组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Org org) {
		try {
			org.setUpdateUserId(getCurUser().getId());
			org.setUpdateTime(new Date());
			org.setState(1);
			orgService.addAndUpdate(org);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加组织机构错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}

	/**
	 * 到达修改组织机构页面
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Org org = orgService.getEntity(id);
			model.addAttribute("org", org);
			Org pOrg = orgService.getEntity(org.getParentId());
			if (pOrg != null) {
				model.addAttribute("pOrg", orgService.getEntity(org.getParentId()));
			}
			return "sys/org/orgEdit";
		} catch (Exception e) {
			log.error("到达修改组织机构页面错误", e);
			return "sys/org/orgEdit";
		}
	}

	/**
	 * 完成修改组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Org org) {
		try {
			Org entity = orgService.getEntity(org.getId());
			entity.setName(org.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User) getCurUser()).getId());
			entity.setNo(org.getNo());
			orgService.editAndUpdate(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改组织机构错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}

	/**
	 * 完成删除组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			orgService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除组织机构错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}

	/**
	 * 到达移动组织机构页面
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @return String
	 */
	@RequestMapping("/toMove")
	public String toMove() {
		try {
			return "sys/org/orgMove";
		} catch (Exception e) {
			log.error("到达移动组织机构页面错误", e);
			return "sys/org/orgMove";
		}
	}

	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/moveOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> moveOrgTreeList() {
		try {
			return orgService.getTreeList();
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 完成移动组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/doMove")
	@ResponseBody
	public PageResult doMove(Integer sourceId, Integer targetId) {
		try {
			orgService.doMove(sourceId, targetId);
			return new PageResult(true, "移动成功");
		} catch (Exception e) {
			log.error("完成移动组织机构错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
}
