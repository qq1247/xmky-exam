package com.wcpdoc.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
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

import lombok.extern.slf4j.Slf4j;

/**
 * 用户控制层
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class ApiUserController extends BaseController {

	@Resource
	private UserService userService;
	@Resource
	private UserExService userExService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private FileService fileService;
	@Resource
	private OnlineUserService onlineUserService;

	/**
	 * 用户列表
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (getCurUser().getType() == 0) {// 如果是管理员
				if (!ValidateUtil.isValid(pageIn.getParm("type", String.class))) {// 默认查询考试用户
					pageIn.addParm("type", "1");
				} else {
					pageIn.addParm("parentId", getCurUser().getId());
				}
			} else if (getCurUser().getType() == 2) {// 如果是子管理
				if (!ValidateUtil.isValid(pageIn.getParm("type", String.class)) || pageIn.getParm("type").equals("1")) {// 默认查询考试用户
					User user = baseCacheService.getUser(getCurUser().getId());
					pageIn.addParm("examUserIds", user.getUserIds());
				} else {// 查看阅卷用户
					pageIn.addParm("parentId", getCurUser().getId());
				}
			} else if (getCurUser().getType() == 3) {// 阅卷用户没有角色权限

			} else if (getCurUser().getType() == 1) {// 考试用户没有角色权限

			}
			PageOut pageOut = userService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("用户列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户添加
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(User user) {
		try {
			// 用户添加
			userService.addEx(user);

			// 密码初始化
			String initPwd = userService.pwdInit(user.getId());
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("用户添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户修改
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param user
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(User user) {
		try {
			// 用户修改
			User entity = baseCacheService.getUser(user.getId());
			String oldLoginName = entity.getLoginName();
			userService.editEx(user);
			String newLoginName = entity.getLoginName();

			// 更新密码
			Map<String, Object> data = new HashMap<String, Object>();
			if (!oldLoginName.equals(newLoginName)) {
				String initPwd = userService.pwdInit(user.getId());
				data.put("initPwd", initPwd);
			}

			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("用户修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户删除
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			userService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("用户删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户获取
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			if (!ValidateUtil.isValid(id)) {
				id = getCurUser().getId();
			}
			User user = baseCacheService.getUser(id);
			Org org = null;
			if (ValidateUtil.isValid(user.getOrgId()) && user.getOrgId() != 0) {
				org = baseCacheService.getOrg(user.getOrgId());
			}

			PageResultEx pageResult = PageResultEx.ok()//
					.addAttr("id", user.getId())//
					.addAttr("name", user.getName())//
					.addAttr("loginName", user.getLoginName())//
					.addAttr("orgId", user.getOrgId())//
					.addAttr("orgName", org == null ? null : org.getName())//
					.addAttr("state", user.getState())//
					.addAttr("userIds", user.getUserIds())//
					.addAttr("orgIds", user.getOrgIds());
			return pageResult;
		} catch (MyException e) {
			log.error("用户获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 密码初始化
	 * 
	 * v1.0 zhanghc 2017年7月13日下午9:27:18
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/pwdInit")
	public PageResult pwdInit(Integer id) {
		try {
			String pwdInit = userService.pwdInit(id);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", pwdInit);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("密码初始化错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("密码初始化错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户冻结
	 * 
	 * v1.0 chenyun 2022年04月21日下午3:36:55
	 * 
	 * @param userId
	 * @return pageOut
	 */
	@RequestMapping("/frozen")
	public PageResult frozen(Integer id) {
		try {
			userService.frozen(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("用户冻结错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户冻结错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 退出登陆
	 * 
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @param userId
	 * @return pageOut
	 */
	@RequestMapping("/out")
	public PageResult out(Integer id) {
		try {
			onlineUserService.out(id);
			return PageResult.ok();
		} catch (Exception e) {
			log.error("退出登陆错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户导入
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/import")
	public PageResult xlsImport(Integer fileId) {
		try {
			userExService.xlsImport(fileId);
			return PageResultEx.ok();
		} catch (MyException e) {
			log.error("用户导入错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户导入错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 用户模板导出
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/template")
	public void template() {
		try {
			fileService.exportTemplate("用户.xlsx");
		} catch (Exception e) {
			log.error("用户模板导出失败：", e);
		}
	}
}
