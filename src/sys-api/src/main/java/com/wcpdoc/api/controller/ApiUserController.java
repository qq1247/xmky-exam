package com.wcpdoc.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserExService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.constant.ConstantManager;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.OnlineUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.OnlineUserService;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.service.FileService;

/**
 * 用户控制层
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Controller
@RequestMapping("/api/user")
public class ApiUserController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiUserController.class);

	@Resource
	private UserService userService;
	@Resource
	private UserExService userExService;
	@Resource
	private OrgService orgService;
	@Resource
	private OnlineUserService onlineUserService;
	@Resource
	private FileService fileService;

	/**
	 * 用户列表
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageOut pageOut = userService.getListpage(new PageIn(request));
			for (Map<String, Object> map : pageOut.getList()) {
				map.put("roles", map.get("roles").toString().split(","));
				
				if (ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) { // 如果是管理员，显示在线状态和最后在线时间
					Integer id = (Integer) map.get("id");
					OnlineUser onlineUser = onlineUserService.getEntity(id);
					if (onlineUser == null) {
						map.put("online", false);
						map.put("onlineTime", null);
						continue;
					}

					map.put("online", onlineUser.getState());
					map.put("onlineTime", DateUtil.formatDateTime(onlineUser.getUpdateTime()));
				}
				
				if (!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {//只有管理员显示以下字段
					map.remove("registTime");
					map.remove("lastLoginTime");
					map.remove("roles");
					map.remove("online");
					map.remove("onlineTime");
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("用户列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(User user) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(user.getLoginName())) {
				throw new MyException("参数错误：loginName");
			}
			if (userService.existLoginName(user)) {
				throw new MyException("登录名称已存在");
			}
			if (user.getOrgId() == null) {
				user.setOrgId(1);// 如果没有默认为根节点
			}

			// 添加用户
			Date curTime = new Date();
			user.setRoles("user");
			user.setType(1);
			user.setRegistTime(curTime);
			user.setUpdateTime(curTime);
			user.setUpdateUserId(getCurUser().getId());
			user.setState(1);
			if(ValidateUtil.isValid(user.getHeadFileId())){
				fileService.doUpload(user.getHeadFileId());
			}
			userService.add(user);

			// 设置密码
			String initPwd = userService.doPwdUpdate(user.getId());
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("添加用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加用户错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 修改用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(User user) {
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

			entity.setOrgId(user.getOrgId());
			//entity.setRoles(null); // 修改不允许修改角色
			entity.setName(user.getName());
			entity.setLoginName(user.getLoginName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setEmail(user.getEmail());
			if((!ValidateUtil.isValid(entity.getHeadFileId()) && ValidateUtil.isValid(user.getHeadFileId())) 
					|| (ValidateUtil.isValid(user.getHeadFileId()) && user.getHeadFileId().intValue() != entity.getHeadFileId().intValue())){
				fileService.doUpload(user.getHeadFileId());
			}
			entity.setHeadFileId(user.getHeadFileId());
			userService.update(entity);
			
			// 更新密码
			Map<String, Object> data = new HashMap<String, Object>();
			if (changeLoginName) {
				String initPwd = userService.doPwdUpdate(user.getId());
				data.put("initPwd", initPwd);
			}

			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("修改用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改用户错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 删除用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			User user = userService.getEntity(id);
			if (user.getState() == 1) {
				user.setState(0);
				user.setUpdateTime(new Date());
				user.setUpdateUserId(getCurUser().getId());
				userService.update(user);
			}
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除用户错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 获取用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			User entity = userService.getEntity(id);
			Org org = null;
			if (entity.getOrgId() != null) {
				org = orgService.getEntity(entity.getOrgId());
			}
			
			PageResultEx pageResult = PageResultEx.ok()
				.addAttr("id", entity.getId())
				.addAttr("name", entity.getName())
				.addAttr("loginName", entity.getLoginName())
				.addAttr("orgId", entity.getOrgId())
				.addAttr("orgName", org == null ? null : org.getName())
				.addAttr("state", entity.getState())
				.addAttr("email", entity.getEmail())
				.addAttr("headFileId", entity.getHeadFileId())
				.addAttr("state", entity.getState())
				;
			
			if (!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				return pageResult;
			}
			
			pageResult.addAttr("registTime", DateUtil.formatDateTime(entity.getRegistTime()))
				.addAttr("lastLoginTime", entity.getLastLoginTime() == null ? null : DateUtil.formatDateTime(entity.getLastLoginTime()))
				.addAttr("roles", (ValidateUtil.isValid(entity.getRoles()) && entity.getRoles().contains("subAdmin"))
						? new String[] { "subAdmin" } : new String[] { "user" });
			return pageResult;
		} catch (MyException e) {
			log.error("获取用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取用户错误：", e);
			return PageResult.err();
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
	@RequestMapping("/pwdInit")
	@ResponseBody
	public PageResult pwdInit(Integer id) {
		try {
			String pwdInit = userService.doPwdUpdate(id);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", pwdInit);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("初始化密码错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("初始化密码错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 设置角色
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @param postIds
	 * @return PageResult
	 */
	@RequestMapping("/role")
	@ResponseBody
	public PageResult role(Integer id, String[] roles) {
		try {
			userService.roleUpdate(id, roles);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("设置角色错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("设置角色错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 强制退出登陆 v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @param userId
	 * @return pageOut
	 */
	@RequestMapping("/out")
	@ResponseBody
	public PageResult out(Integer id) {
		try {
			onlineUserService.out(id);
			return PageResult.ok();
		} catch (Exception e) {
			log.error("强制退出登陆错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 冻结账户  v1.0 chenyun 2022年04月21日下午3:36:55
	 * 
	 * @param userId
	 * @return pageOut
	 */
	@RequestMapping("/frozen")
	@ResponseBody
	public PageResult frozen(Integer id) {
		try {
			userService.frozen(id);
			return PageResult.ok();
		} catch (Exception e) {
			log.error("冻结账户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 导入用户
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/import")
	@ResponseBody
	public PageResult xlsImport(Integer fileId) {
		try {
			userExService.xlsImport(fileId);
			return PageResultEx.ok();
		} catch (Exception e) {
			log.error("用户列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 导出用户表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/export")
	@ResponseBody
	public void export(Integer[] ids) {
		try {
//	        userXlsxService.export(ids);
		} catch (MyException e) {
			log.error("导出用户表失败：", e.getMessage());
		} catch (Exception e) {
			log.error("导出用户表失败：", e);
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
	@ResponseBody
	public void template() {
		try {
			fileService.exportTemplate("用户.xlsx");
		} catch (Exception e) {
			log.error("用户导出模板下载附件失败：", e);
		}
	}

	/**
	 * 组织机构用户同步 
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/syncUser")
	@ResponseBody
	public PageResult syncUser(String orgName, String orgCode, List<User> user) {
		// org [name, code]
		// user [name, loginName, email, phone, pwd ]
		try {
			Integer orgId = orgService.syncOrg(orgName, orgCode);
			userService.syncUser(user, orgId);
			return PageResult.ok();
		} catch (Exception e) {
			log.error("组织机构用户同步错误：", e);
			return PageResult.err();
		}
	}
}
