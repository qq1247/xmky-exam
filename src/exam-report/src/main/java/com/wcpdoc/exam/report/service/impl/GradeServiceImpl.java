package com.wcpdoc.exam.report.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.report.dao.GradeDao;
import com.wcpdoc.exam.report.service.GradeService;
import com.wcpdoc.exam.report.service.ReportExService;

/**
 * 统计服务层实现
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Service
public class GradeServiceImpl extends BaseServiceImp<Object> implements GradeService {
	
	@Resource
	private GradeDao gradeDao;
	@Resource
	private ReportExService reportExService;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	
	@Override
	public void setDao(BaseDao<Object> dao) {}
	
	@Override
	public Map<String, Object> homeUser() {
		List<Map<String, Object>> homeUserList = gradeDao.homeUser(getCurUser().getId());
		return homeUserList.get(0);
	}

	@Override
	public Map<String, Object> homeSubAdmin() {
		Map<String, Object> homeSubAdmin = new HashMap<String, Object>();
		List<Map<String, Object>> homeSubAdminExam = gradeDao.homeSubAdminExam(getCurUser().getId());
		Map<String, Object> examMap = homeSubAdminExam.get(0);
		homeSubAdmin.put("userId", examMap.get("userId"));
		homeSubAdmin.put("userName", examMap.get("userName"));
		homeSubAdmin.put("orgId", examMap.get("orgId"));
		homeSubAdmin.put("orgName", examMap.get("orgName"));
		homeSubAdmin.put("type", examMap.get("type"));
		homeSubAdmin.put("examNum", examMap.get("examNum"));
		List<Map<String, Object>> paperMap = gradeDao.homeSubAdminPaper(getCurUser().getId());
		homeSubAdmin.put("paperNum", paperMap.get(0).get("paperNum"));
		List<Map<String, Object>> questionMap = gradeDao.homeSubAdminQuestion(getCurUser().getId());
		homeSubAdmin.put("questionNum", questionMap.get(0).get("questionNum"));
		List<Map<String, Object>> markMap = gradeDao.homeSubAdminMark(getCurUser().getId());
		homeSubAdmin.put("markNum", markMap.get(0).get("markNum"));
		return homeSubAdmin;
	}
	
	@Override
	public Map<String, Object> homeAdmin() {
		if (getCurUser().getId().intValue() != 1) {
			throw new MyException("登录用户角色错误！");
		}

		Map<String, Object> homeAdmin = new HashMap<String, Object>();
		List<Map<String, Object>> userMap = gradeDao.homeAdminUser();
		homeAdmin.put("userNum", userMap.get(0).get("userNum"));
		homeAdmin.put("subadminNum", userMap.get(0).get("subadminNum"));
		List<Map<String, Object>> bulletinMap = gradeDao.homeAdminBulletin();
		homeAdmin.put("bulletinNum", bulletinMap.get(0).get("bulletinNum"));
		homeAdmin.put("onUserNum", reportExService.OnlineNum());
		return homeAdmin;
	}
	
	@Override
	public Map<String, Object> serverParam() throws SigarException {
		String osName = System.getProperty("os.name"); 
		String sysName = "linux";
		if (osName.toLowerCase().startsWith("windows")) {
			sysName = "windows";
		}
		Map<String, Object> homeAdmin = new HashMap<String, Object>();
		homeAdmin.put("sys", osName);// 操作系统 
		if(sysName.equals("windows")){
			Sigar sigar = MonitorJna.sigar; // new Sigar(); 报错 --》  no sigar-amd64-winnt.dll in java.library.path
			
	        CpuInfo infos[] = sigar.getCpuInfoList();
	        CpuPerc cpuList[] = sigar.getCpuPercList();
			double combined = 0;
	        for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
	            combined =  combined + Double.valueOf(cpuList[i].getIdle());
	        }
	        homeAdmin.put("cpuType", "--------");//cpu型号
			homeAdmin.put("freeCpu", Math.round(combined));//剩余cpu
			homeAdmin.put("totalCpu", infos.length);//总cpu
			
	        Mem mem = sigar.getMem();
			homeAdmin.put("freeRam", mem.getFree());// 剩余内存
			homeAdmin.put("totalRam", mem.getTotal());// 总内存
			long freeHdd = 0,totalHdd = 0;
	        FileSystem fslist[] = sigar.getFileSystemList();
	        for (int i = 0; i < fslist.length; i++) {
	            FileSystem fs = fslist[i];
	            FileSystemUsage usage = null;
	            try {
					usage = sigar.getFileSystemUsage(fs.getDirName());
				} catch (SigarException e) {
					continue;
				}
	            switch (fs.getType()) {
	            case 0: // TYPE_UNKNOWN ：未知
	                break;
	            case 1: // TYPE_NONE
	                break;
	            case 2: // TYPE_LOCAL_DISK : 本地硬盘
	            	totalHdd = totalHdd + usage.getTotal();
	            	freeHdd = freeHdd + usage.getFree();
	                break;
	            case 3:// TYPE_NETWORK ：网络
	                break;
	            case 4:// TYPE_RAM_DISK ：闪存
	                break;
	            case 5:// TYPE_CDROM ：光驱
	                break;
	            case 6:// TYPE_SWAP ：页面交换
	                break;
	            }
	        }
			homeAdmin.put("freeHdd", freeHdd);//剩余硬盘
			homeAdmin.put("totalHdd", totalHdd);//总硬盘
			
	        Runtime runtime = Runtime.getRuntime();
	        Properties props = System.getProperties();
			homeAdmin.put("freeJvm", runtime.freeMemory());//剩余jvm
			homeAdmin.put("TotalJvm", runtime.totalMemory());//总的jvm
			homeAdmin.put("java", "jdk" + props.getProperty("java.specification.version"));//jdk版本
			homeAdmin.put("web", "---------"); //web服务器

			String sqlString = "";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection c = DriverManager.getConnection(url, username, password);
				DatabaseMetaData dbmd = c.getMetaData();
				sqlString = String.format("%s：%s", dbmd.getDatabaseProductName(), dbmd.getDatabaseProductVersion());
			} catch (Exception e) {
				e.printStackTrace();
			}
			homeAdmin.put("sql", sqlString); //sql版本
			
			Parm parm = ParmCache.get();
			homeAdmin.put("fileBak", parm.getFileUploadDir().replaceAll("\\\\", "/")); //附件保存地址
			homeAdmin.put("dbBak", parm.getDbBakDir().replaceAll("\\\\", "/")); // 数据库备份地址
			return homeAdmin;
		}
		homeAdmin.put("cpuType", "");
		homeAdmin.put("cpuTotal", "");
		homeAdmin.put("ram", "");
		homeAdmin.put("ramTotal", "");
		homeAdmin.put("hdd", "");
		homeAdmin.put("hddTotal", "");
		homeAdmin.put("jvm", "");
		homeAdmin.put("jvmTotal ", "");
		homeAdmin.put("java", "");
		homeAdmin.put("web", "");
		homeAdmin.put("sql", "");
		homeAdmin.put("fileBak", "");
		homeAdmin.put("dbBak", "");
		return homeAdmin;
	}
	
	@Override
	public Map<String, Object> slowLog() {
		Map<String, Object> homeAdmin = new HashMap<String, Object>();
		homeAdmin.put("content", "");
		return homeAdmin;
	}
	
	@Override
	public Map<String, Object> count(Integer examId) {
		List<Map<String, Object>> count = gradeDao.count(examId);
		if (count.size() == 0) {
			return null;
		}
		Map<String, Object> map = gradeDao.count(examId).get(0);
		map.put("examEndTime", map.get("examEndTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime( map.get("examEndTime").toString())));
		map.put("examStartTime", map.get("examStartTime") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("examStartTime").toString())));
		map.put("maxExam", map.get("maxExam") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("maxExam").toString())));
		map.put("minExam", map.get("minExam") == null ? null : DateUtil.formatDateTime(DateUtil.getDateTime(map.get("minExam").toString())));
		return map;
	}	
}