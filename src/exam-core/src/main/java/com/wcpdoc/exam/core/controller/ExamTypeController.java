package com.wcpdoc.exam.core.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.OrgService;
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
@RequestMapping("/examType")
public class ExamTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ExamTypeController.class);
	
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private OrgService orgService;
	
	/**
	 * 到达试题分类列表页面 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "exam/examType/examTypeList";
		} catch (Exception e) {
			log.error("到达试题分类列表页面错误：", e);
			return "exam/examType/examTypeList";
		}
	}
	
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
			return new PageResultEx(true, "查询成功", examTypeService.getTreeList());
		} catch (Exception e) {
			log.error("试题分类树错误：", e);
			return new PageResult(false, "查询失败");
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
			return new PageResultEx(true, "查询成功", examTypeService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题分类列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达添加试题分类页面 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "exam/examType/examTypeEdit";
		} catch (Exception e) {
			log.error("到达添加试题分类页面错误", e);
			return "exam/examType/examTypeEdit";
		}
	}
	
	/**
	 * 完成添加试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(ExamType examType) {
		try {
			examTypeService.addAndUpdate(examType);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达修改试题分类页面 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			ExamType examType = examTypeService.getEntity(id);
			model.addAttribute("examType", examType);
			ExamType parentExamType = examTypeService.getEntity(examType.getParentId());
			if (parentExamType != null) {
				model.addAttribute("parentExamType", examTypeService.getEntity(examType.getParentId()));
			}
			return "exam/examType/examTypeEdit";
		} catch (Exception e) {
			log.error("到达修改试题分类页面错误", e);
			return "exam/examType/examTypeEdit";
		}
	}
	
	/**
	 * 完成修改试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(ExamType examType) {
		try {
			//校验数据有效性
			if(!ValidateUtil.isValid(examType.getName())){
				throw new MyException("参数错误：name");
			}
			if(examTypeService.existName(examType)){
				throw new MyException("名称已存在！");
			}
			
			//修改试题分类
			ExamType entity = examTypeService.getEntity(examType.getId());
			entity.setName(examType.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User)getCurUser()).getId());
			entity.setNo(examType.getNo());
			examTypeService.update(entity);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 完成删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			examTypeService.delAndUpdate(id);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达移动试题分类页面
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * @return String
	 */
	@RequestMapping("/toMove")
	public String toMove() {
		try {
			return "exam/examType/examTypeMove";
		} catch (Exception e) {
			log.error("到达移动试题分类页面错误", e);
			return "exam/examType/examTypeMove";
		}
	}
	
	/**
	 * 移动试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/doMove")
	@ResponseBody
	public PageResult doMove(Integer sourceId, Integer targetId) {
		try {
			examTypeService.doMove(sourceId, targetId);
			return new PageResult(true, "移动成功");
		} catch (MyException e) {
			log.error("完成移动试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成移动试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达权限列表页面
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 */
	@RequestMapping("/toAuth")
	public String toAuth(Model model, Integer id) {
		try {
			ExamType examType = examTypeService.getEntity(id);
			model.addAttribute("examType", examType);
			return "exam/examType/examTypeAuth";
		} catch (Exception e) {
			log.error("到达权限列表页面错误：", e);
			return "exam/examType/examTypeAuth";
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
			return new PageResultEx(true, "查询成功", examTypeService.getAuthUserListpage(pageIn));
		} catch (Exception e) {
			log.error("权限用户列表错误：", e);
			return new PageResult(false, "查询失败");
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
			return new PageResultEx(true, "查询成功", examTypeService.getAuthPostListpage(pageIn));
		} catch (Exception e) {
			log.error("权限岗位列表错误：", e);
			return new PageResult(false, "查询失败");
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
			return new PageResultEx(true, "查询成功", examTypeService.getAuthOrgListpage(pageIn));
		} catch (Exception e) {
			log.error("权限机构列表错误：", e);
			return new PageResult(false, "查询失败");
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
	@RequestMapping("/doAuth")
	@ResponseBody
	public PageResult doAuth(Integer id, Integer[] userIds, Integer[] postIds, Integer[] orgIds, boolean syn2Sub) {
		try {
			examTypeService.doAuth(id, userIds, postIds, orgIds, syn2Sub);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加权限用户错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加权限用户错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
