/*
 * @Description: 考试API
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 16:28:25
 * @LastEditors: Che
 * @LastEditTime: 2021-08-11 22:47:52
 */
import request from './request'

/**
 * @name: examListPage
 * @description: 考试列表信息
 * @param {*}
 * @return {*}
 */
export const examListPage = (params) => request('exam/listpage', params)

/**
 * @name: examAdd
 * @description: 添加考试信息
 * @param {*}
 * @return {*}
 */
export const examAdd = (params) => request('exam/add', params)

/**
 * @name: examEdit
 * @description: 编辑考试信息
 * @param {*}
 * @return {*}
 */
export const examEdit = (params) => request('exam/edit', params)

/**
 * @name: examDel
 * @description: 删除考试信息
 * @param {*}
 * @return {*}
 */
export const examDel = (params) => request('exam/del', params)

/**
 * @name: examUpdateExamUser
 * @description: 更新考试用户
 * @param {*}
 * @return {*}
 */
export const examUpdateExamUser = (params) =>
  request('exam/updateExamUser', params)

/**
 * @name: examUpdateMarkUser
 * @description: 跟新阅卷用户
 * @param {*}
 * @return {*}
 */
export const examUpdateMarkUser = (params) =>
  request('exam/updateMarkUser', params)

/**
 * @name: examUserList
 * @description: 考试用户列表
 * @param {*}
 * @return {*}
 */
export const examUserList = (params) => request('exam/examUserList', params)

/**
 * @name: examMarkUserList
 * @description: 阅卷用户列表
 * @param {*}
 * @return {*}
 */
export const examMarkUserList = (params) => request('exam/markUserList', params)

/**
 * @name: examQuestionList
 * @description: 考试试题列表
 * @param {*}
 * @return {*}
 */
export const examQuestionList = (params) => request('exam/questionList', params)

/**
 * @name: examPublish
 * @description: 发布考试
 * @param {*}
 * @return {*}
 */
export const examPublish = (params) => request('exam/publish', params)

/**
 * @name: examTypeListPage
 * @description: 考试类型列表
 * @param {*}
 * @return {*}
 */
export const examTypeListPage = (params) => request('examType/listpage', params)

/**
 * @name: examTypeAdd
 * @description: 添加考试类型
 * @param {*}
 * @return {*}
 */
export const examTypeAdd = (params) => request('examType/add', params)

/**
 * @name: examTypeEdit
 * @description: 编辑考试类型
 * @param {*}
 * @return {*}
 */
export const examTypeEdit = (params) => request('examType/edit', params)

/**
 * @name: examTypeDel
 * @description: 删除考试类型
 * @param {*}
 * @return {*}
 */
export const examTypeDel = (params) => request('examType/del', params)

/**
 * @name: examTypeGet
 * @description: 获取考试类型
 * @param {*}
 * @return {*}
 */
export const examTypeGet = (params) => request('examType/get', params)

/**
 * @name: examTypeMove
 * @description: 合并考试类型
 * @param {*}
 * @return {*}
 */
export const examTypeMove = (params) => request('examType/move', params)

/**
 * @name: examTypeAuth
 * @description: 编辑考试类型权限
 * @param {*}
 * @return {*}
 */
export const examTypeAuth = (params) => request('examType/auth', params)
