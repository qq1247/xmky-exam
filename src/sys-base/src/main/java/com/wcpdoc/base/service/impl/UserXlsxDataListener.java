package com.wcpdoc.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.entity.UserXlsx;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.util.EncryptUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 监听器
 * 
 * v1.0 chenyun 2022-4-16下午17:24:19
 */
public class UserXlsxDataListener extends AnalysisEventListener<UserXlsx> {
	@Resource
	private OrgService orgService = SpringUtil.getBean(OrgService.class);
	@Resource
	private UserService userService = SpringUtil.getBean(UserService.class);;
	
    List<UserXlsx> list = new ArrayList<UserXlsx>();
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public UserXlsxDataListener() {}
    
    /**
     * 这个每一条数据解析都会来调用
     *
     * @param userXlsx
     * @param context
     */
    @Override
    public void invoke(UserXlsx userXlsx, AnalysisContext context) {
        if (userXlsx != null && ValidateUtil.isValid(userXlsx.getOrgName()) && ValidateUtil.isValid(userXlsx.getLoginName()) && ValidateUtil.isValid(userXlsx.getName())) {
        	System.out.println("解析到一条数据:{}"  + JSON.toJSONString(userXlsx));
        	list.add(userXlsx);
		}
    }
    
    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        for(UserXlsx userXlsxs : list){
    		Org org = orgService.getOrg(userXlsxs.getOrgName());
    		User user = userService.getUser(userXlsxs.getLoginName());
    		if (user == null) {
    			Date date = new Date();
    			User entity = new User();
    			entity.setName(userXlsxs.getName());
    			entity.setLoginName(userXlsxs.getLoginName());
    			entity.setPwd(EncryptUtil.md52Base64(userXlsxs.getLoginName() + "111111"));
    			if (ValidateUtil.isValid(userXlsxs.getEmail())) {
    				entity.setEmail(userXlsxs.getEmail());
    			}
    			if (ValidateUtil.isValid(userXlsxs.getPhone())) {
    				entity.setPhone(userXlsxs.getPhone());
    			}
    			entity.setRegistTime(date);
    			entity.setOrgId(org.getId());
    			entity.setRoles("user");
    			entity.setType(1);
    			entity.setUpdateTime(date);
    			entity.setUpdateUserId(1);	
    			entity.setState(1);
    			userService.add(entity);
    			continue;
    		}
    		if (user != null && org.getId() != user.getOrgId()) {
    			Date date = new Date();
    			User entity = new User();
    			entity.setName(userXlsxs.getName());
    			entity.setLoginName(userXlsxs.getLoginName());
    			entity.setPwd(EncryptUtil.md52Base64(userXlsxs.getLoginName() + "111111"));
    			if (ValidateUtil.isValid(userXlsxs.getEmail())) {
    				entity.setEmail(userXlsxs.getEmail());
    			}
    			if (ValidateUtil.isValid(userXlsxs.getPhone())) {
    				entity.setPhone(userXlsxs.getPhone());
    			}
    			entity.setRegistTime(date);
    			entity.setOrgId(org.getId());
    			entity.setUpdateTime(date);
    			entity.setUpdateUserId(1);	
    			entity.setState(1);
    			userService.add(entity);
    			continue;
    		}
    		if (user != null && org.getId() == user.getOrgId()) {
    			user.setName(userXlsxs.getName());
    			if (ValidateUtil.isValid(userXlsxs.getEmail())) {
    				user.setEmail(userXlsxs.getEmail());
    			}
    			if (ValidateUtil.isValid(userXlsxs.getPhone())) {
    				user.setPhone(userXlsxs.getPhone());
    			}
    			user.setName(userXlsxs.getName());
    			user.setUpdateTime(new Date());
    			user.setUpdateUserId(1);
    			userService.update(user);
    		}
        }
    }
}
