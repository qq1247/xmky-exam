package com.wcpdoc.exam.sys.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.sys.dao.PostDao;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.OrgPost;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.entity.PostRes;
import com.wcpdoc.exam.sys.service.OrgPostService;
import com.wcpdoc.exam.sys.service.PostResService;
import com.wcpdoc.exam.sys.service.PostService;
/**
 * 岗位服务层实现
 * 
 * v1.0 zhanghc 2016-5-19下午9:32:43
 */
@Service
public class PostServiceImpl extends BaseServiceImp<Post> implements PostService{
	@Resource
	private PostDao postDao;
	@Resource
	private OrgPostService orgPostService;
	@Resource
	private PostResService postResService;

	@Override
	@Resource(name = "postDaoImpl")
	public void setDao(BaseDao<Post> dao) {
		super.dao = dao;
	}

	@Override
	public void saveAndUpdate(Post post, Integer orgId) {
		//校验数据有效性
		if(orgId == null){
			throw new RuntimeException("无法获取参数：orgId");
		}
		if(!ValidateUtil.isValid(post.getName())){
			throw new RuntimeException("无法获取参数：post.name");
		}
		if(existName(post)){
			throw new RuntimeException("名称已存在！");
		}
	
		//保存岗位
		postDao.save(post);
		
		//更新和组织机构的关系
		OrgPost orgPost = new OrgPost(orgId, post.getId());
		orgPostService.save(orgPost);
	}
	
	@Override
	public void doResUpdate(Integer id, Integer[] resIds) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(resIds == null){
			throw new RuntimeException("无法获取参数：resIds");
		}
		
		//删除岗位资源关系
		postResService.deleteByPostId(id);
		
		//新建岗位资源关系
		for(Integer resId : resIds){
			PostRes postRes = new PostRes(id, resId);
			postResService.save(postRes);
		}
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除岗位
		for(Integer id : ids){
			Post post = postDao.getEntity(id);
			post.setState(0);
			postDao.update(post);
		}
	}
	
	@Override
	public Org getOrg(Integer id) {
		return postDao.getOrg(id);
	}

	@Override
	public List<Post> getList(Integer orgId) {
		return postDao.getOrgPostList(orgId);
	}

	@Override
	public List<Map<String, Object>> getOrgPostTreeList() {
		return postDao.getOrgPostTreeList();
	}

	@Override
	public List<Map<String, Object>> getResUpdateResTreeList(Integer id) {
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		return postDao.getResUpdateResTreeList(id);
	}

	/**
	 * 名称是否已存在
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * @param post
	 * @return boolean
	 */
	private boolean existName(Post post){
		//校验数据有效性
		if(post == null){
			throw new RuntimeException("无法获取参数：post");
		}
		if(!ValidateUtil.isValid(post.getName())){
			throw new RuntimeException("无法获取参数：post.name");
		}
		
		//如果是添加
		Post post2 = postDao.getPostByName(post.getName());
		if(post2 != null){
			postDao.evict(post2);
		}
		
		if(post.getId() == null){
			if(post2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(post2 != null && !post.getId().equals(post2.getId())){
			return true;
		}
		return false;
	}

	@Override
	public void editAndUpdate(Post post) {
		//校验数据有效性
		if(!ValidateUtil.isValid(post.getName())){
			throw new RuntimeException("无法获取参数：post.name");
		}
		
		if(existName(post)){
			throw new RuntimeException("名称已存在！");
		}
		
		//修改岗位
		postDao.update(post);
	}
}
