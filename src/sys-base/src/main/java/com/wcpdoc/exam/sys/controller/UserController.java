package com.wcpdoc.exam.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.User;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 用户控制层
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserService userService;

	/**
	 * 到达用户列表页面
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "sys/user/userList";
		} catch (Exception e) {
			log.error("到达用户列表页面 错误：", e);
			return "sys/user/userList";
		}
	}

	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/orgTreeList")
	@ResponseBody
	public List<Map<String, Object>> orgTreeList() {
		try {
			return userService.getOrgTreeList();
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 用户列表
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return userService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("用户列表 错误：", e);
			return new PageOut();
		}
	}

	/**
	 * 到达添加用户页面
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "sys/user/userEdit";
		} catch (Exception e) {
			log.error("到达添加用户页面 错误：", e);
			return "sys/user/userEdit";
		}
	}

	/**
	 * 完成添加用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(User user) {
		try {
			user.setUpdateTime(new Date());
			user.setUpdateUserId(getCurUser().getId());
			user.setState(1);
			userService.addAndUpdate(user);

			String initPwd = StringUtil.getRandomStr(8);
			userService.doPwdUpdate(user.getId(), initPwd);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
			return new PageResultEx(true, "添加成功", data);
		} catch (Exception e) {
			log.error("完成添加用户错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}

	/**
	 * 到达修改用户页面
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			User user = userService.getEntity(id);
			Org org = userService.getOrg(id);
			model.addAttribute("user", user);
			model.addAttribute("org", org);
			return "sys/user/userEdit";
		} catch (Exception e) {
			log.error("到达修改用户页面 错误：", e);
			return "sys/user/userEdit";
		}
	}

	/**
	 * 完成修改用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(User user) {
		try {
			User entity = userService.getEntity(user.getId());
			boolean changeLoginName = false;
			if (!entity.getLoginName().equals(user.getLoginName())) {
				changeLoginName = true;
			}

			entity.setName(user.getName());
			entity.setLoginName(user.getLoginName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			userService.editAndUpdate(entity);

			String initPwd = null;
			Map<String, Object> data = new HashMap<String, Object>();
			if (changeLoginName) {
				initPwd = StringUtil.getRandomStr(8);
				userService.doPwdUpdate(user.getId(), initPwd);
				data.put("initPwd", initPwd);
			}

			return new PageResultEx(true, "修改成功", data);
		} catch (Exception e) {
			log.error("完成修改用户错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}

	/**
	 * 完成删除用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			userService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除用户错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}

	/**
	 * 到达设置岗位页面
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return String
	 */
	@RequestMapping("/toPostUpdate")
	public String toPostUpdate() {
		try {
			return "sys/user/userPostUpdate";
		} catch (Exception e) {
			log.error("到达设置岗位页面错误：", e);
			return "sys/user/userPostUpdate";
		}
	}

	/**
	 * 获取岗位树
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param ids
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/postUpdatePostTreeList")
	@ResponseBody
	public List<Map<String, Object>> postUpdatePostTreeList(Integer[] ids) {
		try {
			return userService.getPostUpdatePostTreeList(ids);
		} catch (Exception e) {
			log.error("获取岗位树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 完成设置岗位
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param ids
	 * @param postIds
	 * @return PageResult
	 */
	@RequestMapping("/doPostUpdate")
	@ResponseBody
	public PageResult doPostUpdate(Integer[] ids, Integer[] postIds) {
		try {
			userService.doPostUpdate(ids, postIds);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置岗位错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}

	/**
	 * 到达设置组织机构页面
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return String
	 */
	@RequestMapping("/toOrgUpdate")
	public String toOrgUpdate() {
		try {
			return "sys/user/userOrgUpdate";
		} catch (Exception e) {
			log.error("到达设置组织机构页面错误：", e);
			return "sys/user/userOrgUpdate";
		}
	}

	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/orgUpdateOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> orgUpdateOrgTreeList() {
		try {
			return userService.getOrgTreeList();
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 完成设置组织机构
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param ids
	 * @param orgId
	 * @return PageResult
	 */
	@RequestMapping("/doOrgUpdate")
	@ResponseBody
	public PageResult doOrgUpdate(Integer[] ids, Integer orgId) {
		try {
			userService.doOrgUpdate(ids, orgId);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置组织机构错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}

	/**
	 * 初始化密码
	 * 
	 * v1.0 zhanghc 2017年7月13日下午9:27:18
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/initPwd")
	@ResponseBody
	public PageResult initPwd(Integer id) {
		try {
			String initPwd = StringUtil.getRandomStr(8);
			userService.doPwdUpdate(id, initPwd);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
			return new PageResultEx(true, "初始化成功", data);
		} catch (Exception e) {
			log.error("完成初始化密码错误：", e);
			return new PageResult(false, "初始化失败：" + e.getMessage());
		}
	}
}
