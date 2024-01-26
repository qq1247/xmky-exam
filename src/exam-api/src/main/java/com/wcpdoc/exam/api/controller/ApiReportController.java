package com.wcpdoc.exam.api.controller;
 
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.report.service.ReportService;

import lombok.extern.slf4j.Slf4j;
 
/**
 * 成绩报表控制层
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@RestController
@RequestMapping("/api/report")
@Slf4j
public class ApiReportController extends BaseController{
    
    @Resource
    private ReportService reportService;
    
    /**
     * 用户首页
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/user/home")
    public PageResult userHome() {
        try {
            return PageResultEx.ok().data(reportService.userHome());
        } catch (MyException e) {
            log.error("用户首页错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("用户首页错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 管理员首页
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/admin/home")
    public PageResult adminHome() {
        try {
            return PageResultEx.ok().data(reportService.adminHome());
        } catch (MyException e) {
            log.error("管理员首页错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("管理员首页错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 子管理员首页
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/subAdmin/home")
    public PageResult subAdminHome() {
        try {
            return PageResultEx.ok().data(reportService.subAdminHome());
        } catch (MyException e) {
            log.error("子管理员首页错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("子管理员首页错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 阅卷用户首页
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/markUser/home")
    public PageResult markUserHome() {
    	try {
    		return PageResultEx.ok().data(reportService.markUserHome());
    	} catch (MyException e) {
    		log.error("阅卷用户首页错误：{}", e.getMessage());
    		return PageResult.err().msg(e.getMessage());
    	} catch (Exception e) {
    		log.error("阅卷用户首页错误：", e);
    		return PageResult.err();
    	}
    }
    
//    /**
//     * 服务器参数 // 第三方dll有bug
//     * 
//     * v1.0 chenyun 2021年12月10日上午10:14:34
//     * @return PageResult
//     */
//    @RequestMapping("/server/parm")
//    public PageResult serverParm() {
//        try {
//        	return null;
////            return PageResultEx.ok().data(serverParmService.getList());
//        } catch (MyException e) {
//            log.error("首页服务器参数统计错误：{}", e.getMessage());
//            return PageResult.err().msg(e.getMessage());
//        } catch (Exception e) {
//            log.error("首页服务器参数统计错误：", e);
//            return PageResult.err();
//        }
//    }
    
    /**
     * 慢接口日志
     * 
     * v1.0 chenyun 2021-12-15 13:49:29
     * @return PageResult
     */
    @RequestMapping("/server/log")
    public PageResult serverLog() {
        try {
           return PageResultEx.ok().data(reportService.serverLog());
        } catch (MyException e) {
            log.error("首页慢接口日志统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("首页慢接口日志统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 试题统计
     * 
     * v1.0 chenyun 2021-12-15 13:44:47
     * @param id
     * @return PageResult
     */
    @RequestMapping("/question/statis")
    public PageResult questionStatis(Integer questionTypeId) {
        try {
            return PageResultEx.ok().data(reportService.questionStatis(questionTypeId));
        } catch (MyException e) {
            log.error("试题统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("试题统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 考试统计
     * 
     * v1.0 chenyun 2021-12-15 13:44:47
     * @param examId
     * @return PageResult
     */
    @RequestMapping("/exam/statis")
    public PageResult examStatis(Integer examId) {
        try {
            return PageResultEx.ok().data(reportService.examStatis(examId));
        } catch (MyException e) {
            log.error("考试统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("考试统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 考试排名
     * 
     * v1.0 chenyun 2021-12-15 13:44:47
     * @return PageResult
     */
    @RequestMapping("/exam/rankListpage")
    public PageResult examRankListpage(PageIn pageIn) {
        try {// 不校验，任何时候都能查询
        	PageOut pageOut = reportService.examRankListpage(pageIn);
        	for (Map<String, Object> map : pageOut.getList()) {
        		Integer examMarkType = (Integer) map.get("examMarkType");
        		Integer examMarkState = (Integer) map.get("examMarkState");
        		if (examMarkType == 2 && examMarkState != 3) {// 如果是主观题试卷，且考试未结束，不显示分数
        			map.put("myExamTotalScore", null);// 阅完一张会打分，但是会二次修改分数，不应该显示中间状态
        		}
        	}
            return PageResultEx.ok().data(pageOut);
        } catch (MyException e) {
            log.error("考试排名错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("考试排名错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 错题分析
     * 
     * v1.0 chenyun 2021-12-15 13:44:47
     * @return PageResult
     */
//    @RequestMapping("/exam/questionErrList")
//    public PageResult questionErrList(Integer examId) {
//        try {
//            return PageResultEx.ok().data(reportService.questionErrList(examId));
//        } catch (MyException e) {
//            log.error("错题分析统计错误：{}", e.getMessage());
//            return PageResult.err().msg(e.getMessage());
//        } catch (Exception e) {
//            log.error("错题分析统计错误：", e);
//            return PageResult.err();
//        }
//    }
    
}