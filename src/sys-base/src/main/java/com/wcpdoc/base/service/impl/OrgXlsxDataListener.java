package com.wcpdoc.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.OrgXlsx;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 监听器
 * 
 * v1.0 chenyun 2022-4-16下午17:24:19
 */
public class OrgXlsxDataListener extends AnalysisEventListener<OrgXlsx> {
	@Resource
	private OrgService orgService = SpringUtil.getBean(OrgService.class);
	
    List<OrgXlsx> list = new ArrayList<OrgXlsx>();
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public OrgXlsxDataListener() {}
    
    /**
     * 这个每一条数据解析都会来调用
     *
     * @param orgXlsx
     * @param context
     */
    @Override
    public void invoke(OrgXlsx orgXlsx, AnalysisContext context) {
        if (orgXlsx != null && ValidateUtil.isValid(orgXlsx.getName()) && ValidateUtil.isValid(orgXlsx.getParentName()) && ValidateUtil.isValid(orgXlsx.getNo())) {
        	System.out.println("解析到一条数据:{}"  + JSON.toJSONString(orgXlsx));
        	list.add(orgXlsx);
		}
    }
    
    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        for(OrgXlsx orgXlsxs : list){
    		Org orgOld = orgService.getOrg(orgXlsxs.getName());
    		Org parentOrg = orgService.getOrg(orgXlsxs.getParentName());

    		if(parentOrg != null && orgOld == null){
    			Org org = new Org();
    			org.setName(orgXlsxs.getName());
    			
    			//获取父id
    			org.setParentId(parentOrg.getId());
    			
    			org.setUpdateUserId(1);
    			org.setUpdateTime(new Date());
    			org.setState(1);
    			org.setNo((int)Math.ceil(orgXlsxs.getNo())); //(int)Math.ceil(Double.valueOf(map.get("no").toString()))
    			orgService.add(org);
    			
    			// 更新父子关系
    			org.setParentIds(parentOrg.getParentIds() + org.getId() + "_");
    			org.setLevel(org.getParentIds().split("_").length - 1);
    			orgService.update(org);
    			continue;
    		}

    		if(parentOrg != null && orgOld != null){
    			if (orgOld.getParentId().intValue() == parentOrg.getId().intValue()) {
    				orgOld.setNo((int)Math.ceil(orgXlsxs.getNo())); //(int)Math.ceil(Double.valueOf(map.get("no").toString()))
    				orgOld.setUpdateUserId(1);
    				orgOld.setUpdateTime(new Date());
    				orgService.update(orgOld);
    				continue;
    			}
    			
    			List<Org> sonOrg = orgService.getList(orgOld.getId());
    			if (sonOrg != null) {
    				throw new MyException("此组织机构下有附属机构不能被移动");
    			}

    			if (orgOld.getParentIds().contains(parentOrg.getParentIds())) {
    				throw new MyException("父组织机构不能移动到子组织机构下");
    			}
    			
    			orgOld.setParentId(parentOrg.getId());
    			orgOld.setParentIds(parentOrg.getParentIds() + orgOld.getId() + "_");
    			orgOld.setLevel(orgOld.getParentIds().split("_").length - 1);
    			orgOld.setNo(orgXlsxs.getNo());
    			orgOld.setUpdateUserId(1);
    			orgOld.setUpdateTime(new Date());
    			orgService.update(orgOld);
    		}
        }
    }
}
