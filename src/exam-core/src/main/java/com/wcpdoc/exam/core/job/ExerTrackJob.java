package com.wcpdoc.exam.core.job;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wcpdoc.core.lock.ReadWriteLockManager;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.entity.MyExerTrack;
import com.wcpdoc.exam.core.service.MyExerTrackService;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;

/**
 * 练习跟踪任务
 * 
 * v1.0 zhanghc 2025年9月8日下午8:54:47
 */
@Component
@Slf4j
public class ExerTrackJob {
	@Resource
	private MyExerTrackService myExerTrackService;
	@Resource
	private ReadWriteLockManager lockManager;
	@Resource
	private CacheManager cacheManager;

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void execute() {
		try {
			Cache cache = cacheManager.getCache(ExamConstant.EXER_TIME_CACHE);
			@SuppressWarnings("unchecked")
			List<String> keyList = (List<String>) ((Ehcache) cache.getNativeCache()).getKeys();
			for (String key : keyList) {
				Lock writeLock = lockManager.getLock(key).writeLock();
				writeLock.lock();

				try {
					String[] parts = key.split(":");
					Integer exerId = Integer.valueOf(parts[0]);
					Integer userId = Integer.valueOf(parts[1]);
					Integer type = Integer.valueOf(parts[2]);
					Integer ymd = Integer.valueOf(parts[3]);
					@SuppressWarnings("unchecked")
					Set<Integer> minuteSet = cache.get(key, Set.class);

					MyExerTrack myExerTrack = myExerTrackService.getMyExerTrack(exerId, userId, type, ymd);
					if (myExerTrack == null) {
						myExerTrack = new MyExerTrack();
						myExerTrack.setExerId(exerId);
						myExerTrack.setUserId(userId);
						myExerTrack.setType(type);
						myExerTrack.setYmd(ymd);
						myExerTrack.setMinuteTicks(minuteSet.stream().sorted().collect(Collectors.toList()));
						myExerTrack.setMinuteCount(minuteSet.size());
						myExerTrack.setUpdateTime(new Date());
						myExerTrack.setUpdateUserId(1);
						myExerTrackService.save(myExerTrack);
					} else {
						myExerTrack
								.setMinuteTicks(Stream.concat(myExerTrack.getMinuteTicks().stream(), minuteSet.stream())
										.distinct().sorted().collect(Collectors.toList()));
						myExerTrack.setMinuteCount(myExerTrack.getMinuteTicks().size());
						myExerTrack.setUpdateTime(new Date());
						myExerTrack.setUpdateUserId(1);
						myExerTrackService.updateById(myExerTrack);
					}
				} catch (Exception e) {
					log.error("加锁错误：", e);
				} finally {
					cache.evict(key);
					writeLock.unlock();
				}
			}
		} catch (Exception e) {
			log.error("练习跟踪任务失败：", e);
		}
	}
}