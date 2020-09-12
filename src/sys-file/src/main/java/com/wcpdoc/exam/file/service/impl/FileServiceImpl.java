package com.wcpdoc.exam.file.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.IdUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.file.dao.FileDao;
import com.wcpdoc.exam.file.entity.File;
import com.wcpdoc.exam.file.entity.FileEx;
import com.wcpdoc.exam.file.service.FileService;

/**
 * 附件服务层实现
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@Service
public class FileServiceImpl extends BaseServiceImp<File> implements FileService {
	@Resource
	private FileDao fileDao;
	@Value("${file.upload.dir}")
	private String fileUploadDir;

	@Override
	@Resource(name = "fileDaoImpl")
	public void setDao(BaseDao<File> dao) {
		super.dao = dao;
	}

	@Override
	public String doTempUpload(MultipartFile[] files, String[] allowTypes) {
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
		String baseDir = fileUploadDir;
		String tempPath = String.format("%stemp", java.io.File.separator);
		String timeStr = DateUtil.formatDateTime(new Date());
		String ymdPath = String.format("%s%s%s%s%s%s", 
				java.io.File.separator, timeStr.substring(0, 4), 
				java.io.File.separator, timeStr.substring(5, 7), 
				java.io.File.separator, timeStr.substring(8, 10));
		java.io.File tempUploadDir = new java.io.File(baseDir + tempPath + ymdPath);
		if (!tempUploadDir.exists()) {
			tempUploadDir.mkdirs();
		}

		// 保存临时上传附件（如果中间有失败，数据库事务会回滚，部分已上传的临时文件会采用定时任务清除）
		StringBuilder fileIds = new StringBuilder();
		for (MultipartFile multipartFile : files) {
			String fileId = IdUtil.getInstance().nextId() + "";
			java.io.File destFile = new java.io.File(tempUploadDir.getAbsolutePath() + java.io.File.separator + fileId);
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
			file.setPath(tempPath + ymdPath + java.io.File.separator + fileId);
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
		String baseDir = fileUploadDir;
		java.io.File tempFile = new java.io.File(String.format("%s%s", fileUploadDir, file.getPath()));
		String timeStr = DateUtil.formatDateTime(new Date());
		String ymdPath = String.format("%s%s%s%s%s%s", 
				java.io.File.separator, timeStr.substring(0, 4),
				java.io.File.separator, timeStr.substring(5, 7), 
				java.io.File.separator, timeStr.substring(8, 10));
		file.setState(1);
		file.setPath(ymdPath + java.io.File.separator + tempFile.getName());
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
		String baseDir = fileUploadDir;
		java.io.File file = new java.io.File(String.format("%s%s%s", baseDir, java.io.File.separator, entity.getPath()));
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
}
