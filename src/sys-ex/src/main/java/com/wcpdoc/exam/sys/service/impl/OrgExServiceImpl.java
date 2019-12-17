package com.wcpdoc.exam.sys.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.entity.User;
import com.wcpdoc.exam.sys.service.OrgExService;
import com.wcpdoc.exam.sys.service.PostService;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 组织机构扩展服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class OrgExServiceImpl implements OrgExService {
	@Resource
	private PostService postService;
	@Resource
	private UserService userService;

	@Override
	public void delAndUpdate(Org org) {
		// 逻辑删除该组织机构下的岗位
		List<Post> postList = postService.getList(org.getId());
		for (Post post : postList) {
			post.setState(0);
			postService.update(post);
		}

		// 更新该组织机构下的用户所属组织机构为根组织机构
		List<User> userList = userService.getList(org.getId());
		for (User user : userList) {
			user.setOrgId(1);
			userService.update(user);
		}
	}
}
