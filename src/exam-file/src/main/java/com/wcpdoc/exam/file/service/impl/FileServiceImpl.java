package com.wcpdoc.exam.file.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.PropertiesUtil;
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
	private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Resource
	private FileDao fileDao;

	@Override
	@Resource(name = "fileDaoImpl")
	public void setDao(BaseDao<File> dao) {
		super.dao = dao;
	}
	
	@Override
	public String doTempUpload(MultipartFile[] files, String[] allowTypes, LoginUser user, String ip) {
		//校验数据有效性
		if(files == null){
			throw new RuntimeException("无法获取参数：files");
		}
		if(allowTypes == null){
			throw new RuntimeException("无法获取参数：allowTypes");
		}
		
		//检验上传类型
		List<String> allowTypeList = Arrays.asList(allowTypes);
		for (MultipartFile multipartFile : files) {
			String extName = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
			if(!allowTypeList.contains(extName)){
				throw new RuntimeException("允许的上传类型为：" + allowTypeList);
			}
		}
	
		//创建临时上传目录
		String baseDir = PropertiesUtil.getValue("file.upload.dir");
		String tempPath = java.io.File.separator + "temp";
		String timeStr = DateUtil.getFormatDateTime();
		String ymdPath = java.io.File.separator + timeStr.substring(0,4)
				+ java.io.File.separator + timeStr.substring(5,7)
				+ java.io.File.separator + timeStr.substring(8,10);
		java.io.File tempUploadDir = new java.io.File(baseDir + tempPath + ymdPath);
		if (!tempUploadDir.exists()) {
			tempUploadDir.mkdirs();
		}
		
		//保存临时上传附件（如果中间有失败，数据库事务会回滚，部分已上传的临时文件会采用定时任务清除）
		StringBuilder fileIds = new StringBuilder();
		for (MultipartFile multipartFile : files) {
			String fileId = UUID.randomUUID().toString();
			java.io.File destFile = new java.io.File(tempUploadDir.getAbsolutePath() + java.io.File.separator + fileId);
			try {
				multipartFile.transferTo(destFile);
			} catch (Exception e) {
				throw new RuntimeException("保存临时上传附件时失败：", e);
			}
			
			//保存临时上传附件信息到数据库
			File file = new File();
			file.setName(FilenameUtils.getBaseName(multipartFile.getOriginalFilename()));
			file.setExtName(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
			file.setFileType(multipartFile.getContentType());
			file.setPath(tempPath + ymdPath + java.io.File.separator + fileId);
			file.setIp(ip);
			file.setState(0);
			file.setUpdateUserId(user.getId());
			file.setUpdateTime(new Date());
			fileDao.save(file);
			
			if(fileIds.length() > 0){
				fileIds.append(",");
			}
			fileIds.append(file.getId());
		}
		return fileIds.toString();
	}

	@Override
	public void doUpload(Integer id, LoginUser user, String ip) throws Exception {
		//校验数据有效性
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		File file = fileDao.getEntity(id);
		if(file.getState().equals("1")){
			throw new RuntimeException("临时附件【" + file.getName() + "】已经上传成功！");
		}
		
		//数据库更新附件信息（如果先移动附件成功，而后数据库更新失败，恢复移动附件不好处理）
		String baseDir = PropertiesUtil.getValue("file.upload.dir");
		java.io.File tempFile = new java.io.File(baseDir + file.getPath());
		String timeStr = DateUtil.getFormatDateTime();
		String ymdPath = java.io.File.separator + timeStr.substring(0,4)
				+ java.io.File.separator + timeStr.substring(5,7)
				+ java.io.File.separator + timeStr.substring(8,10);
		file.setState(1);
		file.setPath(ymdPath + java.io.File.separator + tempFile.getName());
		file.setIp(ip);
		file.setUpdateUserId(user.getId());
		file.setUpdateTime(new Date());
		fileDao.flush();
		
		//移动临时附件到附件目录
		java.io.File destDir = new java.io.File(baseDir + ymdPath);
		try {
			FileUtils.moveFileToDirectory(tempFile, destDir, true);
		} catch (Exception e) {
			throw new RuntimeException("移动临时附件【" + file.getName() + "】到附件目录时失败：", e);
		}
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(ids == null){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		for(Integer id : ids){
			//删除附件
			File file = getEntity(id);
			file.setState(0);
		}
	}

	@Override
	public FileEx getEntityEx(Integer id) {
		if(id == null){
			throw new RuntimeException("无法获取参数：id");
		}
		
		File entity = getEntity(id);
		if(entity == null){
			throw new RuntimeException("附件信息不存在");
		}
		
		String baseDir = PropertiesUtil.getValue("file.upload.dir");
		java.io.File file = new java.io.File( baseDir + java.io.File.separator + entity.getPath());
		if(!file.exists()){
			throw new RuntimeException("附件不存在");
		}
		
		FileEx fileEx = new FileEx();
		fileEx.setEntity(entity);
		fileEx.setFile(file);
		return fileEx;
	}

	@Override
	public void clearFile() {
		//清理临时附件
		String baseDir = PropertiesUtil.getValue("file.upload.dir");
		java.io.File dirFile = new java.io.File(baseDir + java.io.File.separator + "temp");
		List<java.io.File> fileList = (List<java.io.File>) FileUtils.listFilesAndDirs(dirFile, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		Date beforTime = DateUtil.getNextDay(new Date(), -7);
		
		for (int i = 0; i < fileList.size() - 1; i++) {
			java.io.File file = fileList.get(fileList.size() - i - 1);
			if((file.isDirectory() && file.listFiles().length == 0) 
					|| (file.isFile() && FileUtils.isFileOlder(file, beforTime))){ //如果是空目录或过期附件
				log.info("清理临时附件：{}", file.getAbsolutePath());
				try {
					file.delete();
				} catch (Exception e) {
					log.error("清理临时附件失败：", e);
				}
			}
		}
		
		//清理标记为删除的附件
		List<File> delFileList = fileDao.getDelList();
		for(File fileEntity : delFileList){
			java.io.File delFile = new java.io.File(baseDir + java.io.File.separator + fileEntity.getPath());
			log.info("清理标记为删除的附件：{}", delFile.getAbsolutePath());
			try {
				delFile.delete();
			} catch (Exception e) {
				log.error("清理标记为删除的附件失败：", e);
			}
			fileDao.del(fileEntity.getId());
		}
	}
}
