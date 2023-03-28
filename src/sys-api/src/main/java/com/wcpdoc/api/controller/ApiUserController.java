package com.wcpdoc.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.cache.UserCache;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserExService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.OnlineUserService;
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
			UserCache.tryWriteLock("userAdd", 5000);
			// 校验数据有效性
			if (!ValidateUtil.isValid(user.getLoginName())) {
				throw new MyException("参数错误：loginName");
			}
			if (userService.existLoginName(user)) {
				throw new MyException("登录账号已存在");
			}
			if (user.getOrgId() == null) {
				user.setOrgId(1);// 如果没有默认为根节点
			}

			// 添加用户
			Date curTime = new Date();
			user.setRegistTime(curTime);
			user.setUpdateTime(curTime);
			user.setUpdateUserId(getCurUser().getId());
			user.setType(1);
			user.setState(1);
			if(ValidateUtil.isValid(user.getHeadFileId())){
				fileService.upload(user.getHeadFileId());
			}
			userService.add(user);

			// 设置密码
			String initPwd = userService.pwdUpdate(user.getId());
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("添加用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加用户错误：", e);
			return PageResult.err();
		} finally {
			UserCache.releaseWriteLock("userAdd");
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
				throw new MyException("登录账号已存在");
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
				fileService.upload(user.getHeadFileId());
			}
			entity.setHeadFileId(user.getHeadFileId());
			userService.update(entity);
			
			// 更新密码
			Map<String, Object> data = new HashMap<String, Object>();
			if (changeLoginName) {
				String initPwd = userService.pwdUpdate(user.getId());
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
			if (!ValidateUtil.isValid(id)) {
				id = getCurUser().getId();
			}
			User user = userService.getEntity(id);
			Org org = null;
			if (user.getOrgId() != null) {
				org = orgService.getEntity(user.getOrgId());
			}
			
			PageResultEx pageResult = PageResultEx.ok()
				.addAttr("id", user.getId())
				.addAttr("name", user.getName())
				.addAttr("loginName", user.getLoginName())
				.addAttr("orgId", user.getOrgId())
				.addAttr("orgName", org == null ? null : org.getName())
				.addAttr("state", user.getState());
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
			String pwdInit = userService.pwdUpdate(id);
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
		} catch (MyException e) {
			log.error("导入用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("导入用户错误：", e);
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
	@ResponseBody
	public void template() {
		try {
			fileService.exportTemplate("用户.xlsx");
		} catch (Exception e) {
			log.error("用户导出模板下载附件失败：", e);
		}
	}
}
