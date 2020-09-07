package com.wcpdoc.exam.base.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	@Resource
	private OrgService orgService;
	@Resource
	private PostService postService;

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
			return "/base/user/userList";
		} catch (Exception e) {
			log.error("到达用户列表页面错误：", e);
			return "/base/user/userList";
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
	public PageResult orgTreeList() {
		try {
			return new PageResultEx(true, "查询成功", orgService.getTreeList());
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new PageResult(false, "查询失败");
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
	@RequestMapping("/postTreeList")
	@ResponseBody
	public PageResult postTreeList(Integer id) {
		try {
			User user = userService.getEntity(id);
			List<Post> postList = postService.getOrgPostList(user.getOrgId());
			List<Map<String, Object>> postMapList = new ArrayList<>();
			for (Post post : postList) {
				Map<String, Object> map = new HashMap<>();
				map.put("ID", post.getId());
				map.put("NAME", post.getName());
				map.put("PARENT_ID", null);
				if (ValidateUtil.isValid(user.getPostIds()) && user.getPostIds().contains(String.format(",%s,", post.getId()))) {
					map.put("CHECKED", true);
				}
				postMapList.add(map);
			}
			
			return new PageResultEx(true, "查询成功", postMapList);
		} catch (Exception e) {
			log.error("获取岗位树错误：", e);
			return new PageResult(false, "查询失败");
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
	public PageResult list(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", userService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("用户列表错误：", e);
			return new PageResult(false, "查询失败");
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
			return "/base/user/userEdit";
		} catch (Exception e) {
			log.error("到达添加用户页面 错误：", e);
			return "/base/user/userEdit";
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
			// 校验数据有效性
			if (!ValidateUtil.isValid(user.getLoginName())) {
				throw new MyException("参数错误：loginName");
			}
			if (userService.existLoginName(user)) {
				throw new MyException("登录名称已存在");
			}
			
			// 添加用户
			Date date = new Date();
			user.setRegistTime(date);
			user.setUpdateTime(date);
			user.setUpdateUserId(getCurUser().getId());
			user.setState(1);
			userService.add(user);
			
			// 设置密码
			String initPwd = StringUtil.getRandomStr(8);
			userService.doPwdUpdate(user.getId(), initPwd);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
			return new PageResultEx(true, "添加成功", data);
		} catch (MyException e) {
			log.error("完成添加用户错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加用户错误：", e);
			return new PageResult(false, "未知异常！");
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
			Org org = orgService.getEntity(user.getOrgId());
			model.addAttribute("user", user);
			model.addAttribute("org", org);
			return "/base/user/userEdit";
		} catch (Exception e) {
			log.error("到达修改用户页面 错误：", e);
			return "/base/user/userEdit";
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
			// 校验数据有效性
			if (!ValidateUtil.isValid(user.getLoginName())) {
				throw new MyException("参数错误：loginName");
			}
			if (userService.existLoginName(user)) {
				throw new MyException("登录名称已存在");
			}
			
			// 修改用户
			User entity = userService.getEntity(user.getId());
			boolean changeLoginName = false;
			if (!entity.getLoginName().equals(user.getLoginName())) {
				changeLoginName = true;
			}

			entity.setName(user.getName());
			entity.setLoginName(user.getLoginName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			userService.update(entity);

			// 更新密码
			String initPwd = null;
			Map<String, Object> data = new HashMap<String, Object>();
			if (changeLoginName) {
				initPwd = StringUtil.getRandomStr(8);
				userService.doPwdUpdate(user.getId(), initPwd);
				data.put("initPwd", initPwd);
			}

			return new PageResultEx(true, "修改成功", data);
		} catch (MyException e) {
			log.error("完成修改用户错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改用户错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}

	/**
	 * 完成删除用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			User user = userService.getEntity(id);
			if (user.getState() == 1) {
				user.setState(0);
				user.setUpdateTime(new Date());
				user.setUpdateUserId(getCurUser().getId());
				userService.update(user);
			}
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除用户错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除用户错误：", e);
			return new PageResult(false, "未知异常！");
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
			return "/base/user/userPostUpdate";
		} catch (Exception e) {
			log.error("到达设置岗位页面错误：", e);
			return "/base/user/userPostUpdate";
		}
	}

	/**
	 * 完成设置岗位
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @param postIds
	 * @return PageResult
	 */
	@RequestMapping("/doPostUpdate")
	@ResponseBody
	public PageResult doPostUpdate(Integer id, Integer[] postIds) {
		try {
			// 校验数据有效性
			if (id == null) {
				throw new MyException("参数错误：id");
			}
			if (!ValidateUtil.isValid(postIds)) {
				throw new MyException("参数错误：postIds");
			}
			
			User user = userService.getEntity(id);
			List<Post> postList = postService.getOrgPostList(user.getOrgId());
			Set<Integer> postIdSet = new HashSet<Integer>();
			for (Post post : postList) {
				postIdSet.add(post.getId());
			}
			if (!postIdSet.containsAll(Arrays.asList(postIds))) {
				throw new MyException("参数错误：postIds");
			}
			
			// 更新岗位
			user.setPostIds(String.format(",%s,", StringUtil.join(postIds, ",")));
			user.setUpdateTime(new Date());
			user.setUpdateUserId(getCurUser().getId());
			userService.update(user);
			return new PageResult(true, "设置成功");
		} catch (MyException e) {
			log.error("完成设置岗位错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成设置岗位错误：", e);
			return new PageResult(false, "未知异常！");
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
			return "/base/user/userOrgUpdate";
		} catch (Exception e) {
			log.error("到达设置组织机构页面错误：", e);
			return "/base/user/userOrgUpdate";
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
	public PageResult doOrgUpdate(Integer id, Integer orgId) {
		try {
			// 校验数据有效性
			if (id == null) {
				throw new MyException("参数错误：id");
			}
			if (orgId == null) {
				throw new MyException("参数错误：orgId");
			}
			
			// 完成设置组织机构
			User user = userService.getEntity(id);
			if (user.getOrgId().intValue() != orgId.intValue()) {
				user.setOrgId(orgId);
				user.setUpdateTime(new Date());
				user.setUpdateUserId(getCurUser().getId());
				user.setPostIds(null);//清空岗位，因为机构变更了
				userService.update(user);
			}
			return new PageResult(true, "设置成功");
		} catch (MyException e) {
			log.error("完成设置组织机构错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成设置组织机构错误：", e);
			return new PageResult(false, "未知异常！");
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
		} catch (MyException e) {
			log.error("完成初始化密码错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成初始化密码错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
