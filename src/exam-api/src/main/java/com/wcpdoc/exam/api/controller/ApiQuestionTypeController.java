package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 题库控制层
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@RestController
@RequestMapping("/api/questionType")
@Slf4j
public class ApiQuestionTypeController extends BaseController {
	
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 题库列表 
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (!CurLoginUserUtil.isAdmin()) {// 考试用户、阅卷用户没有权限；子管理员看自己；管理员看所有；
				pageIn.addParm("curUserId", getCurUser().getId());
			}
			PageOut pageOut = questionTypeService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("题库列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 题库添加
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/add")
	public PageResult add(QuestionType questionType) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(questionType.getName())) {
				throw new MyException("参数错误：name");
			}
			//if (existName(questionType)) {
			//	throw new MyException("名称已存在");
			//} // 不同的子管理员添加可以重复
			
			// 题库添加
			questionType.setCreateUserId(getCurUser().getId());
			questionType.setUpdateTime(new Date());
			questionType.setUpdateUserId(getCurUser().getId());
			questionTypeService.save(questionType);
			
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
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	public PageResult edit(QuestionType questionType) {
		try {
			//数据校验
			if(!ValidateUtil.isValid(questionType.getName())) {
				throw new MyException("参数错误：name");
			}
			QuestionType entity = questionTypeService.getById(questionType.getId());
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {// 子管理可以改自己创建的题库，管理员可以改所有子管理的题库
				throw new MyException("无操作权限");
			}
			
			// 保存题库
			entity.setName(questionType.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			questionTypeService.updateById(entity);
			
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
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/del")
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
	public PageResult get(Integer id) {
		try {
			QuestionType entity = questionTypeService.getById(id);
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
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
	
//	/**
//	 * 题库合并
//	 * 
//	 * v1.0 zhanghc 2017-05-07 14:56:29
//	 * @param id
//	 * @return pageOut
//	 */
//	@RequestMapping("/move")
//	public PageResult move(Integer sourceId, Integer targetId) {
//		try {
//			questionTypeService.move(sourceId, targetId);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("题库合并错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		}  catch (Exception e) {
//			log.error("题库合并错误：", e);
//			return PageResult.err();
//		}
//	}
	
	/**
	 * 题库清空
	 * 
	 * v1.0 zhanghc 2022年9月15日上午9:28:44
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/clear")
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
