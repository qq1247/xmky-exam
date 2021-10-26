package com.wcpdoc.api.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.OrgXlsx;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.OrgXlsxService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 组织机构控制层
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Controller
@RequestMapping("/api/org")
public class ApiOrgController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiOrgController.class);

	@Resource
	private OrgService orgService;
	@Resource
	private OrgXlsxService orgXlsxService;

	/**
	 * 组织机构列表
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage(PageIn pageIn, Integer parentId, String name) {
		try {
			return PageResultEx.ok().data(orgService.getListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("组织机构列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Org org, String phone) {
		try {
			orgService.addAndUpdate(org);
			Map<String, Object> data = new HashMap<String, Object>();
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("添加组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加组织机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 修改组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Org org) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(org.getName())) {
				throw new MyException("参数错误：name");
			}
			if (orgService.existName(org)) {
				throw new MyException("名称已存在！");
			}
			if (orgService.existCode(org)) {
				throw new MyException("编码已存在！");
			}

			// 修改组织机构
			Org entity = orgService.getEntity(org.getId());
			entity.setName(org.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setNo(org.getNo());
			entity.setCode(org.getCode());
			orgService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改组织机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 删除组织机构
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
			orgService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除组织机构错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 移动组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/move")
	@ResponseBody
	public PageResult move(Integer sourceId, Integer targetId) {
		try {
			orgService.doMove(sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("移动组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("移动组织机构错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 导入组织机构表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * @return PageResult
	 */
	@RequestMapping("/input")
	@ResponseBody
	public PageResult input(@RequestParam("file") MultipartFile file) {
		try {
			orgXlsxService.inputOrgXlsx(file);
			return PageResultEx.ok();
		} catch (Exception e) {
			log.error("组织机构列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 导出组织机构表
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * @return PageResult
	 */
	@RequestMapping("/export")
	@ResponseBody
	public void export(String ids) {
		ServletOutputStream outputStream = null;
		try {
			List<OrgXlsx> orgXlsx = orgXlsxService.exportOrgXlsx(ids);
			try {
				FileOutputStream os = new FileOutputStream("D:/aorg.xlsx");
				Context context = new Context();
		        //将列表参数放入context中
		        context.putVar("orgXlsxList", orgXlsx);
				InputStream inputStream = this.getClass().getResourceAsStream("/res/orgTemplate.xlsx");
				JxlsHelper jxlsHelper = JxlsHelper.getInstance();
				Transformer transformer = jxlsHelper.createTransformer(inputStream, os);
				JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
				Map<String, Object> funcs = new HashMap<String, Object>();
				evaluator.getJexlEngine().setFunctions(funcs);
				jxlsHelper.processTemplate(context, transformer);
				os.close();

				FileInputStream fileInputStream = new FileInputStream("D:/aorg.xlsx");
				String fileName = new String(("orgExample.xlsx").getBytes("UTF-8"),"ISO-8859-1");
				response.addHeader("Content-Disposition", "attachment;filename" + fileName);
				response.setContentType("application/fprce-download");
				outputStream = response.getOutputStream();
				IOUtils.copy(fileInputStream, outputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			log.error("组织机构列表错误：", e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}
	
	/**
	 * 导出模板
	 * 
	 * v1.0 chenyun 2021年3月4日下午5:41:02
	 * @return PageResult
	 */
	@RequestMapping("/template")
	@ResponseBody
	public void template() {
		OutputStream output = null;
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("/res/orgExample.xlsx");
			String fileName = new String(("orgExample.xlsx").getBytes("UTF-8"),"ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename" + fileName);
			response.setContentType("application/fprce-download");
			output = response.getOutputStream();
			IOUtils.copy(inputStream, output);
		} catch (Exception e) {
			log.error("组织机构导出模板下载附件失败：", e);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	/**
	 * 获取组织机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Org org = orgService.getEntity(id);
			Org parentOrg = null;
			if (org.getParentId() != null) {
				parentOrg = orgService.getEntity(org.getParentId());
			}
			return PageResultEx.ok()
					.addAttr("id", org.getId())
					.addAttr("name", org.getName())
					.addAttr("no", org.getNo())
					.addAttr("parentId", org.getParentId())
					.addAttr("parentName", parentOrg == null ? null : parentOrg.getName());
		} catch (MyException e) {
			log.error("获取组织机构错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取组织机构错误：", e);
			return PageResult.err();
		}
	}
}
