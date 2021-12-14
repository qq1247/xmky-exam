package com.wcpdoc.exam.report.service.impl;

import java.io.File;
import java.util.Properties;

import org.hyperic.sigar.Sigar;

/**
 * 读取服务器参数问题
 * Sigar sigar = MonitorJna.sigar; // new Sigar(); 报错 --》  no sigar-amd64-winnt.dll in java.library.path
 * 在web下dll文件内
 * 
 * v1.0 chenyun 2021年12月13日下午4:34:24
 */
public class MonitorJna{
	public final static Sigar sigar = initSigar();

	private static Sigar initSigar() {
		try {
			String file = null;
			String osName = System.getProperty("os.name");
			Properties props = System.getProperties();
			String osArch = props.getProperty("os.arch");
			
			if (osName.toLowerCase().startsWith("windows")) {
					file = String.format(".\\dll\\monitor\\x%s\\sigar-x86-winnt", System.getProperty("sun.arch.data.model"));		
				if("amd64".equals(osArch)){
					file = String.format(".\\dll\\monitor\\x%s\\sigar-amd64-winnt", System.getProperty("sun.arch.data.model"));		
				}
			} else if (osName.toLowerCase().startsWith("linux")) {
					file = String.format(".\\dll\\monitor\\x%s\\libsigar-x86-linux", System.getProperty("sun.arch.data.model"));		
				if("x64".equals(osArch)){
					file = String.format(".\\dll\\monitor\\x%s\\libsigar-amd64-linux", System.getProperty("sun.arch.data.model"));		
				}
			}
			
			File classPath = new File(file).getParentFile();
			String path = System.getProperty("java.library.path");
			String sigarLibPath = classPath.getCanonicalPath();
			if (!path.contains(sigarLibPath)) {
				if (osName.toLowerCase().startsWith("windows")) {
					path += ";" + sigarLibPath;
				} else if (osName.toLowerCase().startsWith("linux")) {
					path += ":" + sigarLibPath;
				}
				System.setProperty("java.library.path", path);
			}
			return new Sigar();
		} catch (Exception e) {
			return null;
		}
	}
}
