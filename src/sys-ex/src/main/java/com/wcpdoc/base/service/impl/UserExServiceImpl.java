package com.wcpdoc.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.wcpdoc.auth.realm.JWTRealm;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.entity.UserRowData;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserExService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.entity.FileEx;
import com.wcpdoc.file.service.FileService;

/**
 * 用户扩展服务层实现
 * 
 * v1.0 zhanghc 2021年10月15日下午1:44:08
 */
@Service
public class UserExServiceImpl extends BaseServiceImp<Object> implements UserExService {
	@Resource
	private JWTRealm jwtRealm;
	@Resource
	private FileService fileService;
	@Resource
	private OrgService orgService;
	@Resource
	@Lazy
	private UserService userService;

	@Override
	public RBaseDao<Object> getDao() {
		return null;
	}

	/**
	 * 用户导入表结构<br/>
	 * 用户导入<br/>
	 * 说明：密码不填，默认为111111 <br/>
	 * 姓名* 登录账号* 机构名称* 密码 邮箱 手机号<br/>
	 * 张三 zs 山西分部 s36852 xm@qq.com 13000000000<br/>
	 * 李四 ls 山西软件部 <br/>
	 * 王五 ww 山西运维部 <br/>
	 * 赵六 zl 山西研发组 <br/>
	 * 田七 tq 山西测试组 <br/>
	 */
	@Override
	public void xlsImport(Integer fileId) {
		// 数据校验
		List<Org> orgList = orgService.getList();
		Map<String, Org> orgCache = new HashMap<>();
		for (Org org : orgList) {
			orgCache.put(org.getName(), org);// 机构名称是唯一的
		}
		Map<String, User> userCache = new HashMap<>();
		List<User> userList = userService.getList();
		for (User user : userList) {
			userCache.put(user.getLoginName(), user);
		}

		FileEx fileEx = fileService.getFileEx(fileId);
		EasyExcel.read(fileEx.getFile(), UserRowData.class, new PageReadListener<UserRowData>(userRowList -> {
			for (UserRowData userRowData : userRowList) {
				if (!ValidateUtil.isValid(userRowData.getName())) {
					continue;// excel添加名称后删除，还是能解析到。处理方式为姓名为空，直接处理下一行
//					throw new MyException(String.format("姓名为必填项"));
				}
				if (!ValidateUtil.isValid(userRowData.getLoginName())) {
					throw new MyException(String.format("登录账号为必填项"));
				}
				if (!ValidateUtil.isValid(userRowData.getOrgName())) {
					throw new MyException(String.format("机构名称为必填项"));
				}
				if (orgCache.get(userRowData.getOrgName()) == null) {
					throw new MyException(String.format("机构不存在：%s", userRowData.getOrgName()));
				}
			}

			// 如果存在则更新
			Date curTime = new Date();
			for (UserRowData userRowData : userRowList) {
				if (!ValidateUtil.isValid(userRowData.getName())) {
					continue;
				}
				User user = userCache.get(userRowData.getLoginName());
				if (user != null) {
					user.setName(userRowData.getName());
					user.setOrgId(orgCache.get(userRowData.getOrgName()).getId());
					if (ValidateUtil.isValid(userRowData.getEmail())) {
						user.setEmail(userRowData.getEmail());
					}
					if (ValidateUtil.isValid(userRowData.getPhone())) {
						user.setPhone(userRowData.getPhone());
					}
					if (ValidateUtil.isValid(userRowData.getPwd())) {
						user.setPwd(userService.getEncryptPwd(user.getLoginName(), userRowData.getPwd()));
					}
					user.setUpdateTime(curTime);
					user.setUpdateUserId(getCurUser().getId());
					userService.updateById(user);
					continue;
				}

				// 如果不存在则添加
				user = new User();
				user.setLoginName(userRowData.getLoginName());
				user.setName(userRowData.getName());
				user.setOrgId(orgCache.get(userRowData.getOrgName()).getId());
				if (ValidateUtil.isValid(userRowData.getEmail())) {
					user.setEmail(userRowData.getEmail());
				}
				if (ValidateUtil.isValid(userRowData.getPhone())) {
					user.setPhone(userRowData.getPhone());
				}
				user.setPwd(userService.getEncryptPwd(user.getLoginName(),
						ValidateUtil.isValid(userRowData.getPwd()) ? userRowData.getPwd() : "111111"));// 初始化密码

				user.setRegistTime(curTime);
				user.setUpdateTime(curTime);
				user.setUpdateUserId(getCurUser().getId());
				user.setState(1);
				user.setType(1);
				user.setParentId(getCurUser().getId());// 该接口只有admin可访问
				userService.save(user);
			}
		})).sheet().doRead();
	}

}
