package com.wcpdoc.plugin.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.pf4j.PluginManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.plugin.api.resolver.PluginRoleResolver;
import com.wcpdoc.plugin.api.service.PluginService;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 插件控制层
 * 
 * v1.0 zhanghc 2025年11月13日下午5:26:03
 */
@RestController
@RequestMapping("/api/plugin")
@RequiredArgsConstructor
@Slf4j
public class ApiPluginController extends BaseController {
	private final PluginManager pluginManager;

	/**
	 * 插件列表
	 * 
	 * v1.0 zhanghc 2025年11月14日下午4:08:52
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/list")
	public PageResult list() {
		try {
			List<Map<String, Object>> resultList = pluginManager.getExtensions(PluginService.class).stream()
					.filter(service -> {
						Set<String> roles = PluginRoleResolver.getAllowedRoles(service);
						return PluginRoleResolver.hasAccess(Set.of(getCurUser().getRole()), roles);
					}).map(pluginService -> {
						Map<String, Object> data = new HashMap<>();
						data.put("name", pluginService.getName());
						data.put("class", pluginService.getClass().getSimpleName());
						return data;
					}).collect(Collectors.toList());
			return PageResultEx.ok().data(resultList);
		} catch (Exception e) {
			log.error("插件列表错误：", e);
			return PageResult.err();
		}

	}

	/**
	 * 插件执行
	 * 
	 * v1.0 zhanghc 2025年11月13日下午5:30:27
	 * 
	 * @param pluginId   插件名称
	 * @param requestMap 请求参数
	 * @return PageResult
	 */
	@RequestMapping("/execute/{pluginId}")
	public Object execute(@PathVariable String pluginId, @RequestParam Map<String, Object> requestMap) {
		try {
			for (PluginService service : pluginManager.getExtensions(PluginService.class)) {
				if (service.getId().equals(pluginId)) {
					Set<String> allowedRoles = PluginRoleResolver.getAllowedRoles(service);
					if (!PluginRoleResolver.hasAccess(Set.of(getCurUser().getRole()), allowedRoles)) {
						throw new MyException("无插件权限：" + pluginId);
					}

					return service.execute(requestMap);
				}
			}
			return PageResultEx.err().msg("插件未找到：" + pluginId);
		} catch (MyException e) {
			log.error("插件运行错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("插件运行错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 插件执行
	 * 
	 * v1.0 zhanghc 2025年11月13日下午5:30:27
	 * 
	 * @param pluginId   插件名称
	 * @param requestMap 请求参数
	 * @return PageResult
	 */
	@RequestMapping("/run/{pluginId}")
	public Flux<String> run(@PathVariable String pluginId, @RequestBody Map<String, Object> requestMap) {
		try {
			for (PluginService service : pluginManager.getExtensions(PluginService.class)) {
				if (service.getId().equals(pluginId)) {
					Set<String> allowedRoles = PluginRoleResolver.getAllowedRoles(service);
					if (!PluginRoleResolver.hasAccess(Set.of(getCurUser().getRole()), allowedRoles)) {
						throw new MyException("无插件权限：" + pluginId);
					}

					return service.run(requestMap);
				}
			}

			throw new MyException("未知插件");
		} catch (MyException e) {
			log.error("插件执行错误：{}", e.getMessage());
			return Flux.just(JSONUtil.toJsonStr(PageResult.err().msg(e.getMessage())));
		} catch (Exception e) {
			log.error("插件执行错误：", e);
			return Flux.just(JSONUtil.toJsonStr(PageResult.err()));
		}
	}
}