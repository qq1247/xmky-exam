package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.MyComment;
import com.wcpdoc.exam.core.service.MyCommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 试题评论控制层
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:28
 */
@RestController
@RequestMapping("/api/question-comment")
@RequiredArgsConstructor
@Slf4j
public class ApiQuestionCommentController extends BaseController {
	private final MyCommentService questionCommentService;

	/**
	 * 试题评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			PageOut pageOut = questionCommentService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (!ValidateUtil.isValid(map.get("likeUserIds").toString())) {
					map.put("likeUserIds", new Integer[0]);
					continue;
				}

				String likeUserIds = map.get("likeUserIds").toString();
				String[] likeUserIdStrArr = likeUserIds.substring(1, likeUserIds.length() - 1).split(",");
				Integer[] likeUserIdArr = new Integer[likeUserIdStrArr.length];
				for (int i = 0; i < likeUserIdStrArr.length; i++) {
					likeUserIdArr[i] = Integer.parseInt(likeUserIdStrArr[i]);
				}
				map.put("likeUserIds", likeUserIdArr);
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("试题评论列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 试题评论删除
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			// 试题评论删除
			MyComment exerRmk = questionCommentService.getById(id);
			exerRmk.setState(0);
			exerRmk.setUpdateTime(new Date());
			exerRmk.setUpdateUserId(getCurUser().getId());
			questionCommentService.updateById(exerRmk);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题评论删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试题评论删除错误：", e);
			return PageResult.err();
		}
	}
}
