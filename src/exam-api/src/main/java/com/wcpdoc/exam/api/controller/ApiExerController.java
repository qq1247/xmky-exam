package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionBankService;

import lombok.extern.slf4j.Slf4j;

/**
 * 练习控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@RestController
@RequestMapping("/api/exer")
@Slf4j
public class ApiExerController extends BaseController {

	@Resource
	private ExerService exerService;
	@Resource
	private QuestionBankService questionBankService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private UserService userService;
	@Resource
	private OrgService orgService;

	/**
	 * 练习列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (CurLoginUserUtil.isAdmin()) {// 管理员看所有

			} else if (CurLoginUserUtil.isSubAdmin()) {// 子管理员登录，各看各的创建的
				pageIn.addParm("subAdminUserId", getCurUser().getId());
			} else if (CurLoginUserUtil.isExamUser()) {// 考试用户看（管理或子管理）分配给自己的
				User user = baseCacheService.getUser(getCurUser().getId());
				pageIn.addParm("examUserId", user.getId());
				pageIn.addParm("examOrgId", user.getOrgId());
			} else if (CurLoginUserUtil.isMarkUser()) {// 阅卷用户没有权限
				throw new MyException("无权限");
			}

			PageOut pageOut = exerService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				List<Integer> questionBankIds = StringUtil.toIntList((String) map.remove("questionBankIds"));
				List<Integer> userIds = StringUtil.toIntList((String) map.remove("userIds"));
				List<Integer> orgIds = StringUtil.toIntList((String) map.remove("orgIds"));

				List<QuestionBank> questionBankList = questionBankIds.stream()
						.map(questionBankId -> questionBankService.getById(questionBankId))
						.collect(Collectors.toList());
				map.put("objectiveNum", questionBankList.stream().mapToInt(QuestionBank::getObjectiveNum).sum());
				map.put("subjectiveNum", questionBankList.stream().mapToInt(QuestionBank::getSubjectiveNum).sum());
				map.put("singleNum", questionBankList.stream().mapToInt(QuestionBank::getSingleNum).sum());
				map.put("multipleNum", questionBankList.stream().mapToInt(QuestionBank::getMultipleNum).sum());
				map.put("fillBlankObjNum", questionBankList.stream().mapToInt(QuestionBank::getFillBlankObjNum).sum());
				map.put("fillBlankSubNum", questionBankList.stream().mapToInt(QuestionBank::getFillBlankSubNum).sum());
				map.put("judgeNum", questionBankList.stream().mapToInt(QuestionBank::getJudgeNum).sum());
				map.put("qaObjNum", questionBankList.stream().mapToInt(QuestionBank::getQaObjNum).sum());
				map.put("qaSubNum", questionBankList.stream().mapToInt(QuestionBank::getQaSubNum).sum());

				if (CurLoginUserUtil.isAdmin() || CurLoginUserUtil.isSubAdmin()) {// 管理员和子管理员显示
					map.put("questionBankIds", questionBankIds);
					map.put("userIds", userIds);
					map.put("orgIds", orgIds);
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("练习列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习添加
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param exer
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Exer exer) {
		try {
			exerService.add(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习修改
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param exer
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Exer exer) {
		try {
			exerService.update(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习删除
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			// 数据校验
			Exer exer = exerService.getById(id);
			if (!(CurLoginUserUtil.isSelf(exer.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}
			if (exer.getState().intValue() == 0) {
				throw new MyException("已删除");
			}

			// 删除
			exer.setState(0);
			exer.setUpdateTime(new Date());
			exer.setUpdateUserId(getCurUser().getId());
			exerService.updateById(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习获取
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Exer exer = exerService.getById(id);
			if (!(CurLoginUserUtil.isSelf(exer.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}

			return PageResultEx.ok()//
					.addAttr("id", exer.getId())//
					.addAttr("name", exer.getName())//
					.addAttr("questionBanks", exer.getQuestionBankIds().stream().map(questionBankId -> {
						QuestionBank questionBank = questionBankService.getById(questionBankId);
						Map<String, Object> data = new HashMap<>();
						data.put("id", questionBank.getId());
						data.put("name", questionBank.getName());
						return data;
					}).collect(Collectors.toList()))//
					.addAttr("orgs", orgService.getList().stream().filter(org -> exer.getOrgIds().contains(org.getId()))
							.map(org -> {
								Map<String, Object> data = new HashMap<>();
								data.put("id", org.getId());
								data.put("name", org.getName());
								return data;
							}).collect(Collectors.toList()))//
					.addAttr("users", userService.getList().stream()
							.filter(user -> exer.getUserIds().contains(user.getId())).map(user -> {
								Map<String, Object> data = new HashMap<>();
								data.put("id", user.getId());
								data.put("name", user.getName());
								return data;
							}).collect(Collectors.toList()))//
					.addAttr("state", exer.getState())//
			;
		} catch (MyException e) {
			log.error("练习获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习发布
	 * 
	 * v1.0 zhanghc 2025年6月7日下午4:38:01
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/state")
	public PageResult state(Integer id) {
		try {
			Exer exer = exerService.getById(id);
			if (!(CurLoginUserUtil.isSelf(exer.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}

			exer.setState(exer.getState() == 1 ? 2 : 1);
			exer.setUpdateTime(new Date());
			exer.setUpdateUserId(getCurUser().getId());
			exerService.updateById(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习发布错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习发布错误：", e);
			return PageResult.err();
		}
	}

//	/**
//	 * 练习评论
//	 * 
//	 * v1.0 zhanghc 2025年6月7日下午4:38:01
//	 * 
//	 * @param id
//	 * @return PageResult
//	 */
//	@RequestMapping("/rmk")
//	public PageResult rmk(Integer id) {
//		try {
//			Exer exer = exerService.getById(id);
//			QuestionBank questionBank = questionBankService.getById(exer.getQuestionBankId());
//			if (!(CurLoginUserUtil.isSelf(questionBank.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
//				throw new MyException("无操作权限");
//			}
//
//			exer.setRmkState(exer.getRmkState() == 1 ? 2 : 1);
//			exerService.updateById(exer);
//			return PageResult.ok();
//		} catch (MyException e) {
//			log.error("练习发布错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("练习发布错误：", e);
//			return PageResult.err();
//		}
//	}
}
