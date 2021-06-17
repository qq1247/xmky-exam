package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.BulletinBoard;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.BulletinBoardService;
/**
 * 公告栏控制层
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Controller
@RequestMapping("/api/bulletinBoard")
public class ApiBulletinBoardController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiBulletinBoardController.class);
	
	@Resource
	private BulletinBoardService bulletinBoardService;
	
	/**
	 * 公告栏列表
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult listpage() {
		try {
			return PageResultEx.ok().data(bulletinBoardService.getListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("公告栏列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加公告栏
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult add(BulletinBoard bulletinBoard) {
		try {
			bulletinBoard.setUpdateTime(new Date());
			bulletinBoard.setUpdateUserId(getCurUser().getId());
			bulletinBoardService.add(bulletinBoard);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加公告栏错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加公告栏错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改公告栏
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult edit(BulletinBoard bulletinBoard) {
		try {
			BulletinBoard entity = bulletinBoardService.getEntity(bulletinBoard.getId());
			entity.setTitle(bulletinBoard.getTitle());
			entity.setImgs(bulletinBoard.getImgs());
			entity.setVideo(bulletinBoard.getVideo());
			entity.setContent(bulletinBoard.getContent());
			entity.setImgsHeight(bulletinBoard.getImgsHeight());
			entity.setImgsWidth(bulletinBoard.getImgsWidth());
			entity.setUrl(bulletinBoard.getUrl());
			entity.setNo(bulletinBoard.getNo());
			entity.setState(bulletinBoard.getState());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setReadUserIds(bulletinBoard.getReadUserIds());
			entity.setReadOrgIds(bulletinBoard.getReadOrgIds());
			bulletinBoardService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改公告栏错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改公告栏错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除公告栏
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult del(Integer id) {
		try {
			bulletinBoardService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除公告栏错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除公告栏错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加权限
	 * 
	 * v1.0 chenyun 2021年3月2日上午10:18:26
	 * @param id	
	 * @param readUserIds
	 * @param writeUserIds
	 * @param rwState
	 * @return PageResult
	 */
	@RequestMapping("/auth")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult auth(Integer id, String readUserIds, String readOrgIds) {
		try {
			bulletinBoardService.auth(id, readUserIds, readOrgIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加权限用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加权限用户错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取人员列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authUserList")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult userList() {
		try {
			return PageResultEx.ok().data(bulletinBoardService.getUserListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取组织机构列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authOrgList")
	@ResponseBody
	@RequiresRoles(value={"subAdmin"},logical = Logical.OR)
	public PageResult authOrgList(PageIn pageIn, String name, Integer id) {
		try {
			return PageResultEx.ok().data(bulletinBoardService.getOrgListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return PageResult.err();
		}
	}
}
