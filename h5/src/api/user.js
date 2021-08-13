/*
 * @Description: 用户API
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 16:28:34
 * @LastEditors: Che
 * @LastEditTime: 2021-08-11 22:52:45
 */
import request from './request'

/**
 * @name: userListPage
 * @description: 用户列表信息
 * @param {*}
 * @return {*}
 */
export const userListPage = (params) => request('user/listpage', params)

/**
 * @name: userGet
 * @description: 获取用户信息
 * @param {*}
 * @return {*}
 */
export const userGet = (params) => request('user/get', params)

/**
 * @name: userAdd
 * @description: 添加用户信息
 * @param {*}
 * @return {*}
 */
export const userAdd = (params) => request('user/add', params)

/**
 * @name: userEdit
 * @description: 修改用户信息
 * @param {*}
 * @return {*}
 */
export const userEdit = (params) => request('user/edit', params)

/**
 * @name: userDel
 * @description: 删除用户信息
 * @param {*}
 * @return {*}
 */
export const userDel = (params) => request('user/del', params)

/**
 * @name: userInitPwd
 * @description: 初始用户密码
 * @param {*}
 * @return {*}
 */
export const userInitPwd = (params) => request('user/initPwd', params)
