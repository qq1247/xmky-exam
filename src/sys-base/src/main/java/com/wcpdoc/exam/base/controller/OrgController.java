package com.wcpdoc.exam.base.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
			return "base/org/orgList";
		} catch (Exception e) {
			log.error("到达组织机构列表页面错误：", e);
			return "base/org/orgList";
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
	public PageResult treeList() {
		try {
			return PageResultEx.ok().data(orgService.getTreeList());
		} catch (Exception e) {
			log.error("组织机构树错误：", e);
			return PageResult.err();
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
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(orgService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("组织机构列表错误：", e);
			return PageResult.err();
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
			return "base/org/orgEdit";
		} catch (Exception e) {
			log.error("到达添加组织机构页面错误", e);
			return "base/org/orgEdit";
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
	public PageResult doAdd(Org org, String phone) {
		try {
			orgService.addAndUpdate(org);
			Map<String, Object> data = new HashMap<String, Object>();
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("完成添加组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加组织机构错误：", e);
			return PageResult.err();
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
			if (org.getParentId() != null) {
				model.addAttribute("parentOrg", orgService.getEntity(org.getParentId()));
			}
			return "base/org/orgEdit";
		} catch (Exception e) {
			log.error("到达修改组织机构页面错误", e);
			return "base/org/orgEdit";
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
			// 校验数据有效性
			if (!ValidateUtil.isValid(org.getName())) {
				throw new MyException("参数错误：name");
			}
			if (orgService.existName(org)) {
				throw new MyException("名称已存在！");
			}
			if (orgService.existCode(org)) {
				throw new MyException("编码已存在！");
			}

			// 修改组织机构
			Org entity = orgService.getEntity(org.getId());
			entity.setName(org.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User) getCurUser()).getId());
			entity.setNo(org.getNo());
			entity.setCode(org.getCode());
			orgService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改组织机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成删除组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			orgService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除组织机构错误：", e);
			return PageResult.err();
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
			return "base/org/orgMove";
		} catch (Exception e) {
			log.error("到达移动组织机构页面错误", e);
			return "base/org/orgMove";
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
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成移动组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成移动组织机构错误：", e);
			return PageResult.err();
		}
	}
}
