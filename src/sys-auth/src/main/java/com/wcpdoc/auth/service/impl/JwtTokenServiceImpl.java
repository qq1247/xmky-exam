package com.wcpdoc.auth.service.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.entity.JwtToken;
import com.wcpdoc.auth.service.JwtTokenService;
import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jwt令牌服务层实现
 * 
 * v1.0 zhanghc 2025年11月5日下午6:41:45
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenServiceImpl implements JwtTokenService {
	private static final File SECRET_FILE = new File(
			String.format(".%sconfig%sjwtSecret.txt", File.separator, File.separator));
	private static SecretKey SECRET_KEY = null;
	private static final long ACCESS_TOKEN_EXPIRE = 10 * 60 * 1000; // 访问令牌10分钟过期
	private static final long REFRESH_TOKEN_EXPIRE = 12 * 60 * 60 * 1000; // 刷新令牌12小时过期
	private final CacheManager cacheManager;

	@Override
	public void loadSecret() {
		if (!SECRET_FILE.exists()) {
			try {
				String secret = StringUtil.getRandom(64);
				FileUtils.writeStringToFile(SECRET_FILE, secret, StandardCharsets.UTF_8);
			} catch (Exception e) {
				log.error("生成秘钥文件失败：{}", e.getMessage());
			}
		}

		try {
			String secret = FileUtils.readFileToString(SECRET_FILE, StandardCharsets.UTF_8);
			SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			log.error("生成临时秘钥：{}", e.getMessage());
			String secret = StringUtil.getRandom(64);
			SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}
	}

	@Override
	public String createAccessToken(String loginName, List<String> roles, Integer userId, String userName) {
		return Jwts.builder()//
				.subject(loginName)//
				.claim("userId", userId)//
				.claim("userName", userName)//
				.claim("roles", roles)//
				.issuedAt(new Date())//
				.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))//
				.signWith(SECRET_KEY)//
				.compact();
	}

	@Override
	public String createRefreshToken(String loginName) {
		String refreshToken = Jwts.builder()//
				.subject(loginName)//
				.issuedAt(new Date())//
				.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE))//
				.signWith(SECRET_KEY)//
				.compact();
		return refreshToken;
	}

	@Override
	public boolean isValid(String token) {
		try {
			if (!ValidateUtil.isValid(token)) {
				return false;
			}

			Cache cache = cacheManager.getCache(BaseConstant.TOKEN_BLACKLIST_CACHE);
			Long timestamp = cache.get(token, Long.class);
			if (ValidateUtil.isValid(timestamp)) {
				return false;
			}

			Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JwtToken parse(String token) {
		Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
		return JwtToken.builder().userId(claims.get("userId", Integer.class)).LoginName(claims.getSubject())
				.roles(claims.get("roles", List.class)).build();
	}

	@Override
	public void blacklist(String token) {
		if (!isValid(token)) {
			return;
		}
		Cache cache = cacheManager.getCache(BaseConstant.TOKEN_BLACKLIST_CACHE);
		cache.put(token, System.currentTimeMillis());
	}
}