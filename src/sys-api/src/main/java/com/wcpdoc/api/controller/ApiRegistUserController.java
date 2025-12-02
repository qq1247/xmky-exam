package com.wcpdoc.api.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.RegistUser;
import com.wcpdoc.base.service.RegistUserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册用户控制层
 * 
 * v1.0 zhanghc 2025年12月1日下午7:06:04
 */
@RestController
@RequestMapping("/api/regist-user")
@RequiredArgsConstructor
@Slf4j
public class ApiRegistUserController extends BaseController {
	private final RegistUserService registUserService;

	/**
	 * 注册用户列表
	 * 
	 * v1.0 zhanghc 2025年12月1日下午7:06:04
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			PageOut pageOut = registUserService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("注册用户列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 注册用户同意
	 * 
	 * v1.0 zhanghc 2025年12月1日下午7:06:04
	 * 
	 * @param id
	 * @param remark
	 * @return PageResult
	 */
	@RequestMapping("/approve")
	public PageResult approve(Integer id, String remark) {
		try {
			registUserService.approve(id, remark);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("注册用户删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("注册用户删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 注册用户拒绝
	 * 
	 * v1.0 zhanghc 2025年12月1日下午7:06:04
	 * 
	 * @param id
	 * @param remark
	 * @return PageResult
	 */
	@RequestMapping("/reject")
	public PageResult reject(Integer id, String remark) {
		try {
			if (!ValidateUtil.isValid(id)) {
				throw new MyException("参数错误：id");
			}
			if (!ValidateUtil.isValid(remark)) {
				throw new MyException("参数错误：remark");
			}
			RegistUser registUser = registUserService.getById(id);
			if (registUser.getState() == 1 || registUser.getState() == 2) {
				throw new MyException("已审核");
			}

			registUser.setRemark(remark);
			registUser.setState(2);
			registUser.setUpdateUserId(1);
			registUser.setUpdateTime(new Date());
			registUserService.updateById(registUser);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("注册用户删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("注册用户删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 注册用户获取
	 * 
	 * v1.0 zhanghc 2025年12月1日下午7:06:04
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			RegistUser registUser = registUserService.getById(id);
			PageResultEx pageResult = PageResultEx.ok()//
					.addAttr("id", registUser.getId())//
					.addAttr("loginName", registUser.getLoginName())//
					.addAttr("name", registUser.getName())//
					.addAttr("orgId", registUser.getOrgId())//
					.addAttr("registTime", registUser.getRegistTime())//
					.addAttr("state", registUser.getState())//
					.addAttr("remark", registUser.getRemark())//
			;
			return pageResult;
		} catch (MyException e) {
			log.error("注册用户获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("注册用户获取错误：", e);
			return PageResult.err();
		}
	}

}
