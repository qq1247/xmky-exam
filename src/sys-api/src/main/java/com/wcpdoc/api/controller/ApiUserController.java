package com.wcpdoc.api.controller;

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

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.entity.UserXlsx;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.service.UserXlsxService;
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
	@ResponseBody
	public PageResult listpage() {
		try {
			PageOut pageOut = userService.getListpage(new PageIn(request));
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("roles") != null) {
					map.put("roleNames", ConstantManager.SUB_ADMIN_LOGIN_NAME.equals(((String) map.get("roles"))) ? "子管理员" : "用户");
				}else{
					map.put("roleNames", "用户");
				}
				
				if (ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())// 如果是管理员或子管理员，显示在线状态和最后在线时间
						|| ConstantManager.SUB_ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
					Integer id = (Integer)map.get("id");
					OnlineUser onlineUser = onlineUserService.getEntity(id);
					if (onlineUser == null) {
						map.put("online", false);
						map.put("onlineTime", null);
						continue;
					}
					
					map.put("online", onlineUser.getState());
					map.put("onlineTime", DateUtil.formatDateTime(onlineUser.getUpdateTime()));
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
			entity.setRoles(user.getRoles().contains("subAdmin") ? "user,subAdmin" : "user");
			entity.setName(user.getName());
			entity.setLoginName(user.getLoginName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			userService.update(entity);

			// 更新密码
			String initPwd = null;
			Map<String, Object> data = new HashMap<String, Object>();
			if (changeLoginName) {
				initPwd = "111111";// StringUtil.getRandomStr(8);
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
					.addAttr("orgName", org == null ? null : org.getName())
					.addAttr("state", entity.getState())
					.addAttr("roles", (ValidateUtil.isValid(entity.getRoles()) && entity.getRoles().contains("subAdmin")) ? new String[]{"subAdmin"} : new String[]{"user"});
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
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/initPwd")
	@ResponseBody
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
	 * 设置角色
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param id
	 * @param postIds
	 * @return PageResult
	 */
	@RequestMapping("/roleUpdate")
	@ResponseBody
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
	 * 强制退出登陆
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * @param userId
	 * @return pageOut
	 */
	@RequestMapping("/out")
	@ResponseBody
	public PageResult out(Integer userId) {
		try {
			onlineUserService.out(userId);
			return PageResult.ok();
		} catch (Exception e) {
			log.error("强制退出登陆错误：", e);
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
	 * 组织机构用户同步
	 * v1.0 zhanghc 2016年8月27日上午11:36:55
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/syncUser")
	@ResponseBody
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
