package com.wcpdoc.exam.cache.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 缓存配置
 * 
 * v1.0 zhanghc 2019年5月25日上午9:56:48
 */
@Configuration
public class RedisConf {

	/**
	 * 自定义配置
	 * 
	 * v1.0 zhanghc 2019年4月12日上午10:35:35
	 * 
	 * @param factory
	 * @return RedisTemplate<String,Object>
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jsonSerializer.setObjectMapper(om);// 转换成对象，默认HashMap。

		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(factory);
		template.setStringSerializer(stringSerializer);
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(jsonSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(jsonSerializer);
		return template;
	}
}
