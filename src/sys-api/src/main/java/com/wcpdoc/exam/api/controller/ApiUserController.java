package com.wcpdoc.exam.api.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.auth.cache.TokenCache;
import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.entity.UserXlsx;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.base.service.UserXlsxService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	private OrgService orgService;
	@Resource
	private UserXlsxService userXlsxService;

	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/orgTreeList")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult orgTreeList() {
		try {
			return PageResultEx.ok().data(orgService.getTreeList());
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return PageResult.err();
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
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult listpage() {
		try {
			PageOut pageOut = userService.getListpage(new PageIn(request));
			for (Map<String, Object> map : pageOut.getList()) {
				map.put("roleNames", ((String) map.get("roles")).contains("subAdmin") ? "子管理员" : "用户");
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
	@RequiresRoles(value={"admin"},logical = Logical.OR)
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
				throw new MyException("参数错误：orgId");
			}
			if (ValidateUtil.isValid(user.getRoles()) && !user.getRoles().equals("subAdmin")) {
				throw new MyException("参数错误：roles");
			}
			
			// 添加用户
			Date date = new Date();
			user.setRoles("subAdmin".equals(user.getRoles()) ? "user,subAdmin" : "user");
			user.setRegistTime(date);
			user.setUpdateTime(date);
			user.setUpdateUserId(getCurUser().getId());
			user.setState(1);
			userService.add(user);
			
			// 设置密码
			String initPwd = "111111";//StringUtil.getRandomStr(8);
			userService.doPwdUpdate(user.getId(), initPwd);
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
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult edit(User user) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(user.getLoginName())) {
				throw new MyException("参数错误：loginName");
			}
			if (userService.existLoginName(user)) {
				throw new MyException("登录名称已存在");
			}
			if (ValidateUtil.isValid(user.getRoles()) && !user.getRoles().equals("subAdmin")) {
				throw new MyException("参数错误：roles");
			}
			
			// 修改用户
			User entity = userService.getEntity(user.getId());
			boolean changeLoginName = false;
			if (!entity.getLoginName().equals(user.getLoginName())) {
				changeLoginName = true;
			}

			entity.setRoles("subAdmin".equals(user.getRoles()) ? "user,subAdmin" : "user");
			entity.setName(user.getName());
			entity.setLoginName(user.getLoginName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			userService.update(entity);

			// 更新密码
			String initPwd = null;
			Map<String, Object> data = new HashMap<String, Object>();
			if (changeLoginName) {
				initPwd = "111111";//StringUtil.getRandomStr(8);
				userService.doPwdUpdate(user.getId(), initPwd);
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
	@RequiresRoles(value={"admin"},logical = Logical.OR)
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
	 * 设置角色
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @param postIds
	 * @return PageResult
	 */
	@RequestMapping("/roleUpdate")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult roleUpdate(Integer id, String roles) {
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
	 * 设置组织机构
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param ids
	 * @param orgId
	 * @return PageResult
	 */
	@RequestMapping("/orgUpdate")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult orgUpdate(Integer id, Integer orgId) {
		try {
			// 校验数据有效性
			if (id == null) {
				throw new MyException("参数错误：id");
			}
			if (orgId == null) {
				throw new MyException("参数错误：orgId");
			}
			
			// 设置组织机构
			User user = userService.getEntity(id);
			if (user.getOrgId().intValue() != orgId.intValue()) {
				user.setOrgId(orgId);
				user.setUpdateTime(new Date());
				user.setUpdateUserId(getCurUser().getId());
				user.setRoles(null);//清空岗位，因为机构变更了
				userService.update(user);
			}
			return PageResult.ok();
		} catch (MyException e) {
			log.error("设置组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("设置组织机构错误：", e);
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
	@RequestMapping("/initPwd")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult initPwd(Integer id) {
		try {
			String initPwd = "111111";//StringUtil.getRandomStr(8);
			userService.doPwdUpdate(id, initPwd);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("initPwd", initPwd);
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
	 * 导入用户表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * @return PageResult
	 */
	@RequestMapping("/input")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult input(@RequestParam("file") MultipartFile file) {
		try {
			userXlsxService.inputUserXlsx(file);
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
	 * @return PageResult
	 */
	@RequestMapping("/export")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public void export(String ids) {
		ServletOutputStream outputStream = null;
		try {
			List<UserXlsx> userXlsx = userXlsxService.exportUserXlsx(ids);
			try {
				FileOutputStream os = new FileOutputStream("D:/auser.xlsx");
				Context context = new Context();
		        //将列表参数放入context中
		        context.putVar("userXlsxList", userXlsx);
				InputStream inputStream = this.getClass().getResourceAsStream("/res/userTemplate.xlsx");
				JxlsHelper jxlsHelper = JxlsHelper.getInstance();
				Transformer transformer = jxlsHelper.createTransformer(inputStream, os);
				JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
				Map<String, Object> funcs = new HashMap<String, Object>();
				evaluator.getJexlEngine().setFunctions(funcs);
				jxlsHelper.processTemplate(context, transformer);
				os.close();

				FileInputStream fileInputStream = new FileInputStream("D:/auser.xlsx");
				String fileName = new String(("userTemplate.xlsx").getBytes("UTF-8"),"ISO-8859-1");
				response.addHeader("Content-Disposition", "attachment;filename" + fileName);
				response.setContentType("application/fprce-download");
				outputStream = response.getOutputStream();
				IOUtils.copy(fileInputStream, outputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			log.error("用户列表错误：", e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}
	
	/**
	 * 导出模板
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * @return PageResult
	 */
	@RequestMapping("/template")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public void template() {
		OutputStream output = null;
		try {
			//userXlsxService.templateUserXlsx();
			InputStream inputStream = this.getClass().getResourceAsStream("/res/userExample.xlsx");
			String fileName = new String(("userExample.xlsx").getBytes("UTF-8"),"ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename" + fileName);
			response.setContentType("application/fprce-download");
			output = response.getOutputStream();
			IOUtils.copy(inputStream, output);
		} catch (Exception e) {
			log.error("用户导出模板下载附件失败：", e);
		} finally {
			IOUtils.closeQuietly(output);
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
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult get(Integer id) {
		try {
			User entity = userService.getEntity(id);
			Org org = null;
			if(entity.getOrgId() != null){
				org = orgService.getEntity(entity.getOrgId());
			}
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName())
					.addAttr("loginName", entity.getLoginName())
					.addAttr("registTime", DateUtil.formatDateTime(entity.getRegistTime()))
					.addAttr("lastLoginTime", entity.getLastLoginTime() == null ? null : DateUtil.formatDateTime(entity.getLastLoginTime()))
					.addAttr("orgId", entity.getOrgId())
					.addAttr("state", entity.getState())
					.addAttr("orgName", org == null ? null : org.getName())
					.addAttr("roles", entity.getRoles().contains("subAdmin") ? "subAdmin" : "user");
		} catch (MyException e) {
			log.error("获取用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取在线用户
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/onList")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult onList() {
		try {
			return PageResultEx.ok().data(userService.onList());
		} catch (MyException e) {
			log.error("获取在线用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取在线用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 强制退出在线用户
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/exit")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult exit(Integer id) {
		try {
			TokenCache.del(id);
			return PageResult.ok();
		} catch (Exception e) {
			log.error("强制退出在线用户错误：", e);
			return PageResult.err();
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
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult syncUser(String orgName, String orgCode, List<User> user) {
		// org  [name, code]
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
