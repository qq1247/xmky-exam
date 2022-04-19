package com.wcpdoc.base.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.entity.UserXlsx;
import com.wcpdoc.base.service.OrgService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.base.service.UserXlsxService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 组织机构表格服务层实现
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
@Service
public class UserXlsxServiceImpl extends BaseServiceImp<Object> implements UserXlsxService {
	private static final Logger log = LoggerFactory.getLogger(UserXlsxServiceImpl.class);
	
	@Resource
	private OrgService orgService;
	
	@Resource
	private UserService userService;
	
	@Override
	@Resource(name = "orgDaoImpl")
	public void setDao(BaseDao<Object> dao) {
		super.dao = dao;
	}

	@Override
	public void inputUserXlsx(MultipartFile file) {
	    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
	    // 参数一：读取的excel文件路径
	    // 参数二：读取sheet的一行，将参数封装在DemoData实体类中
	    // 参数三：读取每一行的时候会执行DemoDataListener监听器
	    try {
			EasyExcel.read(file.getInputStream(), UserXlsx.class, new UserXlsxDataListener()).sheet().doRead();
		} catch (IOException e) {
			log.error("导入用户错误：", e);
			throw new MyException("导入用户错误");
		}
	}
	
	@Override
	public void export(Integer[] ids) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(ids)) {
			throw new MyException("参数错误：ids");
		}
		
		try {
	        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
	        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setCharacterEncoding("utf-8");
	        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
	        String fileName = URLEncoder.encode("用户表", "UTF-8").replaceAll("\\+", "%20");
	        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
	        
	        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
	        // 如果这里想使用03 则 传入excelType参数即可
	        // 参数一：写入excel文件路径
	        // 参数二：写入的数据类型是DemoData
	        // data()方法是写入的数据，结果是List<DemoData>集合
			EasyExcel.write(response.getOutputStream(), UserXlsx.class).sheet("用户表").doWrite(userXlsxExport(ids));
		} catch (IOException e) {
			log.error("导出用户错误：", e);
			throw new MyException("导出用户错误");
		}
	}
	
	private List<UserXlsx> userXlsxExport(Integer[] ids) {
		List<UserXlsx> list = new ArrayList<UserXlsx>();
		for(Integer id : ids){
			User user = userService.getEntity(id);
			if (user == null) {
				throw new MyException("参数错误：ids");
			}
			Org org = orgService.getEntity(user.getOrgId().intValue());
			if (org == null) {
				throw new MyException("参数错误：ids");
			}
			
			UserXlsx userXlsx = new UserXlsx();
			userXlsx.setName(user.getName());
			userXlsx.setLoginName(user.getLoginName());
			userXlsx.setOrgName(org.getName());
			userXlsx.setEmail(user.getEmail());
			userXlsx.setPhone(user.getPhone());
            list.add(userXlsx);
        }
        return list;
    }
	
	@Override
	public void templateUserXlsx() {
		ServletOutputStream outputStream = null;
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("/res/userExample.xlsx");
			response.setContentType("application/x-download");
			String filename = URLEncoder.encode("userExample.xlsx", "UTF-8").replaceAll("\\+", "%20");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			outputStream = response.getOutputStream();
			IOUtils.copy(inputStream, outputStream);
		} catch (Exception e) {
			throw new MyException("读取文件错误");
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}
}
