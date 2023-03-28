package com.wcpdoc.exam.api.controller;

import java.util.Date;

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
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 题库控制层
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Controller
@RequestMapping("/api/questionType")
public class ApiQuestionTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionTypeController.class);
	
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 题库列表 
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = questionTypeService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("题库列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 题库添加
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(QuestionType questionType) {
		try {
			//校验数据有效性
			if (!ValidateUtil.isValid(questionType.getName())) {
				throw new MyException("参数错误：name");
			}
			//if (existName(questionType)) {
			//	throw new MyException("名称已存在");
			//} // 不同的子管理员添加可以重复
			
			// 添加题库
			questionType.setUpdateTime(new Date());
			questionType.setUpdateUserId(getCurUser().getId());
			questionTypeService.add(questionType);
			
			return PageResultEx.ok().data(questionType.getId());
		} catch (MyException e) {
			log.error("题库添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 题库修改
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(QuestionType questionType) {
		try {
			//校验数据有效性
			if(!ValidateUtil.isValid(questionType.getName())) {
				throw new MyException("参数错误：name");
			}
			QuestionType entity = questionTypeService.getEntity(questionType.getId());
			if (entity.getUpdateUserId().intValue() != getCurUser().getId()) {
				throw new MyException("无操作权限");
			}
			
			// 保存题库
			entity.setName(questionType.getName());
			entity.setUpdateTime(new Date());
			questionTypeService.update(entity);
			
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库修改错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库删除
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			questionTypeService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库获取
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			QuestionType entity = questionTypeService.getEntity(id);
			if (entity.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无操作权限");
			}
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName());
		} catch (MyException e) {
			log.error("题库获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库获取错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库合并
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/move")
	@ResponseBody
	public PageResult move(Integer sourceId, Integer targetId) {
		try {
			questionTypeService.move(sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库合并错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("题库合并错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库清空
	 * 
	 * v1.0 zhanghc 2022年9月15日上午9:28:44
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/clear")
	@ResponseBody
	public PageResult clear(Integer id) {
		try {
			questionTypeService.clear(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库清空错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库清空错误：", e);
			return PageResult.err();
		}
	}
}
