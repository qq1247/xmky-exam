package com.wcpdoc.core.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

/**
 * 读写锁
 * 
 * v1.0 zhanghc 2025年9月8日下午10:38:31
 */
@Component
public class ReadWriteLockManager {

	private final ConcurrentMap<Object, ReentrantReadWriteLock> lockMap = new ConcurrentHashMap<>();

	public ReentrantReadWriteLock getLock(Object key) {
		return lockMap.computeIfAbsent(key, k -> new ReentrantReadWriteLock());
	}

	// 可选：定时清理无用锁，避免内存泄漏（key 数量有限，可不清理）
	public void cleanUnusedLocks() {
		
	}
}