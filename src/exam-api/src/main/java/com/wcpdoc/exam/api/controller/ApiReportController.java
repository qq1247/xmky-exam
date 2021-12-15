package com.wcpdoc.exam.api.controller;
 
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.report.service.GradeService;
import com.wcpdoc.exam.report.service.ServerPramService;
 
/**
 * 成绩报表控制层
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Controller
@RequestMapping("/api/report")
public class ApiReportController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(ApiReportController.class);
    
    @Resource
    private GradeService gradeService;
    @Resource
    private ServerPramService serverParmService;
    
    /**
     * 首页用户
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/homeUser")
    @ResponseBody
    public PageResult homeUser() {
        try {
            return PageResultEx.ok().data(gradeService.homeUser());
        } catch (MyException e) {
            log.error("首页用户统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("首页用户统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 首页子管理
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/homeSubAdmin")
    @ResponseBody
    public PageResult homeSubAdmin() {
        try {
            return PageResultEx.ok().data(gradeService.homeSubAdmin());
        } catch (MyException e) {
            log.error("首页用户统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("首页用户统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 首页管理
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/homeAdmin")
    @ResponseBody
    public PageResult homeAdmin() {
        try {
            return PageResultEx.ok().data(gradeService.homeAdmin());
        } catch (MyException e) {
            log.error("首页用户统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("首页用户统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 服务器参数
     * 
     * v1.0 chenyun 2021年12月10日上午10:14:34
     * @return PageResult
     */
    @RequestMapping("/server/parm")
    @ResponseBody
    public PageResult serverParm() {
        try {
            return PageResultEx.ok().data(serverParmService.getList());
        } catch (MyException e) {
            log.error("首页用户统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("首页用户统计错误：", e);
            return PageResult.err();
        }
    }
    
    /**
     * 分数统计
     * 
     * v1.0 zhanghc 2018年11月24日上午9:13:22
     * @param id
     * @return PageResult
     */
    @RequestMapping("/count")
    @ResponseBody
    public PageResult count(Integer examId) {
        try {
            return PageResultEx.ok().data(gradeService.count(examId));
        } catch (MyException e) {
            log.error("分数统计错误：{}", e.getMessage());
            return PageResult.err().msg(e.getMessage());
        } catch (Exception e) {
            log.error("分数统计错误：", e);
            return PageResult.err();
        }
    }
}