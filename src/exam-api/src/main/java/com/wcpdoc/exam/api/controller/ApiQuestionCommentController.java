package com.wcpdoc.exam.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.QuestionComment;
import com.wcpdoc.exam.core.service.QuestionCommentService;

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
				mapList.put("content", mapList.get("content").toString());
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
	public PageResult add(QuestionComment questionComment, Integer anonymity) {
		try {
			questionCommentService.addEx(questionComment, anonymity);
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
			//questionCommentService.delEx(id);
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
