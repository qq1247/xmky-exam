package com.wcpdoc.exam.exam.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.PaperTypeDao;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.service.PaperTypeExService;
import com.wcpdoc.exam.exam.service.PaperTypeService;
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

	@Override
	@Resource(name = "paperTypeDaoImpl")
	public void setDao(BaseDao<PaperType> dao) {
		super.dao = dao;
	}

	@Override
	public void saveAndUpdate(PaperType paperType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(paperType.getName())){
			throw new RuntimeException("无法获取参数：paperType.name");
		}
		if(existName(paperType)){
			throw new RuntimeException("名称已存在！");
		}
				
		//保存试卷分类
		if(paperType.getParentId() == null){
			paperType.setParentId(0);
		}
		paperTypeDao.save(paperType);
		
		//更新父子关系
		PaperType parentPaperType = paperTypeDao.getEntity(paperType.getParentId());
		if(parentPaperType == null){
			paperType.setParentSub("_" + paperType.getId() + "_");
		}else {
			paperType.setParentSub(parentPaperType.getParentSub() + paperType.getId() + "_");
		}
		paperTypeDao.update(paperType);
	}
	
	@Override
	public void editAndUpdate(PaperType paperType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(paperType.getName())){
			throw new RuntimeException("无法获取参数：paperType.name");
		}
		if(existName(paperType)){
			throw new RuntimeException("名称已存在！");
		}
		
		//修改试卷分类
		paperTypeDao.update(paperType);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试卷分类，不包括根试卷分类
		for(Integer id : ids){
			if(id == 1){
				continue;
			}
			
			List<PaperType> paperTypeList = paperTypeDao.getAllSubPaperTypeList(id);
			for(PaperType paperType : paperTypeList){
				if(paperType.getState().equals("0")){
					continue;
				}
				
				paperType.setState(0);
				paperTypeDao.update(paperType);
				
				paperTypeExService.delAndUpdate(paperType);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return paperTypeDao.getTreeList();
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
		
		//移动试卷分类
		paperTypeDao.doMove(sourceId, targetId);
	}

	/**
	 * 名称是否已存在
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param paperType
	 * @return boolean
	 */
	private boolean existName(PaperType paperType){
		//校验数据有效性
		if(paperType == null){
			throw new RuntimeException("无法获取参数：paperType");
		}
		if(!ValidateUtil.isValid(paperType.getName())){
			throw new RuntimeException("无法获取参数：paperType.name");
		}
		
		//如果是添加
		PaperType paperType2 = paperTypeDao.getPaperTypeByName(paperType.getName());
		if(paperType2 != null){
			paperTypeDao.evict(paperType2);
		}
		
		if(paperType.getId() == null){
			if(paperType2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(paperType2 != null && !paperType.getId().equals(paperType2.getId())){
			return true;
		}
		return false;
	}
}
