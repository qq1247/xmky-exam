package com.wcpdoc.exam.exam.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.PaperTypeAuthDao;
import com.wcpdoc.exam.exam.dao.PaperTypeDao;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.entity.PaperTypeAuth;
import com.wcpdoc.exam.exam.service.PaperTypeExService;
import com.wcpdoc.exam.exam.service.PaperTypeService;
import com.wcpdoc.exam.sys.service.OrgService;
import com.wcpdoc.exam.sys.service.PostService;
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
	private PaperTypeAuthDao paperTypeAuthDao;
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
		
		PaperTypeAuth auth = paperTypeAuthDao.getEntity(id);
		if(auth != null){
			StringBuilder _userIds = new StringBuilder();
			if(ValidateUtil.isValid(auth.getUserIds())){
				_userIds.append(auth.getUserIds());
			}else{
				_userIds.append(",");
			}
			for(Integer userId : userIds){
				if(_userIds.toString().contains("," + userId + ",")){
					continue;
				}
				_userIds.append(userId).append(",");
			}
			auth.setUserIds(_userIds.toString());
			auth.setUpdateTime(new Date());
			auth.setUpdateUserId(user.getId());
			paperTypeAuthDao.update(auth);
			return;
		}
		
		auth = new PaperTypeAuth();
		StringBuilder _userIds = new StringBuilder(",");
		for(Integer userId : userIds){
			_userIds.append(userId).append(",");
		}
		auth.setId(id);
		auth.setUserIds(_userIds.toString());
		auth.setUpdateTime(new Date());
		auth.setUpdateUserId(user.getId());
		paperTypeAuthDao.save(auth);
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
		
		PaperTypeAuth paperTypeAuth = paperTypeAuthDao.getEntity(id);
		if(paperTypeAuth == null){
			return;
		}
		
		String _userIds = paperTypeAuth.getUserIds();
		if(!ValidateUtil.isValid(_userIds)){
			return;
		}
		for(Integer userId : userIds){
			_userIds = _userIds.replaceAll("," + userId + ",", ",");//,2,4,5,55,32,32,
		}
		if(_userIds.equals(",")){
			_userIds = null;
		}
		
		paperTypeAuth.setUserIds(_userIds);
		paperTypeAuthDao.update(paperTypeAuth);
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
		
		PaperTypeAuth auth = paperTypeAuthDao.getEntity(id);
		if(auth != null){
			StringBuilder _orgIds = new StringBuilder(",");
			for(Integer orgId : orgIds){
				_orgIds.append(orgId).append(",");
			}
			if(_orgIds.toString().equals(",")){
				auth.setOrgIds(null);
			}else{
				auth.setOrgIds(_orgIds.toString());
			}
			auth.setUpdateTime(new Date());
			auth.setUpdateUserId(user.getId());
			paperTypeAuthDao.update(auth);
			return;
		}
		
		auth = new PaperTypeAuth();
		StringBuilder _orgIds = new StringBuilder(",");
		for(Integer orgId : orgIds){
			_orgIds.append(orgId).append(",");
		}
		if(_orgIds.toString().equals(",")){
			auth.setOrgIds(null);
		}else{
			auth.setOrgIds(_orgIds.toString());
		}
		auth.setId(id);
		auth.setUpdateTime(new Date());
		auth.setUpdateUserId(user.getId());
		paperTypeAuthDao.save(auth);
	}

	@Override
	public PaperTypeAuth getPaperTypeAuth(Integer paperTypeAuthId) {
		return paperTypeAuthDao.getEntity(paperTypeAuthId);
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
		
		PaperTypeAuth auth = paperTypeAuthDao.getEntity(id);
		if(auth != null){
			StringBuilder _postIds = new StringBuilder(",");
			for(Integer postId : postIds){
				_postIds.append(postId).append(",");
			}
			if(_postIds.toString().equals(",")){
				auth.setPostIds(null);
			}else{
				auth.setPostIds(_postIds.toString());
			}
			auth.setUpdateTime(new Date());
			auth.setUpdateUserId(user.getId());
			paperTypeAuthDao.update(auth);
			return;
		}
		
		auth = new PaperTypeAuth();
		StringBuilder _postIds = new StringBuilder(",");
		for(Integer postId : postIds){
			_postIds.append(postId).append(",");
		}
		if(_postIds.toString().equals(",")){
			auth.setPostIds(null);
		}else{
			auth.setPostIds(_postIds.toString());
		}
		auth.setId(id);
		auth.setUpdateTime(new Date());
		auth.setUpdateUserId(user.getId());
		paperTypeAuthDao.save(auth);
	}

	@Override
	public List<PaperType> getList() {
		return paperTypeDao.getList();
	}

	@Override
	public List<PaperTypeAuth> getPaperTypeAuthList() {
		return paperTypeAuthDao.getList();
	}
}
