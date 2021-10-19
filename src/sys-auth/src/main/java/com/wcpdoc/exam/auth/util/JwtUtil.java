package com.wcpdoc.exam.auth.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import com.wcpdoc.exam.auth.entity.JwtResult;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	private static final String SECRET = SpringUtil.getBean(Environment.class).getProperty("token.secret");
	private static JwtBuilder builder = null; 

	private JwtUtil() {
		
	}
	
	/**
	 * 获取实例
	 * 
	 * v1.0 zhanghc 2021年3月16日下午12:16:40
	 * @return JwtUtil
	 */
	public static JwtUtil getInstance() {
		return new JwtUtil();
	}
	
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
	public JwtUtil createToken(String id, String subject, Date expTime) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(subject)) {
			throw new MyException("参数错误：subject");
		}
		if (expTime == null) {
			throw new MyException("参数错误：expTime");
		}
		if (expTime.getTime() <= new Date().getTime()) {
			throw new MyException("参数错误：expTime必须大于当前时间");
		}

		// 创建令牌
		builder = Jwts.builder();
		builder.setId(id);
		builder.setSubject(subject);
		builder.setExpiration(expTime);
		builder.signWith(SignatureAlgorithm.HS512, SECRET);
		return this;
	}
	
	/**
	 * 添加属性
	 * 
	 * v1.0 zhanghc 2021年3月16日下午12:25:22
	 * @param key
	 * @param value
	 * @return JwtUtil
	 */
	public JwtUtil addAttr(String key, Object value) {
		// 校验数据有效性
		if (Claims.ID.equals(key)) {
			throw new MyException(String.format("key[%s]为jwt关键字", Claims.ID));
		}
		if (Claims.SUBJECT.equals(key)) {
			throw new MyException(String.format("key[%s]为jwt关键字", Claims.SUBJECT));
		}
		if (Claims.EXPIRATION.equals(key)) {
			throw new MyException(String.format("key[%s]为jwt关键字", Claims.EXPIRATION));
		}
		
		// 添加属性
		builder.claim(key, value);
		return this;
	}
	
	/**
	 * 构建字符串
	 * 
	 * v1.0 zhanghc 2021年3月16日下午12:26:17
	 * @return String
	 */
	public String build() {
		return builder.compact();
	}
	
	/**
	 * 解析令牌
	 * 
	 * v1.0 zhanghc 2019年10月24日上午10:38:32
	 * @param token
	 * @return JwtResult
	 */
	public JwtResult parse(String token) {
		try {
			if (!ValidateUtil.isValid(token)) {
				return new JwtResult(HttpStatus.BAD_REQUEST.value(), "令牌为空", null);
			}

			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
			return new JwtResult(HttpStatus.OK.value(), "令牌有效", claims);
		} catch (ExpiredJwtException e) {
			log.info("解析令牌错误：{}", "令牌过期");
			return new JwtResult(HttpStatus.UNAUTHORIZED.value(), "令牌过期", e.getClaims());
		} catch (Exception e) {
			log.error("解析令牌错误：{}", e.getMessage());
			return new JwtResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知错误", null);
		}
	}
	
	public static void main(String[] args) {
		
	}
}