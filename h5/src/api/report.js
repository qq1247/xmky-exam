/*
 * @Description: 统计API
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-12-15 18:13:11
 * @LastEditors: Che
 * @LastEditTime: 2021-12-16 16:12:52
 */
import request from './request'

/**
 * @name: getUserInfo
 * @description: 获取首页用户信息
 * @param {*}
 * @return {*}
 */
export const getUserInfo = (params) => request('report/home/user', params)

/**
 * @name: getSubAdminInfo
 * @description: 获取首页子管理员信息
 * @param {*}
 * @return {*}
 */
export const getSubAdminInfo = (params) =>
  request('report/home/subAdmin', params)

/**
 * @name: getAdminInfo
 * @description: 获取首页管理员信息
 * @param {*}
 * @return {*}
 */
export const getAdminInfo = (params) => request('report/home/admin', params)

/**
 * @name: getServerParam
 * @description: 获取系统运行参数
 * @param {*}
 * @return {*}
 */
export const getServerParam = (params) => request('report/server/parm', params)

/**
 * @name: getServerLog
 * @description: 获取系统慢接口日志
 * @param {*}
 * @return {*}
 */
export const getServerLog = (params) => request('report/server/log', params)

/**
 * @name: getQuestionStatis
 * @description: 获取试题统计信息
 * @param {*}
 * @return {*}
 */
export const getQuestionStatis = (params) =>
  request('report/question/statis', params)

/**
 * @name: getExamStatis
 * @description: 获取考试统计信息
 * @param {*}
 * @return {*}
 */
export const getExamStatis = (params) => request('report/exam/statis', params)

/**
 * @name: getRanking
 * @description: 获取考试排名信息
 * @param {*}
 * @return {*}
 */
export const getRanking = (params) => request('report/myExam/listpage', params)

/**
 * @name: getQuestionError
 * @description: 获取错误试题统计信息
 * @param {*}
 * @return {*}
 */
export const getQuestionError = (params) =>
  request('report/question/listpage', params)
