package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.ExamTypeDao;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamTypeExService;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
/**
 * 考试分类服务层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Service
public class ExamTypeServiceImpl extends BaseServiceImp<ExamType> implements ExamTypeService {
	@Resource
	private ExamTypeDao examTypeDao;
	@Resource
	private ExamTypeExService examTypeExService;
	@Resource
	private OrgService orgService;
	@Resource
	private PostService postService;

	@Override
	@Resource(name = "examTypeDaoImpl")
	public void setDao(BaseDao<ExamType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(ExamType examType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(examType.getName())) {
			throw new MyException("参数错误：name");
		}
		if (examType.getParentId() == null || examType.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		
		if (existName(examType)) {
			throw new MyException("名称已存在！");
		}
				
		// 添加试题分类
		examType.setUpdateUserId(getCurUser().getId());
		examType.setUpdateTime(new Date());
		examType.setState(1);
		add(examType);
		
		// 更新父子关系
		ExamType parentExamType = examTypeDao.getEntity(examType.getParentId());
		examType.setParentSub(parentExamType.getParentSub() + examType.getId() + "_");
		examType.setLevel(examType.getParentSub().split("_").length - 1);
		update(examType);
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试题分类
			return;
		}
		List<ExamType> examTypeList = examTypeDao.getList(id);
		if (ValidateUtil.isValid(examTypeList)) {
			throw new MyException("请先删除子试题分类！");
		}
		
		// 删除试题分类
		ExamType examType = getEntity(id);
		examTypeExService.delAndUpdate(examType);
		
		examType.setState(0);
		examType.setUpdateTime(new Date());
		examType.setUpdateUserId(getCurUser().getId());
		update(examType);
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return examTypeDao.getTreeList();
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
			throw new MyException("源试题分类和目标试题分类一致！");
		}

		ExamType source = getEntity(sourceId);
		ExamType target = getEntity(targetId);
		if (target.getParentSub().contains(source.getParentSub())) {
			throw new MyException("父试题分类不能移动到子试题分类下！");
		}

		// 移动试题分类
		source.setParentId(target.getId());
		source.setParentSub(String.format("%s%s_", target.getParentSub(), source.getId()));
		source.setLevel(source.getParentSub().split("_").length - 1);
		update(source);
		
		List<ExamType> subSourceList = examTypeDao.getList(source.getId());
		doMove(source, subSourceList);
	}
		
	private void doMove(ExamType target, List<ExamType> subTargetList) {
		for (ExamType subTarget : subTargetList) {
			subTarget.setParentId(target.getId());
			subTarget.setParentSub(String.format("%s%s_", target.getParentSub(), subTarget.getId()));
			subTarget.setLevel(subTarget.getParentSub().split("_").length - 1);
			update(subTarget);
			
			List<ExamType> subSubTargetList = examTypeDao.getList(subTarget.getId());
			if (ValidateUtil.isValid(subSubTargetList)) {
				doMove(subTarget, subSubTargetList);
			}
		}
	}

	@Override
	public boolean existName(ExamType examType) {
		return examTypeDao.existName(examType.getName(), examType.getId());
	}

	@Override
	public List<ExamType> getList() {
		return examTypeDao.getList();
	}

	@Override
	public void doAuth(Integer id, Integer[] userIds, Integer[] postIds, Integer[] orgIds, boolean syn2Sub) {
		if(syn2Sub){
			List<ExamType> examTypeList = examTypeDao.getList(id);
			for(ExamType examType : examTypeList){
				doAuth(examType.getId(), userIds, postIds, orgIds, syn2Sub);
			}
		}
		
		ExamType entity = getEntity(id);
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
		return examTypeDao.getAuthUserListpage(pageIn);
	}

	@Override
	public PageOut getAuthPostListpage(PageIn pageIn) {
		return examTypeDao.getAuthPostListpage(pageIn);
	}

	@Override
	public PageOut getAuthOrgListpage(PageIn pageIn) {
		return examTypeDao.getAuthOrgListpage(pageIn);
	}
}
