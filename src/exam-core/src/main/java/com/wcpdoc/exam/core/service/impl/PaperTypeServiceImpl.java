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
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.service.PaperTypeExService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
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
		if(!ValidateUtil.isValid(paperType.getName())){
			throw new RuntimeException("无法获取参数：paperType.name");
		}
		if(existName(paperType)){
			throw new RuntimeException("名称已存在！");
		}
				
		//添加试卷分类
		if(paperType.getParentId() == null){
			paperType.setParentId(0);
		}
		paperTypeDao.add(paperType);
		
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
	 * v1.0 zhanghc 2016-5-24下午14:54:09
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

	@Override
	public List<Map<String, Object>> getOrgTreeList() {
		return orgService.getTreeList();
	}

	@Override
	public PageOut getAuthUserListpage(PageIn pageIn) {
		return paperTypeDao.getAuthUserListpage(pageIn);
	}

	@Override
	public PageOut getAuthUserAddList(PageIn pageIn) {
		return paperTypeDao.getAuthUserAddList(pageIn);
	}

	@Override
	public void doAuthUserAdd(Integer id, Integer[] userIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new RuntimeException("无法获取参数：userIds");
		}
		
		//添加权限用户
		if(syn2Sub){
			List<PaperType> paperTypeList = getList(id);
			for(PaperType paperType : paperTypeList){
				doAuthUserAdd(paperType.getId(), userIds, syn2Sub, user);
			}
		}
		
		PaperType paperType = getEntity(id);
		StringBuilder _userIds = new StringBuilder();
		if(ValidateUtil.isValid(paperType.getUserIds())){
			_userIds.append(paperType.getUserIds());
		}else{
			_userIds.append(",");
		}
		for(Integer userId : userIds){
			if(_userIds.toString().contains("," + userId + ",")){
				continue;
			}
			_userIds.append(userId).append(",");
		}
		paperType.setUserIds(_userIds.toString());
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(user.getId());
		update(paperType);
	}

	private List<PaperType> getList(Integer id) {
		return paperTypeDao.getList(id);
	}

	@Override
	public void doAuthUserDel(Integer id, Integer[] userIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new RuntimeException("无法获取参数：userIds");
		}
		
		if(syn2Sub){
			List<PaperType> paperTypeList = getList(id);
			for(PaperType paperType : paperTypeList){
				doAuthUserDel(paperType.getId(), userIds, syn2Sub, user);
			}
		}
		
		PaperType paperType = getEntity(id);
		if(paperType == null){
			return;
		}
		
		String _userIds = paperType.getUserIds();
		if(!ValidateUtil.isValid(_userIds)){
			return;
		}
		for(Integer userId : userIds){
			_userIds = _userIds.replaceAll("," + userId + ",", ",");//,2,4,5,55,32,32,
		}
		if(_userIds.equals(",")){
			_userIds = null;
		}
		
		paperType.setUserIds(_userIds);
		update(paperType);
	}

	@Override
	public void doAuthOrgUpdate(Integer id, Integer[] orgIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
//		if(!ValidateUtil.isValid(orgIds)){
//			throw new RuntimeException("无法获取参数：orgIds");//全不选就是空。
//		}
		
		//添加权限机构
		if(syn2Sub){
			List<PaperType> paperTypeList = getList(id);
			for(PaperType paperType : paperTypeList){
				doAuthOrgUpdate(paperType.getId(), orgIds, syn2Sub, user);
			}
		}
		
		PaperType paperType = getEntity(id);
		StringBuilder _orgIds = new StringBuilder(",");
		for(Integer orgId : orgIds){
			_orgIds.append(orgId).append(",");
		}
		if(_orgIds.toString().equals(",")){
			paperType.setOrgIds(null);
		}else{
			paperType.setOrgIds(_orgIds.toString());
		}
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(user.getId());
		update(paperType);
	}

	@Override
	public List<Map<String, Object>> getOrgPostTreeList() {
		return postService.getOrgPostTreeList();
	}

	@Override
	public void doAuthPostUpdate(Integer id, Integer[] postIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
//		if(!ValidateUtil.isValid(postIds)){
//			throw new RuntimeException("无法获取参数：postIds");//全不选就是空。
//		}
		
		//添加权限机构
		if(syn2Sub){
			List<PaperType> paperTypeList = getList(id);
			for(PaperType paperType : paperTypeList){
				doAuthPostUpdate(paperType.getId(), postIds, syn2Sub, user);
			}
		}
		
		PaperType paperType = getEntity(id);
		StringBuilder _postIds = new StringBuilder(",");
		for(Integer postId : postIds){
			_postIds.append(postId).append(",");
		}
		if(_postIds.toString().equals(",")){
			paperType.setPostIds(null);
		}else{
			paperType.setPostIds(_postIds.toString());
		}
		paperType.setUpdateTime(new Date());
		paperType.setUpdateUserId(user.getId());
		update(paperType);
	}

	@Override
	public List<PaperType> getList() {
		return paperTypeDao.getList();
	}
}
