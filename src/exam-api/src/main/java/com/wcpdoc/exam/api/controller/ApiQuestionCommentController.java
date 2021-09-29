package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.QuestionComment;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionCommentService;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.wordFilter.service.SensitiveService;

/**
 * 试题评论控制层
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Controller
@RequestMapping("/api/questionComment")
public class ApiQuestionCommentController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionCommentController.class);

	@Resource
	private QuestionCommentService questionCommentService;
	@Resource
	private SensitiveService sensitiveService;
	
	/**
	 * 试题评论列表
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageOut listpage = questionCommentService.getListpage(new PageIn(request));
			for(Map<String, Object> mapList : listpage.getList()){//敏感词过滤
				mapList.put("content",sensitiveService.replace(mapList.get("content").toString()));
			}
			return PageResultEx.ok().data(listpage);
		} catch (Exception e) {
			log.error("试题评论列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加试题评论
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param questionComment
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(QuestionComment questionComment, int anonymity) {
		try {
			questionCommentService.addAndUpdate(questionComment, anonymity);
			Map<String, Object> data = new HashMap<String, Object>();
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("添加试题评论错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试题评论错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 修改试题评论
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param questionComment
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(QuestionComment questionComment) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(questionComment.getContent())) {
				throw new MyException("参数错误：content");
			}
			
			QuestionComment entity = questionCommentService.getEntity(questionComment.getId());
			entity.setContent(questionComment.getContent());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			questionCommentService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改试题评论错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改试题评论错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 删除试题评论
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			questionCommentService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除试题评论错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除试题评论错误：", e);
			return PageResult.err();
		}
	}
}
