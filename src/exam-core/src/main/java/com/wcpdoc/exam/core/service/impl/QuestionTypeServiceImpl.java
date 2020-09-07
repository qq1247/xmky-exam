package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;
/**
 * 试题分类服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeService {
	@Resource
	private QuestionTypeDao questionTypeDao;
	@Resource
	private QuestionTypeExService questionTypeExService;
	@Resource
	private OrgService orgService;
	@Resource
	private PostService postService;

	@Override
	@Resource(name = "questionTypeDaoImpl")
	public void setDao(BaseDao<QuestionType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(questionType.getName())) {
			throw new MyException("参数错误：name");
		}
		if (questionType.getParentId() == null || questionType.getParentId() == 0) {
			throw new MyException("参数错误：parentId");
		}
		
		if (existName(questionType)) {
			throw new MyException("名称已存在！");
		}
				
		// 添加试题分类
		questionType.setUpdateUserId(getCurUser().getId());
		questionType.setUpdateTime(new Date());
		questionType.setState(1);
		add(questionType);
		
		// 更新父子关系
		QuestionType parentQuestionType = questionTypeDao.getEntity(questionType.getParentId());
		questionType.setParentSub(parentQuestionType.getParentSub() + questionType.getId() + "_");
		questionType.setLevel(questionType.getParentSub().split("_").length - 1);
		update(questionType);
	}

	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		if (id == 1) { //不包括根试题分类
			return;
		}
		List<QuestionType> questionTypeList = questionTypeDao.getList(id);
		if (ValidateUtil.isValid(questionTypeList)) {
			throw new MyException("请先删除子试题分类！");
		}
		
		// 删除试题分类
		QuestionType questionType = getEntity(id);
		questionType.setState(0);
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(getCurUser().getId());
		update(questionType);
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return questionTypeDao.getTreeList();
	}
	
	@Override
	public void doMove(Integer sourceId, Integer targetId) {
		// 校验数据有效性
		if (sourceId == null) {
			throw new MyException("无法获取参数：sourceId");
		}
		if (targetId == null) {
			throw new MyException("无法获取参数：targetId");
		}
		if (sourceId == targetId) {
			throw new MyException("源试题分类和目标试题分类一致！");
		}

		QuestionType source = getEntity(sourceId);
		QuestionType target = getEntity(targetId);
		if (target.getParentSub().contains(source.getParentSub())) {
			throw new MyException("父试题分类不能移动到子试题分类下！");
		}

		// 移动试题分类
		source.setParentId(target.getId());
		source.setParentSub(String.format("%s%s_", target.getParentSub(), source.getId()));
		source.setLevel(source.getParentSub().split("_").length - 1);
		update(source);
		
		List<QuestionType> subSourceList = questionTypeDao.getList(source.getId());
		doMove(source, subSourceList);
	}
		
	private void doMove(QuestionType target, List<QuestionType> subTargetList) {
		for (QuestionType subTarget : subTargetList) {
			subTarget.setParentId(target.getId());
			subTarget.setParentSub(String.format("%s%s_", target.getParentSub(), subTarget.getId()));
			subTarget.setLevel(subTarget.getParentSub().split("_").length - 1);
			update(subTarget);
			
			List<QuestionType> subSubTargetList = questionTypeDao.getList(subTarget.getId());
			if (ValidateUtil.isValid(subSubTargetList)) {
				doMove(subTarget, subSubTargetList);
			}
		}
	}

	@Override
	public boolean existName(QuestionType questionType) {
		return questionTypeDao.existName(questionType.getName(), questionType.getId());
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
	public void doAuthUserAdd(Integer id, Integer[] userIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new MyException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new MyException("无法获取参数：userIds");
		}
		
		//添加权限用户
		if(syn2Sub){
			List<QuestionType> questionTypeList = getList(id);
			for(QuestionType questionType : questionTypeList){
				doAuthUserAdd(questionType.getId(), userIds, syn2Sub, user);
			}
		}
		
		QuestionType questionType = getEntity(id);
		StringBuilder _userIds = new StringBuilder();
		if(ValidateUtil.isValid(questionType.getUserIds())){
			_userIds.append(questionType.getUserIds());
		}else{
			_userIds.append(",");
		}
		for(Integer userId : userIds){
			if(_userIds.toString().contains("," + userId + ",")){
				continue;
			}
			_userIds.append(userId).append(",");
		}
		questionType.setUserIds(_userIds.toString());
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(user.getId());
		update(questionType);
	}

	private List<QuestionType> getList(Integer id) {
		return questionTypeDao.getList(id);
	}

	@Override
	public void doAuthUserDel(Integer id, Integer[] userIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new MyException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(userIds)){
			throw new MyException("无法获取参数：userIds");
		}
		
		if(syn2Sub){
			List<QuestionType> questionTypeList = getList(id);
			for(QuestionType questionType : questionTypeList){
				doAuthUserDel(questionType.getId(), userIds, syn2Sub, user);
			}
		}
		
		QuestionType questionType = getEntity(id);
		if(questionType == null){
			return;
		}
		
		String _userIds = questionType.getUserIds();
		if(!ValidateUtil.isValid(_userIds)){
			return;
		}
		for(Integer userId : userIds){
			_userIds = _userIds.replaceAll("," + userId + ",", ",");//,2,4,5,55,32,32,
		}
		if(_userIds.equals(",")){
			_userIds = null;
		}
		
		questionType.setUserIds(_userIds);
		update(questionType);
	}

	@Override
	public void doAuthOrgUpdate(Integer id, Integer[] orgIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new MyException("无法获取参数：id");
		}
//		if(!ValidateUtil.isValid(orgIds)){
//			throw new MyException("无法获取参数：orgIds");//全不选就是空。
//		}
		
		//添加权限机构
		if(syn2Sub){
			List<QuestionType> questionTypeList = getList(id);
			for(QuestionType questionType : questionTypeList){
				doAuthOrgUpdate(questionType.getId(), orgIds, syn2Sub, user);
			}
		}
		
		QuestionType questionType = getEntity(id);
		StringBuilder _orgIds = new StringBuilder(",");
		for(Integer orgId : orgIds){
			_orgIds.append(orgId).append(",");
		}
		if(_orgIds.toString().equals(",")){
			questionType.setOrgIds(null);
		}else{
			questionType.setOrgIds(_orgIds.toString());
		}
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(user.getId());
		update(questionType);
	}

	@Override
	public List<Map<String, Object>> getOrgPostTreeList() {
		return null;//postService.getOrgPostTreeList();
	}

	@Override
	public void doAuthPostUpdate(Integer id, Integer[] postIds, boolean syn2Sub, LoginUser user) {
		//校验数据有效性
		if(id == null){
			throw new MyException("无法获取参数：id");
		}
//		if(!ValidateUtil.isValid(postIds)){
//			throw new MyException("无法获取参数：postIds");//全不选就是空。
//		}
		
		//添加权限机构
		if(syn2Sub){
			List<QuestionType> questionTypeList = getList(id);
			for(QuestionType questionType : questionTypeList){
				doAuthPostUpdate(questionType.getId(), postIds, syn2Sub, user);
			}
		}
		
		QuestionType questionType = getEntity(id);
		StringBuilder _postIds = new StringBuilder(",");
		for(Integer postId : postIds){
			_postIds.append(postId).append(",");
		}
		if(_postIds.toString().equals(",")){
			questionType.setPostIds(null);
		}else{
			questionType.setPostIds(_postIds.toString());
		}
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(user.getId());
		update(questionType);
	}

	@Override
	public List<QuestionType> getList() {
		return questionTypeDao.getList();
	}
}
