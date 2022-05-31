package com.wcpdoc.base.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.OrgRowData;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.OrgExService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.TreeUtil;
import com.wcpdoc.core.util.TreeUtil.Tree;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.entity.FileEx;
import com.wcpdoc.file.service.FileService;

/**
 * 机构扩展服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgExServiceImpl extends BaseServiceImp<Object> implements OrgExService {
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;
	@Resource
	private FileService fileService;

	@Override
	public void delAndUpdate(Org org) {
		List<User> userList = userService.getList(org.getId());
		if (ValidateUtil.isValid(userList)) {
			throw new MyException("该机构下有用户，不允许删除");
		}
	}

	@Override
	public void setDao(BaseDao<Object> dao) {
		
	}

	/**
	 * excel模板数据
	 * 
	 * 机构名称*	所属机构		
	 * 软件部					
	 * 测试部		软件部
	 * 研发部		软件部
	 * 研发1组	研发部
	 * 研发2组	研发部
	 * 研发部		研发2组（导致相互上下级：校验机构唯一）
	 * 研发3组	山西总部（没有上级：默认一级机构）
	 */
	@Override
	public void xlsImport(Integer fileId) {
		// 校验数据有效性
		List<Org> orgList = orgService.getList();
		if (orgList.size() > 1) {// 存在一个根机构
			throw new MyException("请先清空机构");
		}
		
		FileEx fileEx = fileService.getFileEx(fileId);
		EasyExcel.read(fileEx.getFile(), OrgRowData.class, new PageReadListener<OrgRowData>(orgRowList -> {
			Set<String> orgNameCache = new HashSet<>();
			for (OrgRowData orgRowData : orgRowList) {
				if (!ValidateUtil.isValid(orgRowData.getName())) {
					continue;// 添加行保存删除行，读取的时候会读到空行
					//throw new MyException(String.format("机构名称为必填项"));
				}
				
				if (orgNameCache.contains(orgRowData.getName())) {
					throw new MyException(String.format("机构名称重复：%s", orgRowData.getName()));
				}
				orgNameCache.add(orgRowData.getName());
			}
			for (OrgRowData orgRowData : orgRowList) {
				if (!ValidateUtil.isValid(orgRowData.getName())) {
					continue;
				}
				if (!ValidateUtil.isValid(orgRowData.getParentName())) {// 一级机构忽略
					continue;
				}
				if (!orgNameCache.contains(orgRowData.getParentName())) {
					throw new MyException(String.format("所属机构未定义：%s", orgRowData.getParentName()));
				}
			}
			
			// 生成子父级关系
			List<Tree> orgTreeList = TreeUtil.getTree(orgRowList);
			
			// 保存机构信息
			Org topOrg = new Org();// 根机构
			topOrg.setId(1);
			saveOrg(orgTreeList, topOrg);
		})).sheet().doRead();
	}

	private void saveOrg(List<Tree> orgTreeList, Org parentOrg) {
		int no = 1;
		for (Tree orgTree : orgTreeList) {
			OrgRowData orgRowData = (OrgRowData) orgTree;
			
			Org curOrg = new Org();
			curOrg.setName(orgRowData.getName());
			curOrg.setParentId(parentOrg.getId());
			curOrg.setNo(no++);
			orgService.addAndUpdate(curOrg);
			
			if (ValidateUtil.isValid(orgRowData.getChildren())) {// 有子节点则遍历保存
				saveOrg(orgRowData.getChildren(), curOrg);
			}
		}
	}
}
