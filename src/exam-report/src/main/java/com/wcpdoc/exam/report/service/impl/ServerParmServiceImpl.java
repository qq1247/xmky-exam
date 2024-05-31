package com.wcpdoc.exam.report.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.catalina.util.ServerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.exam.report.service.ServerPramService;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;

/**
 * 服务器参数服务层实现
 * 
 * v1.0 zhanghc 2021年12月14日上午11:15:01
 */
@Service
@Slf4j
public class ServerParmServiceImpl implements ServerPramService {
	@Value("${spring.datasource.url}")
	private String dbUrl;
	private static final SystemInfo SYSTEM_INFO = new SystemInfo();
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public List<Map<String, Object>> getList() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// 获取操作系统信息
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", "操作系统");
			data.put("value", String.format("%s %s", System.getProperties().getProperty("os.name"),
					SYSTEM_INFO.getOperatingSystem().getBitness()));
			result.add(data);

			// 获取CPU信息
			CentralProcessor cpu = SYSTEM_INFO.getHardware().getProcessor();
			data = new HashMap<String, Object>();
			data.put("name", "CPU");
			data.put("value",
					String.format("%sGHZ * %s核",
							BigDecimalUtil.newInstance(cpu.getProcessorIdentifier().getVendorFreq()).div(1000000000, 2)
									.getResult().doubleValue(),
							cpu.getLogicalProcessorCount()));
			result.add(data);

			long[] prevTicks = cpu.getSystemCpuLoadTicks();
			TimeUnit.SECONDS.sleep(1);
			long[] ticks = cpu.getSystemCpuLoadTicks();
			long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
					- prevTicks[CentralProcessor.TickType.NICE.getIndex()];
			long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
					- prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
			long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
					- prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
			long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
					- prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
			long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
					- prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
			long user = ticks[CentralProcessor.TickType.USER.getIndex()]
					- prevTicks[CentralProcessor.TickType.USER.getIndex()];
			long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
					- prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
			long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
					- prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
			long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
			data = new HashMap<String, Object>();
			data.put("name", String.format("CPU使用"));
			data.put("value", new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu)));
			result.add(data);

			// 获取内存信息
			GlobalMemory memory = SYSTEM_INFO.getHardware().getMemory();
			data = new HashMap<String, Object>();
			data.put("name", "内存使用");
			data.put("value", String.format("%sM/%sM", (memory.getTotal() - memory.getAvailable()) / 1024 / 1024,
					memory.getTotal() / 1024 / 1024));
			result.add(data);

			Runtime runtime = Runtime.getRuntime();
			data = new HashMap<String, Object>();
			data.put("name", "程序使用");
			data.put("value", String.format("%sM/%sM", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
					runtime.totalMemory() / 1024 / 1024));
			result.add(data);

			// 获取硬盘信息
			List<OSFileStore> fileStoreList = SYSTEM_INFO.getOperatingSystem().getFileSystem().getFileStores();
			data = new HashMap<String, Object>();
			data.put("name", "硬盘使用");
			List<String> values = new ArrayList<>();
			for (OSFileStore fileStore : fileStoreList) {
				values.add(String.format("[%s %sG/%sG]", fileStore.getMount(),
						(fileStore.getTotalSpace() - fileStore.getUsableSpace()) / 1024 / 1024 / 1024,
						fileStore.getTotalSpace() / 1024 / 1024 / 1024));
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

//			SessionImplementor sessionImp = (SessionImplementor) entityManager.getDelegate();
//			connection = sessionImp.connection();
//			DatabaseMetaData metaData = connection.getMetaData();
//			data = new HashMap<String, Object>();
//			data.put("name", "数据库");
//			data.put("value", String.format("%s%s%s", metaData.getDatabaseProductName(), File.separator, metaData.getDatabaseProductVersion()));
//			result.add(data);

			// 其他信息
			data = new HashMap<String, Object>();
			data.put("name", "附件上传目录");
			data.put("value", baseCacheService.getParm().getFileUploadDir());
			result.add(data);

			data = new HashMap<String, Object>();
			data.put("name", "数据库备份目录");
			data.put("value", baseCacheService.getParm().getDbBakDir());
			result.add(data);
		} catch (Exception e) {
			log.error("获取参数列表错误：", e);
		} finally {
			// if (connection != null) {// 不要关闭链接，报错，可能是mysql自助管理关闭，未验证 zhanghc 20211214
			// try {
			// connection.close();
			// } catch (SQLException e) {
			//
			// }
			// }
		}

		return result;
	}
}
