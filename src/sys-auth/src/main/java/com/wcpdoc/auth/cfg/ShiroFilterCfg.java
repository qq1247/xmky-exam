package com.wcpdoc.auth.cfg;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.entity.DemoConfig;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "shiro-filter")
@Data
public class ShiroFilterCfg {
    private Map<String, String> urls;
    private DemoConfig demo;

}