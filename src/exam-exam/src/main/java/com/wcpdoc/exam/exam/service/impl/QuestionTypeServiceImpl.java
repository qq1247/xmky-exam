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
import com.wcpdoc.exam.exam.dao.QuestionTypeDao;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;
import com.wcpdoc.exam.exam.service.QuestionTypeAuthService;
import com.wcpdoc.exam.exam.service.QuestionTypeExService;
import com.wcpdoc.exam.exam.service.QuestionTypeService;
import com.wcpdoc.exam.sys.service.OrgService;
import com.wcpdoc.exam.sys.service.PostService;
/**
 * 试题分类服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeService{
	@Resource
	private QuestionTypeDao questionTypeDao;
	@Resource
	private QuestionTypeExService questionTypeExService;
	@Resource
	private OrgService orgService;
	@Resource
	private QuestionTypeAuthService questionTypeAuthService;
	@Resource
	private PostService postService;

	@Override
	@Resource(name = "questionTypeDaoImpl")
	public void setDao(BaseDao<QuestionType> dao) {
		super.dao = dao;
	}

	@Override
	public void saveAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(questionType.getName())){
			throw new RuntimeException("无法获取参数：questionType.name");
		}
		if(existName(questionType)){
			throw new RuntimeException("名称已存在！");
		}
				
		//保存试题分类
		if(questionType.getParentId() == null){
			questionType.setParentId(0);
		}
		questionTypeDao.save(questionType);
		
		//更新父子关系
		QuestionType parentQuestionType = questionTypeDao.getEntity(questionType.getParentId());
		if(parentQuestionType == null){
			questionType.setParentSub("_" + questionType.getId() + "_");
		}else {
			questionType.setParentSub(parentQuestionType.getParentSub() + questionType.getId() + "_");
		}
		questionTypeDao.update(questionType);
	}
	
	@Override
	public void editAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(questionType.getName())){
			throw new RuntimeException("无法获取参数：questionType.name");
		}
		if(existName(questionType)){
			throw new RuntimeException("名称已存在！");
		}
		
		//修改试题分类
		questionTypeDao.update(questionType);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试题分类，不包括根试题分类
		for(Integer id : ids){
			if(id == 1){
				continue;
			}
			
			List<QuestionType> questionTypeList = questionTypeDao.getAllSubQuestionTypeList(id);
			for(QuestionType questionType : questionTypeList){
				if(questionType.getState().equals("0")){
					continue;
				}
				
				questionType.setState(0);
				questionTypeDao.update(questionType);
				
				questionTypeExService.delAndUpdate(questionType);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return questionTypeDao.getTreeList();
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
		
		//移动试题分类
		questionTypeDao.doMove(sourceId, targetId);
	}

	/**
	 * 名称是否已存在
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType
	 * @return boolean
	 */
	private boolean existName(QuestionType questionType){
		//校验数据有效性
		if(questionType == null){
			throw new RuntimeException("无法获取参数：questionType");
		}
		if(!ValidateUtil.isValid(questionType.getName())){
			throw new RuntimeException("无法获取参数：questionType.name");
		}
		
		//如果是添加
		QuestionType questionType2 = questionTypeDao.getQuestionTypeByName(questionType.getName());
		if(questionType2 != null){
			questionTypeDao.evict(questionType2);
		}
		
		if(questionType.getId() == null){
			if(questionType2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(questionType2 != null && !questionType.getId().equals(questionType2.getId())){
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
		return questionTypeDao.getAuthUserListpage(pageIn);
	}

	@Override
	public PageOut getAuthUserAddList(PageIn pageIn) {
		return questionTypeDao.getAuthUserAddList(pageIn);
	}

	@Override
	public void doAuthUserAdd(Integer id, Integer[] userIds, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new RuntimeException("无法获取参数：userIds");
		}
		
		//添加权限用户
		QuestionTypeAuth auth = questionTypeAuthService.getEntityByQuestionTypeId(id);
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
			questionTypeAuthService.update(auth);
			return;
		}
		
		auth = new QuestionTypeAuth();
		StringBuilder _userIds = new StringBuilder(",");
		for(Integer userId : userIds){
			_userIds.append(userId).append(",");
		}
		auth.setUserIds(_userIds.toString());
		auth.setUpdateTime(new Date());
		auth.setUpdateUserId(user.getId());
		questionTypeAuthService.save(auth);
	}

	@Override
	public void doAuthUserDel(Integer id, Integer[] userIds, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new RuntimeException("无法获取参数：userIds");
		}
		
		QuestionTypeAuth questionTypeAuth = questionTypeAuthService.getEntityByQuestionTypeId(id);
		String _userIds = questionTypeAuth.getUserIds();
		for(Integer userId : userIds){
			_userIds = _userIds.replaceAll("," + userId + ",", ",");//,2,4,5,55,32,32
		}
		if(_userIds.equals(",")){
			_userIds = null;
		}
		
		questionTypeAuth.setUserIds(_userIds);
		questionTypeAuthService.update(questionTypeAuth);
	}

	@Override
	public void doAuthOrgUpdate(Integer id, Integer[] orgIds, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
//		if(!ValidateUtil.isValid(orgIds)){
//			throw new RuntimeException("无法获取参数：orgIds");//全不选就是空。
//		}
		
		//添加权限机构
		QuestionTypeAuth auth = questionTypeAuthService.getEntityByQuestionTypeId(id);
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
			questionTypeAuthService.update(auth);
			return;
		}
		
		auth = new QuestionTypeAuth();
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
		questionTypeAuthService.save(auth);
	}

	@Override
	public QuestionTypeAuth getQuestionTypeAuthEntity(Integer questionTypeAuthId) {
		return questionTypeAuthService.getEntityByQuestionTypeId(questionTypeAuthId);
	}

	@Override
	public List<Map<String, Object>> getOrgPostTreeList() {
		return postService.getOrgPostTreeList();
	}

	@Override
	public void doAuthPostUpdate(Integer id, Integer[] postIds, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
//		if(!ValidateUtil.isValid(postIds)){
//			throw new RuntimeException("无法获取参数：postIds");//全不选就是空。
//		}
		
		//添加权限机构
		QuestionTypeAuth auth = questionTypeAuthService.getEntityByQuestionTypeId(id);
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
			questionTypeAuthService.update(auth);
			return;
		}
		
		auth = new QuestionTypeAuth();
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
		questionTypeAuthService.save(auth);
	}
}
