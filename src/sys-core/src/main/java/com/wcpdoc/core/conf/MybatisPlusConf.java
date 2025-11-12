package com.wcpdoc.core.conf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.yulichang.autoconfigure.consumer.MybatisPlusJoinPropertiesConsumer;

/**
 * MybatisPlus配置
 * 
 * v1.0 zhanghc 2024年1月5日下午1:42:05
 */
@Configuration
@MapperScan("com.wcpdoc.**.dao")
public class MybatisPlusConf {
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor()); // 防全表更新与删除插件
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));// 分页插件
		return interceptor;
	}

	@Bean
	public ConfigurationCustomizer configurationCustomizer() {// Map下划线自动转驼峰
		return i -> i.setObjectWrapperFactory(new MybatisMapWrapperFactory());
	}

	@Bean
	public MybatisPlusJoinPropertiesConsumer mybatisPlusJoinPropertiesConsumer() {
		return prop -> prop.setBanner(false);// 不显示banner
	}

	@Bean
	public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
		return properties -> {
			GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
			dbConfig.setUpdateStrategy(FieldStrategy.ALWAYS);// 更新数据库时，无论字段值是否为null或空，都强制更新
			GlobalConfig globalConfig = new GlobalConfig();
			globalConfig.setDbConfig(dbConfig);
			globalConfig.setBanner(false);
			properties.setGlobalConfig(globalConfig);
		};
	}
}
