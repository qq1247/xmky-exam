package com.wcpdoc.exam.exam.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.ExamTypeDao;
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.service.ExamTypeExService;
import com.wcpdoc.exam.exam.service.ExamTypeService;
/**
 * 考试分类服务层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Service
public class ExamTypeServiceImpl extends BaseServiceImp<ExamType> implements ExamTypeService{
	@Resource
	private ExamTypeDao examTypeDao;
	@Resource
	private ExamTypeExService examTypeExService;

	@Override
	@Resource(name = "examTypeDaoImpl")
	public void setDao(BaseDao<ExamType> dao) {
		super.dao = dao;
	}

	@Override
	public void saveAndUpdate(ExamType examType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(examType.getName())){
			throw new RuntimeException("无法获取参数：examType.name");
		}
		if(existName(examType)){
			throw new RuntimeException("名称已存在！");
		}
				
		//保存考试分类
		if(examType.getParentId() == null){
			examType.setParentId(0);
		}
		examTypeDao.save(examType);
		
		//更新父子关系
		ExamType parentExamType = examTypeDao.getEntity(examType.getParentId());
		if(parentExamType == null){
			examType.setParentSub("_" + examType.getId() + "_");
		}else {
			examType.setParentSub(parentExamType.getParentSub() + examType.getId() + "_");
		}
		examTypeDao.update(examType);
	}
	
	@Override
	public void editAndUpdate(ExamType examType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(examType.getName())){
			throw new RuntimeException("无法获取参数：examType.name");
		}
		if(existName(examType)){
			throw new RuntimeException("名称已存在！");
		}
		
		//修改考试分类
		examTypeDao.update(examType);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除考试分类，不包括根考试分类
		for(Integer id : ids){
			if(id == 1){
				continue;
			}
			
			List<ExamType> examTypeList = examTypeDao.getAllSubExamTypeList(id);
			for(ExamType examType : examTypeList){
				if(examType.getState().equals("0")){
					continue;
				}
				
				examType.setState(0);
				examTypeDao.update(examType);
				
				examTypeExService.delAndUpdate(examType);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return examTypeDao.getTreeList();
	}

	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		//校验数据有效性
		if(sourceId == null){
			throw new RuntimeException("无法获取参数：sourceId");
		}
		if(targetId == null){
			throw new RuntimeException("无法获取参数：targetId");
		}
		
		//移动考试分类
		examTypeDao.doMove(sourceId, targetId);
	}

	/**
	 * 名称是否已存在
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param examType
	 * @return boolean
	 */
	private boolean existName(ExamType examType){
		//校验数据有效性
		if(examType == null){
			throw new RuntimeException("无法获取参数：examType");
		}
		if(!ValidateUtil.isValid(examType.getName())){
			throw new RuntimeException("无法获取参数：examType.name");
		}
		
		//如果是添加
		ExamType examType2 = examTypeDao.getExamTypeByName(examType.getName());
		if(examType2 != null){
			examTypeDao.evict(examType2);
		}
		
		if(examType.getId() == null){
			if(examType2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(examType2 != null && !examType.getId().equals(examType2.getId())){
			return true;
		}
		return false;
	}
}
