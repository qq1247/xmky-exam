package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamTypeService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 考试分类控制层
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Controller
@RequestMapping("/api/examType")
public class ApiExamTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiExamTypeController.class);
	
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private OrgService orgService;
	
	/**
	 * 试题分类树
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public PageResult treeList() {
		try {
			return PageResultEx.ok().data(examTypeService.getTreeList());
		} catch (Exception e) {
			log.error("试题分类树错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题分类列表 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(examTypeService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题分类列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成添加试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(ExamType examType) {
		try {
			examTypeService.addAndUpdate(examType);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成修改试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(ExamType examType) {
		try {
			//校验数据有效性
			if(!ValidateUtil.isValid(examType.getName())) {
				throw new MyException("参数错误：name");
			}
			if(examTypeService.existName(examType)) {
				throw new MyException("名称已存在！");
			}
			
			//修改试题分类
			ExamType entity = examTypeService.getEntity(examType.getId());
			entity.setName(examType.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User)getCurUser()).getId());
			entity.setNo(examType.getNo());
			examTypeService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			examTypeService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 移动试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/move")
	@ResponseBody
	public PageResult move(Integer sourceId, Integer targetId) {
		try {
			examTypeService.doMove(sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成移动试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成移动试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 权限用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authUserList")
	@ResponseBody
	public PageResult authUserList(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(examTypeService.getAuthUserListpage(pageIn));
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 权限岗位列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authPostList")
	@ResponseBody
	public PageResult authPostList(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(examTypeService.getAuthPostListpage(pageIn));
		} catch (Exception e) {
			log.error("权限岗位列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 权限机构列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/authOrgList")
	@ResponseBody
	public PageResult authOrgList(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(examTypeService.getAuthOrgListpage(pageIn));
		} catch (Exception e) {
			log.error("权限机构列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成添加权限
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userIds
	 * @param postIds
	 * @param orgIds
	 * @param syn2Sub true ： 同步授权到子分类
	 * @return PageResult
	 */
	@RequestMapping("/auth")
	@ResponseBody
	public PageResult auth(Integer id, Integer[] userIds, Integer[] postIds, Integer[] orgIds, boolean syn2Sub) {
		try {
			examTypeService.doAuth(id, userIds, postIds, orgIds, syn2Sub);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加权限用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加权限用户错误：", e);
			return PageResult.err();
		}
	}
}
