package com.wcpdoc.base.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.StringUtil;

/**
 * api文档控制层
 * 
 * v1.0 zhanghc 2021年12月8日上午9:35:58
 */
@Controller
@RequestMapping("/doc")
public class DocController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(DocController.class);
	@Resource
	private UserService userService;
	private static File baseDir = null;
	private static final List<Map<String, Object>> mdFileList = new ArrayList<>();
	static {
		try {
			baseDir = new File(ResourceUtils.getURL("classpath:").getPath()).getParentFile().getParentFile().getParentFile().getParentFile();
		} catch (FileNotFoundException e) {
			log.error("api文档初始化错误：", e);
		}
		
		Map<String, Object> md = new HashMap<>();
		md.put("id", "apiBase");
		md.put("file", "/doc/API接口-基础.md");
		mdFileList.add(md);
		
		md = new HashMap<>();
		md.put("id", "apiExam");
		md.put("file", "/doc/API接口-考试相关.md");
		mdFileList.add(md);
		
		md = new HashMap<>();
		md.put("id", "apiMy");
		md.put("file", "/doc/API接口-我的相关.md");
		mdFileList.add(md);
		
		md = new HashMap<>();
		md.put("id", "apiStatistics");
		md.put("file", "/doc/API接口-统计相关.md");
		mdFileList.add(md);
		
		md = new HashMap<>();
		md.put("id", "src");
		md.put("file", "/src/README.md");
		mdFileList.add(md);
		
		md = new HashMap<>();
		md.put("id", "h5");
		md.put("file", "/h5/README.md");
		mdFileList.add(md);
		
		md = new HashMap<>();
		md.put("id", "exam");
		md.put("file", "/README.md");
		mdFileList.add(md);
	}
	
	
	/**
	 * 到达api文档修改页面
	 * 
	 * v1.0 zhanghc 2021年12月8日上午9:58:27
	 * @return String
	 */
	@RequestMapping("")
	public String toEdit(Model model) {
		try {
			model.addAttribute("mdFileList", mdFileList);
			return "doc/docEdit";
		} catch (Exception e) {
			log.error("到达api文档修改页面错误：", e);
			return "doc/docEdit";
		}
	}
	
	/**
	 * api文档修改
	 * 
	 * v1.0 zhanghc 2021年12月8日上午9:38:31
	 * @param id
	 * @param content
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(String id, String content, String pwd) {
		try {
			User user = userService.getUser("admin");
			if(user == null || !user.getPwd().equals(userService.getEncryptPwd("admin", pwd))) {
				throw new LoginException("密码错误");
			}
			
			for (Map<String, Object> md : mdFileList) {
				if (md.get("id").equals(id)) {
					File mdFile = new File(String.format("%s%s", baseDir.getAbsolutePath(), md.get("file")));
					FileUtils.write(mdFile, content, "utf-8");
					return PageResult.ok();
				}
			}
			return PageResult.err().msg("id不存在");
		} catch (LoginException e) {
			log.error("api文档修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("api文档修改错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * api文档获取
	 * 
	 * v1.0 zhanghc 2021年12月8日上午9:38:31
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(String id) {
		try {
			for (Map<String, Object> md : mdFileList) {
				if (md.get("id").equals(id)) {
					File mdFile = new File(String.format("%s%s", baseDir.getAbsolutePath(), md.get("file")));
					List<String> lastLine = StringUtil.getLastLine(mdFile, 10000);
					String txt = StringUtil.join(lastLine, "\n");
					return PageResultEx.ok().data(txt);
				}
			}
			
			return PageResult.err().msg("id不存在");
		} catch (MyException e) {
			log.error("api文档获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("api文档获取错误：", e);
			return PageResult.err();
		}
	}
}
