package com.wcpdoc.exam.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.MyComment;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyCommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 我的评论控制层
 * 
 * v1.0 zhanghc 2025年12月3日上午9:39:51
 */
@RestController
@RequestMapping("/api/my-comment")
@RequiredArgsConstructor
@Slf4j
public class ApiMyCommentController extends BaseController {
	private final ExamCacheService examCacheService;
	private final BaseCacheService baseCacheService;
	private final MyCommentService myCommentService;

	/**
	 * 我的评论列表
	 * 
	 * v1.0 zhanghc 2025年12月3日上午9:43:54
	 * 
	 * @param questionId
	 * @param hot
	 * @return PageResult
	 */
	@RequestMapping("/list")
	public PageResult list(Integer questionId) {
		try {
			List<Map<String, Object>> list = examCacheService.getQuestionCommentList(questionId).stream()
					.map(myComment -> {
						Map<String, Object> data = new HashMap<>();
						data.put("id", myComment.getId());
						data.put("userId", myComment.getUserId());
						data.put("userName", baseCacheService.getUser(myComment.getUserId()).getName());
						data.put("content", myComment.getContent());
						data.put("updateTime", myComment.getUpdateTime());
						data.put("likeNum", myComment.getLikeNum());
						data.put("isLike", myComment.getLikeUserIds().contains(getCurUser().getId()));
						data.put("rootId", myComment.getRootId());
						if (ValidateUtil.isValid(myComment.getReplyUserId())) {
							User replyUser = baseCacheService.getUser(myComment.getReplyUserId());
							data.put("replyUserId", replyUser.getId());
							data.put("replyUserName", replyUser.getName());
						} else {
							data.put("replyUserId", null);
							data.put("replyUserName", null);
						}
						return data;
					}).collect(Collectors.toList());

			return PageResultEx.ok().data(list);
		} catch (Exception e) {
			log.error("我的评论列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的评论添加
	 * 
	 * v1.0 zhanghc 2025年12月3日下午4:01:40
	 * 
	 * @param questionId
	 * @param content
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Integer questionId, String content) {
		try {
			myCommentService.add(questionId, content);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的评论添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的评论添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的评论回复
	 * 
	 * v1.0 zhanghc 2025年12月3日下午8:27:23
	 * 
	 * @param questionId
	 * @param content
	 * @param anon
	 * @return PageResult
	 */
	@RequestMapping("/reply")
	public PageResult reply(Integer questionId, String content, Integer parentId) {
		try {
			myCommentService.reply(questionId, content, parentId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的评论回复错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的评论回复错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 我的评论点赞
	 * 
	 * v1.0 zhanghc 2025年12月3日下午5:51:37
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/like")
	public PageResult like(Integer id) {
		try {
			MyComment myComment = myCommentService.getById(id);
			myCommentService.like(id, myComment.getQuestionId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("我的评论点赞错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的评论点赞错误：", e);
			return PageResult.err();
		}
	}
}
