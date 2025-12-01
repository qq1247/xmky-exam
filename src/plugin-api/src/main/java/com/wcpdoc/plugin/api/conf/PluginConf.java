package com.wcpdoc.plugin.api.conf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 插件配置
 * 
 * v1.0 zhanghc 2025年11月14日上午10:29:21
 */
@Configuration
@Slf4j
public class PluginConf {

	@Bean
	public PluginManager pluginManager() {
		DefaultPluginManager pluginManager = new DefaultPluginManager();
		Path pluginPath = pluginManager.getPluginsRoot();
		log.info("插件目录位置: " + pluginPath.toAbsolutePath());
		if (!Files.exists(pluginPath)) {
			try {
				Files.createDirectories(pluginPath);
			} catch (IOException e) {
				log.info("插件目录创建失败：{}", e.getMessage());
			}
		}

		pluginManager.loadPlugins();
		pluginManager.startPlugins();
		return pluginManager;
	}
}
