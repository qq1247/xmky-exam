package com.wcpdoc.base.job;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.util.DateUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;

/**
 * 版本任务
 * 
 * v1.0 zhanghc 2025年4月8日下午7:21:38
 */
@Component
@RequiredArgsConstructor
public class VerCheckJob {
	private final WebClient webClient;
	private final ParmService parmService;
	private final BaseCacheService baseCacheService;

	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
	public void execute() {
		try {
			// 一分钟内随机时间请求，消除并发
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 60000));

			// 获取最新版本
			parmService.appId();
			Parm parm = baseCacheService.getParm();

			MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
			requestParams.add("appSeries", "小猫开源");
			requestParams.add("appName", "在线考试");
			requestParams.add("appId", parm.getAppId());
			requestParams.add("appVer", parm.getAppVer());
			requestParams.add("osName", System.getProperty("os.name"));
			requestParams.add("osVer", System.getProperty("os.version"));
			requestParams.add("osArch", System.getProperty("os.arch"));

			String responseBody = webClient //
					.post() //
					.uri(parm.getVerhubUrl()) //
					.contentType(MediaType.APPLICATION_FORM_URLENCODED) //
					.body(BodyInserters.fromFormData(requestParams)) //
					.retrieve() //
					.bodyToMono(String.class) //
					.block();

			JSONObject responseJson = JSONUtil.parseObj(responseBody);
			Integer code = responseJson.getInt("code");
			if (code == null || code != 200) {
				return;
			}

			// 更新版本号、版本中心地址等
			JSONObject data = responseJson.getJSONObject("data");
			parmService.app(data.getStr("relVer"), DateUtil.getDate(data.getStr("relTime")), data.getStr("verHubUrl"));
		} catch (Exception e) {
			// 失败也没关系，不要影响程序正常业务
		}
	}
}
