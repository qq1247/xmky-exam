package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperTypeExService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
/**
 * 试卷分类服务层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Service
public class PaperTypeServiceImpl extends BaseServiceImp<PaperType> implements PaperTypeService{
	@Resource
	private PaperTypeDao paperTypeDao;
	@Resource
	private PaperTypeExService paperTypeExService;
	@Resource
	private OrgService orgService;
	@Resource
	private PostService postService;

	@Override
	@Resource(name = "paperTypeDaoImpl")
	public void setDao(BaseDao<PaperType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(PaperType paperType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(paperType.getName())) {
			throw new MyException("参数错误：name");
		}
		if (paperType.getParentId() == null || paperType.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		
		if (existName(paperType)) {
			throw new MyException("名称已存在！");
		}
				
		// 添加试卷分类
		paperType.setUpdateUserId(getCurUser().getId());
		paperType.setUpdateTime(new Date());
		paperType.setState(1);
		add(paperType);
		
		// 更新父子关系
		PaperType parentPaperType = paperTypeDao.getEntity(paperType.getParentId());
		paperType.setParentSub(parentPaperType.getParentSub() + paperType.getId() + "_");
		paperType.setLevel(paperType.getParentSub().split("_").length - 1);
		update(paperType);
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试卷分类
			return;
		}
		List<PaperType> paperTypeList = paperTypeDao.getList(id);
		if (ValidateUtil.isValid(paperTypeList)) {
			throw new MyException("请先删除子试卷分类！");
		}
		
		// 删除试卷分类
		PaperType paperType = getEntity(id);
		paperType.setState(0);
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(getCurUser().getId());
		update(paperType);
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return paperTypeDao.getTreeList();
	}
	
	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		// 校验数据有效性
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if (targetId == null) {
			throw new MyException("参数错误：targetId");
		}
		if (sourceId == targetId) {
			throw new MyException("源试卷分类和目标试卷分类一致！");
		}

		PaperType source = getEntity(sourceId);
		PaperType target = getEntity(targetId);
		if (target.getParentSub().contains(source.getParentSub())) {
			throw new MyException("父试卷分类不能移动到子试卷分类下！");
		}

		// 移动试卷分类
		source.setParentId(target.getId());
		source.setParentSub(String.format("%s%s_", target.getParentSub(), source.getId()));
		source.setLevel(source.getParentSub().split("_").length - 1);
		update(source);
		
		List<PaperType> subSourceList = paperTypeDao.getList(source.getId());
		doMove(source, subSourceList);
	}
		
	private void doMove(PaperType target, List<PaperType> subTargetList) {
		for (PaperType subTarget : subTargetList) {
			subTarget.setParentId(target.getId());
			subTarget.setParentSub(String.format("%s%s_", target.getParentSub(), subTarget.getId()));
			subTarget.setLevel(subTarget.getParentSub().split("_").length - 1);
			update(subTarget);
			
			List<PaperType> subSubTargetList = paperTypeDao.getList(subTarget.getId());
			if (ValidateUtil.isValid(subSubTargetList)) {
				doMove(subTarget, subSubTargetList);
			}
		}
	}

	@Override
	public boolean existName(PaperType paperType) {
		return paperTypeDao.existName(paperType.getName(), paperType.getId());
	}

	@Override
	public List<PaperType> getList() {
		return paperTypeDao.getList();
	}

	@Override
	public void doAuth(Integer id, Integer[] userIds, Integer[] postIds, Integer[] orgIds, boolean syn2Sub) {
		if(syn2Sub){
			List<PaperType> paperTypeList = paperTypeDao.getList(id);
			for(PaperType paperType : paperTypeList){
				doAuth(paperType.getId(), userIds, postIds, orgIds, syn2Sub);
			}
		}
		
		PaperType entity = getEntity(id);
		if (!ValidateUtil.isValid(userIds)) {
			entity.setUserIds(null);
		} else {
			entity.setUserIds(String.format(",%s,", StringUtil.join(userIds)));
		}
		if (!ValidateUtil.isValid(postIds)) {
			entity.setPostIds(null);
		} else {
			entity.setPostIds(String.format(",%s,", StringUtil.join(postIds)));
		}
		if (!ValidateUtil.isValid(orgIds)) {
			entity.setOrgIds(null);
		} else {
			entity.setOrgIds(String.format(",%s,", StringUtil.join(orgIds)));
		}
		
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}

	@Override
	public PageOut getAuthUserListpage(PageIn pageIn) {
		return paperTypeDao.getAuthUserListpage(pageIn);
	}

	@Override
	public PageOut getAuthPostListpage(PageIn pageIn) {
		return paperTypeDao.getAuthPostListpage(pageIn);
	}

	@Override
	public PageOut getAuthOrgListpage(PageIn pageIn) {
		return paperTypeDao.getAuthOrgListpage(pageIn);
	}
}
