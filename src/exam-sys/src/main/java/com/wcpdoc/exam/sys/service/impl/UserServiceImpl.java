package com.wcpdoc.exam.sys.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.EncryptUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.sys.dao.UserDao;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.entity.PostUser;
import com.wcpdoc.exam.sys.entity.User;
import com.wcpdoc.exam.sys.service.OrgService;
import com.wcpdoc.exam.sys.service.PostService;
import com.wcpdoc.exam.sys.service.PostUserService;
import com.wcpdoc.exam.sys.service.UserService;
/**
 * 用户服务层实现
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Service
public class UserServiceImpl extends BaseServiceImp<User> implements UserService{
	
	@Resource
	private UserDao userDao;
	@Resource
	private OrgService orgService;
	@Resource
	private PostService postService;
	@Resource
	private PostUserService postUserService;

	@Override
	@Resource(name = "userDaoImpl")
	public void setDao(BaseDao<User> dao) {
		super.dao = dao;
	}

	@Override
	public void saveAndUpdate(User user) {
		if(!ValidateUtil.isValid(user.getLoginName())){
			throw new RuntimeException("无法获取参数：user.loginName");
		}
		if(user.getLoginName().toLowerCase().equals("sysadmin")){
			throw new RuntimeException("登录名称已存在！");
		}
		if(existLoginName(user)){
			throw new RuntimeException("登录名称已存在！");
		}
		
		userDao.save(user);
	}

	@Override
	public void editAndUpdate(User user) {
		if(!ValidateUtil.isValid(user.getLoginName())){
			throw new RuntimeException("无法获取参数：user.loginName");
		}
		if(user.getLoginName().toLowerCase().equals("sysadmin")){
			throw new RuntimeException("登录名称已存在！");
		}
		if(existLoginName(user)){
			throw new RuntimeException("登录名称已存在！");
		}
		
		userDao.update(user);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除用户
		for(Integer id : ids){
			if(id == 1){
				continue;
			}
			User user = userDao.getEntity(id);
			user.setState(0);
			userDao.update(user);
		}
	}

	@Override
	public List<Map<String, Object>> getOrgTreeList() {
		return orgService.getTreeList();
	}

	@Override
	public Org getOrg(Integer id) {
		return userDao.getOrg(id);
	}
	
	@Override
	public List<Map<String, Object>> getPostUpdatePostTreeList(Integer[] ids) {
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		List<Org> orgList = userDao.getAllOrg(ids);
		if(orgList.size() > 1){
			throw new RuntimeException("请选择同一个组织机构的用户！");
		}
		
		return userDao.getPostUpdateTreeList(orgList.get(0).getId());
	}
	
	@Override
	public void doPostUpdate(Integer[] ids, Integer[] postIds) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		if(!ValidateUtil.isValid(postIds)){
			throw new RuntimeException("无法获取参数：postIds");
		}
		
		List<Org> orgList = userDao.getAllOrg(ids);
		if(orgList.size() > 1){
			throw new RuntimeException("请选择同一个组织机构的用户！");
		}
		
		User user = userDao.getEntity(ids[0]);
		List<Post> postList = postService.getList(user.getOrgId());
		Set<Integer> postIdSet = new HashSet<Integer>();
		for(Post post : postList){
			postIdSet.add(post.getId());
		}
		if(!postIdSet.containsAll(Arrays.asList(postIds))){
			throw new RuntimeException("用户设置了错误的岗位。可能原因：页面已过期，请刷新后重试！");
		}
		
		//完成设置岗位
		for(Integer id : ids){
			postUserService.deleteByUserId(id);
			for(Integer postId : postIds){
				PostUser postUser = new PostUser(postId, id);
				postUserService.save(postUser);
			}
		}
	}

	@Override
	public List<User> getList(Integer orgId) {
		return userDao.getOrgUserList(orgId);
	}

	@Override
	public void doOrgUpdate(Integer[] ids, Integer orgId) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		if(orgId == null){
			throw new RuntimeException("无法获取参数：orgId");
		}
		
		//完成设置组织机构
		for(Integer id : ids){
			User user = userDao.getEntity(id);
			if(user.getOrgId() == orgId){
				continue;
			}
			
			user.setOrgId(orgId);
			userDao.update(user);
			
			postUserService.deleteByUserId(id);
		}
	}
	
	/**
	 * 登录名称是否已存在
	 * v1.0 zhanghc 2016年7月9日下午11:48:30
	 * @param user
	 * @return
	 * boolean
	 */
	private boolean existLoginName(User user){
		if(user == null){
			throw new RuntimeException("无法获取参数：user");
		}
		if(!ValidateUtil.isValid(user.getLoginName())){
			throw new RuntimeException("无法获取参数：user.loginName");
		}
		
		//如果是添加
		User user2 = userDao.getUser(user.getLoginName());
		if(user2 != null){
			userDao.evict(user2);
		}
		
		if(user.getId() == null){
			if(user2 != null){
				return true;
			}
			return false;
		}
		
		//如果是修改
		if(user2 != null && !user.getId().equals(user2.getId())){
			return true;
		}
		return false;
	}

	@Override
	public User getUser(String loginName) {
		return userDao.getUser(loginName);
	}

	@Override
	public Map<Integer, Long> getAuthSum(Integer id) {
		List<Map<String, Object>> list = userDao.getAuthSum(id);
		Map<Integer, Long> authSumMap = new HashMap<Integer, Long>();
		for(Map<String, Object> map : list){
			Integer key = (Integer) map.get("AUTH_POS");
			long value = ((BigDecimal)map.get("SUM_AUTH_CODE")).longValue();
			authSumMap.put(key, value);
		}
		return authSumMap;
	}

	@Override
	public void doPwdUpdate(Integer id, String newPwd) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(newPwd)){
			throw new RuntimeException("无法获取参数：newPwd");
		}
		
		//初始化密码
		User user = getEntity(id);
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
	}

	@Override
	public void doPwdUpdate(Integer id, String oldPwd, String newPwd) {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		if(!ValidateUtil.isValid(oldPwd)){
			throw new RuntimeException("无法获取参数：oldPwd");
		}
		if(!ValidateUtil.isValid(newPwd)){
			throw new RuntimeException("无法获取参数：newPwd");
		}
		
		User user = getEntity(id);
		String oldEncryptPwd = getEncryptPwd(user.getLoginName(), oldPwd);
		if(!user.getPwd().equals(oldEncryptPwd)){
			throw new RuntimeException("原始密码错误！");
		}
		
		//修改密码
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
	}

	@Override
	public String getEncryptPwd(String loginName, String pwd) {
		//校验数据有效性
		if(!ValidateUtil.isValid(loginName)){
			throw new RuntimeException("无法获取参数：loginName");
		}
		if(!ValidateUtil.isValid(pwd)){
			throw new RuntimeException("无法获取参数：pwd");
		}
		
		return EncryptUtil.md52Base64(loginName + pwd);
	}
}
