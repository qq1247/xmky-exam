package com.wcpdoc.api.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

/**
 * 定时任务控制层
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
@Controller
@RequestMapping("/api/cron")
public class ApiCronController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiCronController.class);

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
	@ResponseBody
	public PageResult listpage() {
		try {
			PageOut pageOut = cronService.getListpage(new PageIn(request));
			List<Map<String, Object>> list = pageOut.getList();
			for (Map<String, Object> map : list) {
				String cron = (String)map.get("cron");
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
	 * 完成添加定时任务
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param cron
	 * @return PageResult
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Cron cron) {
		try {
			cron.setUpdateUserId(getCurUser().getId());
			cron.setUpdateTime(new Date());
			// 校验数据有效性
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

			// 添加定时任务，默认不启动
			cron.setState(2);
			cronService.add(cron);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加定时任务错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加定时任务错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成修改定时任务
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param cron
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Cron cron) {
		try {
			cronService.updateEx(cron);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改定时任务错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改定时任务错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成删除定时任务
	 * 
	 * v1.0 zhanghc 2019-07-29 10:38:17
	 * 
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			cronService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除定时任务错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除定时任务错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 启动任务
	 * 
	 * v1.0 zhanghc 2019年9月12日下午5:30:34
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/startTask")
	@ResponseBody
	public PageResult startTask(Integer id) {
		try {
			cronService.startTask(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("启动任务错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("启动任务错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 停止任务
	 * 
	 * v1.0 zhanghc 2019年9月12日下午5:30:34
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/stopTask")
	@ResponseBody
	public PageResult stopTask(Integer id) {
		try {
			cronService.stopTask(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("停止任务错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("停止任务错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 执行一次
	 * 
	 * v1.0 zhanghc 2019年9月12日下午5:30:34
	 * @param ids
	 * @return PageResult
	 */
	@RequestMapping("/runOnceTask")
	@ResponseBody
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
	 * 获取定时任务
	 * 
	 * v1.0 zhanghc 2021年5月27日下午4:27:54
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Cron cron = cronService.getEntity(id);
			List<Date> timeList = QuartzUtil.getRecentTriggerTime(cron.getCron(), 3);
			String[] triggerTimes = new String[3]; 
			for (int i = 0; i < timeList.size(); i++) {
				triggerTimes[i] = DateUtil.formatDateTime(timeList.get(i));
			}
			
			return PageResultEx.ok()
					.addAttr("id", cron.getId())
					.addAttr("name", cron.getName())
					.addAttr("jobClass", cron.getJobClass())
					.addAttr("cron", cron.getCron())
					.addAttr("triggerTimes", triggerTimes)
					;
		} catch (MyException e) {
			log.error("获取定时任务错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取定时任务错误：", e);
			return PageResult.err();
		}
	}
}
