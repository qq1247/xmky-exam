package com.wcpdoc.exam.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcpdoc.exam.core.entity.JwtResult;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 令牌工具类
 * 
 * v1.0 zhanghc 2019年10月17日下午5:11:12
 */
public class JwtUtil {
	private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
	public static final String SECRET_KEY = "9xzR4+K7XpvbCpoIMocXVxS655xxb9S0Qxd5RDU4b7PeSIhYfYdjg9aOMrhjPjwNpjo3XjbthxGduNzWzzw2yw==";

	/**
	 * 生成令牌
	 * 
	 * v1.0 zhanghc 2019年10月24日上午10:38:24
	 * 
	 * @param id 令牌唯一标识
	 * @param subject 主题
	 * @param expTime 过期时间
	 * @param params 自定义参数。
	 * @return String
	 */
	public static String createToken(String id, String subject, Date expTime, Map<String, Object> params) {
		// 校验数据有效性
		if (params != null) {
			if (params.get(Claims.ID) != null) {
				throw new RuntimeException(String.format("params[%s]为jwt关键字，不能使用", Claims.ID));
			}
			if (params.get(Claims.SUBJECT) != null) {
				throw new RuntimeException(String.format("params[%s]为jwt关键字，不能使用", Claims.SUBJECT));
			}
			if (params.get(Claims.EXPIRATION) != null) {
				throw new RuntimeException(String.format("params[%s]为jwt关键字，不能使用", Claims.EXPIRATION));
			}
		}

		// 创建令牌
		JwtBuilder builder = Jwts.builder();
		if (params != null) {
			Map<String, Object> _params = new HashMap<>();
			_params.putAll(params);
			builder.setClaims(_params);
		}
		builder.setId(id);
		builder.setSubject(subject);
		builder.setExpiration(expTime);
		builder.signWith(SignatureAlgorithm.HS512, SECRET_KEY);
		return builder.compact();
	}
	
	/**
	 * 解析令牌
	 * 
	 * v1.0 zhanghc 2019年10月24日上午10:38:32
	 * @param token
	 * @return JwtResult
	 */
	public static JwtResult parse(String token) {
		try {
			if (!ValidateUtil.isValid(token)) {
				return new JwtResult(false, "令牌为空", null);
			}

			Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
			return new JwtResult(true, "令牌有效", claims);
		} catch (ExpiredJwtException e) {
			return new JwtResult(false, "令牌过期", null);
		} catch (Exception e) {
			log.error("解析令牌错误：{}", e.getMessage());
			return new JwtResult(false, "未知错误", null);
		}
	}
	
	public static void main(String[] args) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("personId", 10000001);
		claims.put("personName","张虎成");
		claims.put("improve", true);
		String accessToken = JwtUtil.createToken("1585020716000", "dev", DateUtil.getNextDay(new Date(), 15), claims);
		System.err.println(accessToken);
		
		JwtResult parse = parse(accessToken);
		String id = parse.getClaims().getId();
		System.err.println(id);
		System.err.println(parse.getClaims().get("id"));
	}
}