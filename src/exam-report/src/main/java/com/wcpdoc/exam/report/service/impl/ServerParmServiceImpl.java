package com.wcpdoc.exam.report.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.catalina.util.ServerInfo;
import org.hibernate.engine.spi.SessionImplementor;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.exam.report.service.ServerPramService;

/**
 * 服务器参数服务层实现
 * 
 * v1.0 zhanghc 2021年12月14日上午11:15:01
 */
@Service
public class ServerParmServiceImpl implements ServerPramService {
	private static final Logger log = LoggerFactory.getLogger(ServerParmServiceImpl.class);
	private static Sigar sigar = null;
	@Value("${spring.datasource.url}")
	private String dbUrl;
	@Resource
	private EntityManager entityManager;

	/**
	 * 把dll加载到path路径，并初始化对象
	 */
	static {
		try {
			setPath();
			sigar = new Sigar();
		} catch (Exception e) {
			log.error("服务器参数初始化错误：", e);
		}
	}
	
	@Override
	public List<Map<String, Object>> getList() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		if (sigar == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", "获取硬件配置");
			data.put("value", "未知错误");
			result.add(data);
			return result;
		}
		
		Connection connection = null;
		try {
			// 获取操作系统信息
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", "操作系统");
			data.put("value", String.format("%s %s", System.getProperties().getProperty("os.name"), OperatingSystem.getInstance().getArch()));
			result.add(data);
			
			// 获取CPU信息
			CpuInfo[] cpuInfos = sigar.getCpuInfoList();
			CpuInfo cpuInfo = cpuInfos[0];
			
			data = new HashMap<String, Object>();
			data.put("name", "CPU");
			data.put("value", String.format("%sGHZ * %s核", cpuInfo.getMhz() / 1000.0, cpuInfos.length));
			result.add(data);
			
			CpuPerc[] cpuPercs = sigar.getCpuPercList();
			data = new HashMap<String, Object>();
			data.put("name", String.format("CPU使用率"));
			List<String> values = new ArrayList<>();
			for (int i = 0; i < cpuPercs.length; i++) {
				values.add(CpuPerc.format(cpuPercs[i].getCombined()));
				
			}
			data.put("value", values.toString());
			result.add(data);
			
			// 获取内存信息
			Mem mem = sigar.getMem();
			data = new HashMap<String, Object>();
			data.put("name", "总共内存占用");
			data.put("value", String.format("%sM/%sM", mem.getUsed() / 1024 / 1024, mem.getTotal() / 1024 / 1024));
			result.add(data);
			
			Runtime runtime = Runtime.getRuntime();
			data = new HashMap<String, Object>();
			data.put("name", "当前服务占用");
			data.put("value", String.format("%sM/%sM", runtime.freeMemory() / 1024 / 1024, runtime.totalMemory() / 1024 / 1024));
			result.add(data);
			
			// 获取硬盘信息
			FileSystem[] fileSystems = sigar.getFileSystemList();
			data = new HashMap<String, Object>();
			data.put("name", "硬盘占用率");
			values = new ArrayList<>();
			for (FileSystem fileSystem : fileSystems) {
				if (fileSystem.getType() != 2) {// 比如光驱
					continue;
				}
				
				FileSystemUsage fileSystemUsage = sigar.getFileSystemUsage(fileSystem.getDirName());
				values.add(String.format("[%s - %sG/%sG]", fileSystem.getDirName(), fileSystemUsage.getUsed() / 1024 / 1024, fileSystemUsage.getTotal() / 1024 / 1024));
			}
			data.put("value", values);
			result.add(data);
			
			// 软件信息
			data = new HashMap<String, Object>();
			data.put("name", "程序目录");
			data.put("value", System.getProperty("user.dir"));
			result.add(data);
			
			data = new HashMap<String, Object>();
			data.put("name", "JAVA版本");
			data.put("value", System.getProperty("java.version"));
			result.add(data);
			
			data = new HashMap<String, Object>();
			data.put("name", "web容器");
			data.put("value", ServerInfo.getServerInfo());
			result.add(data);
			
			SessionImplementor sessionImp = (SessionImplementor) entityManager.getDelegate();
			connection = sessionImp.connection();
			DatabaseMetaData metaData = connection.getMetaData();
			data = new HashMap<String, Object>();
			data.put("name", "数据库");
			data.put("value", String.format("%s%s%s", metaData.getDatabaseProductName(), File.separator, metaData.getDatabaseProductVersion()));
			result.add(data);
			
			// 其他信息
			data = new HashMap<String, Object>();
			data.put("name", "附件上传目录");
			data.put("value", ParmCache.get().getFileUploadDir());
			result.add(data);
			
			data = new HashMap<String, Object>();
			data.put("name", "数据库备份目录");
			data.put("value", ParmCache.get().getDbBakDir());
			result.add(data);
		} catch (Exception e) {
			log.error("获取参数列表错误：", e);
		} finally {
			//if (connection != null) {// 不要关闭链接，报错，可能是mysql自助管理关闭，未验证 zhanghc 20211214
			//	try {
			//		connection.close();
			//	} catch (SQLException e) {
			//		
			//	}
			//}
		}
       
		return result;
	}

	/**
	 * 设置环境变量
	 * 
	 * v1.0 zhanghc 2021年12月14日上午11:56:33
	 * @throws IOException void
	 */
	private static void setPath() throws IOException {
		String path = String.format("%s%s%s", 
				System.getProperty("java.library.path"), 
				System.getProperty("os.name").toLowerCase().startsWith("windows") ? ";" : ":",
				getDllFile().getParentFile().getCanonicalPath());// getCanonicalPath 返回全路径，不带.
		System.setProperty("java.library.path", path);
		log.info("设置环境变量：{}", path);
	}

	/**
	 * 获取dll路径
	 * 
	 * v1.0 zhanghc 2021年12月14日上午11:56:41
	 * @return File
	 */
	private static File getDllFile() {
		return new File(String.format(".%sdll%ssigar%sx%s%s%s", 
				File.separator, 
				File.separator, 
				File.separator,// linux下\\不识别
				System.getProperty("sun.arch.data.model").equals("32") ? "86" : System.getProperty("sun.arch.data.model"),// xp系统显示的是32
				File.separator, 
				System.getProperty("os.name").toLowerCase().startsWith("windows")  
						? (("x64".equals(System.getProperty("os.arch")) || "amd64".equals(System.getProperty("os.arch")))
								? "sigar-amd64-winnt" : "sigar-x86-winnt") // win32、64位
						: (("x64".equals(System.getProperty("os.arch")) || "amd64".equals(System.getProperty("os.arch")))
								? "libsigar-amd64-linux" : "libsigar-x86-linux")));// linux32、64位
	}
}
