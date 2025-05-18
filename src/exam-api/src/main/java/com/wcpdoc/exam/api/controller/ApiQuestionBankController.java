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
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.QuestionBankService;

import lombok.extern.slf4j.Slf4j;

/**
 * 题库控制层
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@RestController
@RequestMapping("/api/questionBank")
@Slf4j
public class ApiQuestionBankController extends BaseController {

	@Resource
	private QuestionBankService questionBankService;

	/**
	 * 题库列表
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (!CurLoginUserUtil.isAdmin()) {// 考试用户、阅卷用户没有权限；子管理员看自己；管理员看所有；
				pageIn.addParm("curUserId", getCurUser().getId());
			}
			PageOut pageOut = questionBankService.getListpage(pageIn);
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
	 * 
	 * @param questionBank
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(QuestionBank questionBank) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(questionBank.getName())) {
				throw new MyException("参数错误：name");
			}
			// if (existName(questionBank)) {
			// throw new MyException("名称已存在");
			// } // 不同的子管理员添加可以重复

			// 题库添加
			questionBank.setObjectiveNum(0);
			questionBank.setSubjectiveNum(0);
			questionBank.setSingleNum(0);
			questionBank.setMultipleNum(0);
			questionBank.setJudgeNum(0);
			questionBank.setFillBlankSubNum(0);
			questionBank.setFillBlankObjNum(0);
			questionBank.setQaSubNum(0);
			questionBank.setQaObjNum(0);
			questionBank.setQuestionNum(0);
			questionBank.setCreateUserId(getCurUser().getId());
			questionBank.setUpdateTime(new Date());
			questionBank.setUpdateUserId(getCurUser().getId());
			questionBankService.save(questionBank);

			return PageResultEx.ok().data(questionBank.getId());
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
	 * 
	 * @param questionBank
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(QuestionBank questionBank) {
		try {
			// 数据校验
			if (!ValidateUtil.isValid(questionBank.getName())) {
				throw new MyException("参数错误：name");
			}
			QuestionBank entity = questionBankService.getById(questionBank.getId());
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {// 子管理可以改自己创建的题库，管理员可以改所有子管理的题库
				throw new MyException("无操作权限");
			}

			// 保存题库
			entity.setName(questionBank.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			questionBankService.updateById(entity);

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
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			questionBankService.delEx(id);
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
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			QuestionBank entity = questionBankService.getById(id);
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}
			return PageResultEx.ok()//
					.addAttr("id", entity.getId())//
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
	 * 题库清空
	 * 
	 * v1.0 zhanghc 2022年9月15日上午9:28:44
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/clear")
	public PageResult clear(Integer id) {
		try {
			questionBankService.clear(id);
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
