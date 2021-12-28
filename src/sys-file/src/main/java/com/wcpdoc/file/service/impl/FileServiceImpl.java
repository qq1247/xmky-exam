package com.wcpdoc.file.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.IdUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.cache.FileIdCache;
import com.wcpdoc.file.dao.FileDao;
import com.wcpdoc.file.entity.File;
import com.wcpdoc.file.entity.FileEx;
import com.wcpdoc.file.service.FileExService;
import com.wcpdoc.file.service.FileService;

/**
 * 附件服务层实现
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@Service
public class FileServiceImpl extends BaseServiceImp<File> implements FileService {
	@Resource
	private FileDao fileDao;
	@Resource
	private FileExService fileExService;

	@Override
	@Resource(name = "fileDaoImpl")
	public void setDao(BaseDao<File> dao) {
		super.dao = dao;
	}

	@Override
	public String doTempUpload(MultipartFile[] files, String[] allowTypes, String uuId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(files)) {
			throw new MyException("参数错误：files");
		}
		if (allowTypes == null) {
			throw new MyException("参数错误：allowTypes");
		}

		// 检验上传类型
		List<String> allowTypeList = Arrays.asList(allowTypes);
		for (MultipartFile multipartFile : files) {
			String extName = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
			if (!allowTypeList.contains(extName)) {
				throw new MyException("允许的上传类型为：" + allowTypeList);
			}
		}

		// 创建临时上传目录
		String baseDir = getFileUploadDir();
		String tempPath = "/temp";
		String timeStr = DateUtil.formatDateTime(new Date());
		String ymdPath = String.format("/%s/%s/%s", timeStr.substring(0, 4), timeStr.substring(5, 7), timeStr.substring(8, 10));
		java.io.File tempUploadDir = new java.io.File(baseDir + tempPath + ymdPath);
		if (!tempUploadDir.exists()) {
			tempUploadDir.mkdirs();
		}

		// 保存临时上传附件（如果中间有失败，数据库事务会回滚，部分已上传的临时文件会采用定时任务清除）
		StringBuilder fileIds = new StringBuilder();
		for (MultipartFile multipartFile : files) {
			String fileId = IdUtil.getInstance().nextId() + "";
			java.io.File destFile = new java.io.File(String.format("%s/%s", tempUploadDir.getAbsolutePath(), fileId));
			try {
				multipartFile.transferTo(destFile);
			} catch (Exception e) {
				throw new MyException(e);
			}

			// 保存临时上传附件信息到数据库
			File file = new File();
			file.setName(FilenameUtils.getBaseName(multipartFile.getOriginalFilename()));
			file.setExtName(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
			file.setFileType(multipartFile.getContentType());
			file.setPath(String.format("%s%s/%s", tempPath, ymdPath, fileId));
			file.setIp(request.getRemoteHost());
			file.setState(0);
			file.setUpdateUserId(getCurUser().getId());
			file.setUpdateTime(new Date());
			fileDao.add(file);

			if (fileIds.length() > 0) {
				fileIds.append(",");
			}
			fileIds.append(file.getId());
			
		}
		
		if (ValidateUtil.isValid(uuId)){  //试题FileId校验
			if (!ValidateUtil.isValid(FileIdCache.getFileId(uuId))) {
				FileIdCache.setFileId(uuId, fileIds.toString());
			}else{
				throw new MyException("上传失败");
			}
		}
		
		return fileIds.toString();
	}

	@Override
	public void doUpload(Integer id) {
		// 校验数据有效性
		if (id == null) {
			throw new MyException("参数错误：id");
		}
		File file = fileDao.getEntity(id);
		if (file.getState() == 1) {
			return;
		}

		// 数据库更新附件信息（如果先移动附件成功，而后数据库更新失败，恢复移动附件不好处理）
		String baseDir = getFileUploadDir();
		java.io.File tempFile = new java.io.File(String.format("%s%s", baseDir, file.getPath()));
		String timeStr = DateUtil.formatDateTime(new Date());
		String ymdPath = String.format("/%s/%s/%s", timeStr.substring(0, 4), timeStr.substring(5, 7), timeStr.substring(8, 10));
		file.setState(1);
		file.setPath(String.format("%s/%s", ymdPath, tempFile.getName()));
		file.setIp(request.getRemoteHost());
		file.setUpdateUserId(getCurUser().getId());
		file.setUpdateTime(new Date());

		// 移动临时附件到附件目录
		java.io.File destDir = new java.io.File(baseDir + ymdPath);
		try {
			FileUtils.moveFileToDirectory(tempFile, destDir, true);
		} catch (Exception e) {
			throw new MyException(e);
		}
	}

	@Override
	public FileEx getFileEx(Integer id) {
		if (id == null) {
			throw new MyException("参数错误：id");
		}

		File entity = getEntity(id);
		String baseDir = getFileUploadDir();
		java.io.File file = new java.io.File(String.format("%s/%s", baseDir, entity.getPath()));
		if (!file.exists()) {
			throw new MyException("附件不存在");
		}

		FileEx fileEx = new FileEx();
		fileEx.setEntity(entity);
		fileEx.setFile(file);
		return fileEx;
	}

	@Override
	public List<File> getDelList() {
		return fileDao.getDelList();
	}

	@Override
	public void exportTemplate(String templateName){
		OutputStream output = null;
		InputStream input = null;
		try {
		input = this.getClass().getResourceAsStream("/res/"+templateName);
		String fileName = new String((templateName).getBytes("UTF-8"), "ISO-8859-1");
		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setContentType("application/force-download");
		output = response.getOutputStream();
		IOUtils.copy(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}
	}
	
	@Override
	public String getFileUploadDir() {
		java.io.File dbBakDir = new java.io.File(fileExService.getFileUploadDir());
		if (!dbBakDir.isAbsolute()) {
			dbBakDir = new java.io.File(String.format("%s/%s", System.getProperty("user.dir"), fileExService.getFileUploadDir()));// 如果是相对路径，备份路径为当前war包启动路径+配置文件子目录
		}
		return dbBakDir.getAbsolutePath();
	}

	@Override
	public Integer getFileId(String uuid) {
		String fileId = FileIdCache.getFileId(uuid);
		return Integer.parseInt(fileId);
	}
}
