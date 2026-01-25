package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.wcpdoc.exam.core.entity.Course;
import com.wcpdoc.exam.core.service.CourseService;
import com.wcpdoc.exam.core.util.CourseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 课程控制层
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@Slf4j
public class ApiCourseController extends BaseController {

	private final CourseService courseService;
	private final BaseCacheService baseCacheService;
	private final UserService userService;
	private final OrgService orgService;

	/**
	 * 课程列表
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param pageIn
	 * @return PageResult
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			if (CurLoginUserUtil.isAdmin()) {// 管理员看所有

			} else if (CurLoginUserUtil.isSubAdmin()) {// 子管理员登录，看自己创建的和有共享权限的
				pageIn.addParm("subAdminUserId", getCurUser().getId());
			} else if (CurLoginUserUtil.isExamUser()) {// 考试用户看（管理或子管理）分配给自己的
				User user = baseCacheService.getUser(getCurUser().getId());
				pageIn.addParm("examUserId", user.getId());
				pageIn.addParm("examOrgId", user.getOrgId());
			}

			PageOut pageOut = courseService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				List<Integer> userIds = StringUtil.toIntList((String) map.remove("userIds"));
				List<Integer> orgIds = StringUtil.toIntList((String) map.remove("orgIds"));
				if (CurLoginUserUtil.isAdmin() || CurLoginUserUtil.isSubAdmin()) {// 管理员和子管理员显示
					map.put("userIds", userIds);
					map.put("orgIds", orgIds);
				}
			}

			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("课程列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程添加
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course
	 * @return PageResult
	 */
	@RequestMapping("/add")
	public PageResult add(Course course) {
		try {
			courseService.add(course);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程修改
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Course course) {
		try {
			courseService.update(course);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程删除
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			courseService.del(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程获取
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Course entity = courseService.getById(id);
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
					|| CourseUtil.hasRead(entity))) {
				throw new MyException("无操作权限");
			}

			return PageResultEx.ok() //
					.addAttr("id", entity.getId()) //
					.addAttr("name", entity.getName()) //
					.addAttr("content", entity.getContent()) //
					.addAttr("state", entity.getState()) //
					.addAttr("shareAuth", entity.getShareAuth()) //
					.addAttr("orgs", orgService.getList().stream()
							.filter(org -> entity.getOrgIds().contains(org.getId())).map(org -> {
								Map<String, Object> data = new HashMap<>();
								data.put("id", org.getId());
								data.put("name", org.getName());
								return data;
							}).collect(Collectors.toList()))//
					.addAttr("users", userService.getList().stream()
							.filter(user -> entity.getUserIds().contains(user.getId())).map(user -> {
								Map<String, Object> data = new HashMap<>();
								data.put("id", user.getId());
								data.put("name", user.getName());
								return data;
							}).collect(Collectors.toList()))//
			;
		} catch (MyException e) {
			log.error("课程获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程获取错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程发布状态
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/state")
	public PageResult state(Integer id) {
		try {
			Course entity = courseService.getById(id);
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
					|| CourseUtil.hasWrite(entity))) {
				throw new MyException("无操作权限");
			}

			entity.setState(entity.getState() == 1 ? 2 : 1);
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			courseService.updateById(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程发布状态错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程发布状态错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 课程共享权限
	 * 
	 * v1.0 zhanghc 2025年12月17日上午10:47:53
	 * 
	 * @param id
	 * @param shareAuth
	 * @return PageResult
	 */
	@RequestMapping("/share")
	public PageResult share(Integer id, Integer shareAuth) {
		try {
			// 数据校验
			Course entity = courseService.getById(id);
			if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
					|| CourseUtil.hasWrite(entity))) {
				throw new MyException("无操作权限");
			}
			if (!(shareAuth >= 1 && shareAuth <= 3)) {
				throw new MyException("参数错误：shareAuth");
			}

			// 权限更新
			entity.setShareAuth(shareAuth);
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			courseService.updateById(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("课程共享权限错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("课程共享权限错误：", e);
			return PageResult.err();
		}
	}
}
