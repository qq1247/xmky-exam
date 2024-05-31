package com.wcpdoc.core.conf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wcpdoc.core.util.DateUtil;

/**
 * 日期格式化配置
 * 
 * v1.0 zhanghc 2024年1月17日下午4:17:17
 */
@Configuration
public class DateFormatConf implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				return DateUtil.getDateTime(source);
			}
		});
	}

	@Bean
	public LocalDateTimeSerializer localDateTimeDeserializer() {
		return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss")//
				.serializerByType(LocalDateTime.class, localDateTimeDeserializer());
	}
}
