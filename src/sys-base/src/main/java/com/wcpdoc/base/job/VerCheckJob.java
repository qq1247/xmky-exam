package com.wcpdoc.base.job;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;

/**
 * 版本任务
 * 
 * v1.0 zhanghc 2025年4月8日下午7:21:38
 */
@Component
public class VerCheckJob {

	@Scheduled(cron = "0 0 0 * * ?")
	public void execute() {
		try {
			// 一分钟内随机时间请求，消除并发
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 60000));

			// 获取最新版本
			MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
			requestParams.add("appSeries", "小猫开源");
			requestParams.add("appName", "在线考试");
			requestParams.add("appId", SpringUtil.getBean(BaseCacheService.class).getParm().getAppId());
			requestParams.add("appVer", SpringUtil.getBean(BaseCacheService.class).getParm().getAppVer());
			requestParams.add("osName", System.getProperty("os.name"));
			requestParams.add("osVer", System.getProperty("os.version"));
			requestParams.add("osArch", System.getProperty("os.arch"));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestParams, headers);
			Parm parm = SpringUtil.getBean(BaseCacheService.class).getParm();
			ResponseEntity<String> response = SpringUtil.getBean(RestTemplate.class).postForEntity(parm.getVerhubUrl(),
					requestEntity, String.class);
			String responseBody = response.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(responseBody,
					new TypeReference<Map<String, Object>>() {
					});

			Integer code = (Integer) responseMap.get("code");
			if (code != 200) {
				return;
			}

			// 更新版本号、版本中心地址等
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
			SpringUtil.getBean(ParmService.class).app((String) data.get("relVer"),
					DateUtil.getDate((String) data.get("relTime")), (String) data.get("verHubUrl"));
		} catch (Exception e) {
			// 失败也没关系，无须处理
		}
	}
}
