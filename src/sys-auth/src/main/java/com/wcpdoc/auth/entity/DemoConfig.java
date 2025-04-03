package com.wcpdoc.auth.entity;

import java.util.Map;

import lombok.Data;

@Data
public class DemoConfig {
	private boolean mode;
	private Map<String, String> urls;
}