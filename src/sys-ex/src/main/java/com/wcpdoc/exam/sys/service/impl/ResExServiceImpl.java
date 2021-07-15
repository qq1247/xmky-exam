package com.wcpdoc.exam.sys.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.base.service.PostService;
import com.wcpdoc.exam.base.service.ResExService;

/**
 * 资源扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ResExServiceImpl implements ResExService {

	@Resource
	private PostService postService;

	@Override
	public void delAndUpdate(Res res) {
		List<Post> postList = postService.getResPostList(res.getId());
		for (Post post : postList) {
			post.setResIds(post.getResIds().replaceAll(String.format(",%s,", res.getId()), ","));
			if (post.getResIds().isEmpty()) {
				post.setResIds(null);
			}
			postService.update(post);
		}
	}
}
