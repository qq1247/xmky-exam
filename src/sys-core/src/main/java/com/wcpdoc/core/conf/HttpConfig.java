package com.wcpdoc.core.conf;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * http配置
 * 
 * v1.0 zhanghc 2025年12月12日下午11:35:44
 */
@Configuration
public class HttpConfig {
	private static final int CONNECTION_TIMEOUT_MS = 5_000; // 5秒
	private static final int RESPONSE_TIMEOUT_MS = 10_000; // 10秒
	private static final int MAX_CONNECTIONS = 100; // 最大连接数
	private static final long PENDING_ACQUIRE_TIMEOUT_MS = 60_000; // 60秒

	@Bean
	public WebClient webClient() {
		ConnectionProvider connectionProvider = ConnectionProvider.builder("xmky-exam-pool")//
				.maxConnections(MAX_CONNECTIONS)//
				.pendingAcquireTimeout(Duration.ofMillis(PENDING_ACQUIRE_TIMEOUT_MS))//
				.maxIdleTime(Duration.ofSeconds(30)).maxLifeTime(Duration.ofMinutes(5)).build();
		HttpClient httpClient = HttpClient.create(connectionProvider)//
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MS)//
				.responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MS))//
				.option(ChannelOption.SO_KEEPALIVE, true);
		return WebClient.builder()//
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
				.clientConnector(new ReactorClientHttpConnector(httpClient))//
				.build();
	}
}