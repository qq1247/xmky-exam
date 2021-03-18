package com.wcpdoc.exam.base.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.cfg.ShiroCfg.JWTRealm;
import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.ResService;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.EncryptUtil;
import com.wcpdoc.exam.core.util.StringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 用户服务层实现
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Service
public class UserServiceImpl extends BaseServiceImp<User> implements UserService {
	@Resource
	private UserDao userDao;
	@Resource
	private OrgService orgService;
	@Resource
	private PostService postService;
	@Resource
	private ResService resService;
	@Resource
	private JWTRealm jwtRealm;

	@Override
	@Resource(name = "userDaoImpl")
	public void setDao(BaseDao<User> dao) {
		super.dao = dao;
	}
	
	@Override
	public boolean existLoginName(User user) {
		return userDao.existLoginName(user.getLoginName(), user.getId());
	}

	@Override
	public User getUser(String loginName) {
		return userDao.getUser(loginName);
	}

	@Override
	public Map<Integer, Long> getAuth(Integer id) {
		User user = getEntity(id);
		Map<Integer, Long> authMap = new HashMap<>();
		if (!ValidateUtil.isValid(user.getPostIds())) {
			return authMap;
		}
		
		List<Post> postList = userDao.getPostList(id);
		for (Post post : postList) {
			List<Res> resList = postService.getResList(post.getId());
			for (Res res : resList) {
				if (authMap.get(res.getAuthPos()) == null) {
					authMap.put(res.getAuthPos(), 0L);
				}
				
				authMap.put(res.getAuthPos(), authMap.get(res.getAuthPos()) | res.getAuthCode());//或运算
			}
		}
		return authMap;
	}

	@Override
	public void doPwdUpdate(Integer id, String newPwd) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(newPwd)) {
			throw new MyException("参数错误：newPwd");
		}

		// 初始化密码
		User user = getEntity(id);
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
	}

	@Override
	public void doPwdUpdate(String oldPwd, String newPwd) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(oldPwd)) {
			throw new MyException("参数错误：oldPwd");
		}
		if (!ValidateUtil.isValid(newPwd)) {
			throw new MyException("参数错误：newPwd");
		}
		if (getCurUser().getId() != getCurUser().getId().intValue()) {
			throw new MyException("非法操作！");
		}
		User user = getEntity(getCurUser().getId());
		if (getCurUser().getId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("非法操作！");
		}

		String oldEncryptPwd = getEncryptPwd(user.getLoginName(), oldPwd);
		if (!user.getPwd().equals(oldEncryptPwd)) {
			throw new MyException("原始密码错误！");
		}

		// 修改密码
		user.setPwd(getEncryptPwd(user.getLoginName(), newPwd));
		update(user);
	}

	@Override
	public String getEncryptPwd(String loginName, String pwd) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(loginName)) {
			throw new MyException("参数错误：loginName");
		}
		if (!ValidateUtil.isValid(pwd)) {
			throw new MyException("参数错误：pwd");
		}

		return EncryptUtil.md52Base64(loginName + pwd);
	}

	@Override
	public List<User> getList(Integer orgId) {
		return userDao.getList(orgId);
	}

	@Override
	public List<Post> getPostList(Integer id) {
		return userDao.getPostList(id);
	}

	@Override
	public void postUpdate(Integer id, Integer[] postId) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		User user = userDao.getEntity(id);
		if(user == null){
			throw new MyException("参数错误：id");
		}
		
		List<Post> postList = postService.getList();
		Set<Integer> postIdSet = new HashSet<Integer>();
		for (Post post : postList) {
			postIdSet.add(post.getId());
		}
		if (!postIdSet.containsAll(Arrays.asList(postId))) {
			throw new MyException("参数错误：postId");
		}
		
		// 更新岗位
		user.setPostIds(String.format(",%s,", StringUtil.join(postId, ",")));
		user.setUpdateTime(new Date());
		user.setUpdateUserId(getCurUser().getId());
		userDao.update(user);
		
		jwtRealm.getAuthorizationCache().clear();
	}
}
