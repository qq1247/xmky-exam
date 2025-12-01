package com.wcpdoc.plugin.api.resolver;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.wcpdoc.plugin.api.annotation.PluginRoles;
import com.wcpdoc.plugin.api.service.PluginService;

/**
 * 插件角色解析器
 * 
 * v1.0 zhanghc 2025年11月14日下午4:05:01
 */
public final class PluginRoleResolver {
	private static final String EXECUTE_METHOD_NAME = "execute";
	private static final Class<?>[] EXECUTE_PARAM_TYPES = { java.util.Map.class };

	// 缓存：Class → 所需角色集合
	private static final Map<Class<?>, Set<String>> ROLE_CACHE = new ConcurrentHashMap<>();

	private PluginRoleResolver() {
	}

	/**
	 * 
	 * 获取插件 execute 方法所需的角色（仅从方法读取）
	 * 
	 */
	public static Set<String> getAllowedRoles(PluginService service) {
		return ROLE_CACHE.computeIfAbsent(service.getClass(), PluginRoleResolver::loadFromMethod);
	}

	private static Set<String> loadFromMethod(Class<?> pluginClass) {
		try {
			Method executeMethod = pluginClass.getMethod(EXECUTE_METHOD_NAME, EXECUTE_PARAM_TYPES);
			PluginRoles anno = executeMethod.getAnnotation(PluginRoles.class);
			if (anno == null || anno.value().length == 0) {
				return Collections.emptySet(); // 无注解 = 无限制
			}
			return Stream.of(anno.value()).filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toSet());
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("插件实现类必须实现 execute(LoginUser, Map) 方法: " + pluginClass.getName(), e);
		}
	}

	/**
	 * 
	 * 用户是否具备任一所需角色
	 * 
	 */

	public static boolean hasAccess(Set<String> userRoles, Set<String> allowedRoles) {
		if (allowedRoles.isEmpty()) {
			return true; // 无角色要求
		}
		if (userRoles == null || userRoles.isEmpty()) {
			return false;
		}
		return !Collections.disjoint(userRoles, allowedRoles); // 有交集即通过
	}
}