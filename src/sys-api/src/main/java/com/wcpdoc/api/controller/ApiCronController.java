package com.wcpdoc.api.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.quartz.entity.Cron;
import com.wcpdoc.quartz.service.CronService;
import com.wcpdoc.quartz.util.QuartzUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务控制层
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
@RestController
@RequestMapping("/api/cron")
@Slf4j
public class ApiCronController extends BaseController {

	@Resource
	private CronService cronService;

	/**
	 * 定时任务列表
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			PageOut pageOut = cronService.getListpage(pageIn);
			List<Map<String, Object>> list = pageOut.getList();
			for (Map<String, Object> map : list) {
				String cron = (String) map.get("cron");
				List<Date> timeList = QuartzUtil.getRecentTriggerTime(cron, 3);
				String[] triggerTimes = new String[3];
				for (int i = 0; i < timeList.size(); i++) {
					triggerTimes[i] = DateUtil.formatDateTime(timeList.get(i));
				}
				map.put("triggerTimes", triggerTimes);
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("定时任务列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 定时任务添加
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param cron
	 * @return PageResult
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/add")
	public PageResult add(Cron cron) {
		try {
			cron.setUpdateUserId(getCurUser().getId());
			cron.setUpdateTime(new Date());
			// 数据校验
			if (!ValidateUtil.isValid(cron.getJobClass())) {
				throw new MyException("参数错误：jobClass");
			}
			if (!ValidateUtil.isValid(cron.getCron())) {
				throw new MyException("参数错误：cron");
			}

			Class<Job> jobClass = null;
			try {
				jobClass = (Class<Job>) Class.forName(cron.getJobClass());
			} catch (ClassNotFoundException e) {
				throw new MyException("无法实例化：" + cron.getJobClass());
			}
			if (!Job.class.isAssignableFrom(jobClass)) {
				throw new MyException("未实现Job接口：" + cron.getJobClass());
			}
			if (!QuartzUtil.validExpression(cron.getCron())) {
				throw new MyException("cron表达式错误：" + cron.getCron());
			}

			// 定时任务添加，默认不启动
			cron.setState(2);
			cronService.save(cron);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("定时任务添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("定时任务添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 定时任务修改
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param cron
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	public PageResult edit(Cron cron) {
		try {
			cronService.updateEx(cron);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("定时任务修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("定时任务修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 定时任务删除
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/del")
	public PageResult del(Integer id) {
		try {
			cronService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("定时任务删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("定时任务删除错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 任务启动
	 * 
	 * v1.0 zhanghc 2019年9月12日下午5:30:34
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/startTask")
	public PageResult startTask(Integer id) {
		try {
			cronService.startTask(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("任务启动错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("任务启动错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 任务停止
	 * 
	 * v1.0 zhanghc 2019年9月12日下午5:30:34
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/stopTask")
	public PageResult stopTask(Integer id) {
		try {
			cronService.stopTask(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("任务停止错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("任务停止错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 执行一次
	 * 
	 * v1.0 zhanghc 2019年9月12日下午5:30:34
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/runOnceTask")
	public PageResult runOnceTask(Integer id) {
		try {
			cronService.runOnceTask(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("执行一次错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("执行一次错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 定时任务获取
	 * 
	 * v1.0 zhanghc 2021年5月27日下午4:27:54
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(Integer id) {
		try {
			Cron cron = cronService.getById(id);
			List<Date> timeList = QuartzUtil.getRecentTriggerTime(cron.getCron(), 3);
			String[] triggerTimes = new String[3];
			for (int i = 0; i < timeList.size(); i++) {
				triggerTimes[i] = DateUtil.formatDateTime(timeList.get(i));
			}

			return PageResultEx.ok()//
					.addAttr("id", cron.getId())//
					.addAttr("name", cron.getName())//
					.addAttr("jobClass", cron.getJobClass())//
					.addAttr("cron", cron.getCron())//
					.addAttr("triggerTimes", triggerTimes)//
					.addAttr("state", cron.getState());
		} catch (MyException e) {
			log.error("定时任务获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("定时任务获取错误：", e);
			return PageResult.err();
		}
	}
}
