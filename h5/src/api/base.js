/*
 * @Description: 基础API
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 17:02:08
 * @LastEditors: Che
 * @LastEditTime: 2021-08-11 22:53:21
 */

import request from './request'

export const dictListPage = (params) => request('dict/listpage', params)
export const dictGet = (params) => request('dict/get', params)
export const dictAdd = (params) => request('dict/add', params)
export const dictEdit = (params) => request('dict/edit', params)
export const dictDel = (params) => request('dict/del', params)
export const cronListPage = (params) => request('cron/listpage', params)
export const cronGet = (params) => request('cron/get', params)
export const cronAdd = (params) => request('cron/add', params)
export const cronEdit = (params) => request('cron/edit', params)
export const cronDel = (params) => request('cron/del', params)
export const cronStartTask = (params) => request('cron/startTask', params)
export const cronStopTask = (params) => request('cron/stopTask', params)
export const cronrunOnceTask = (params) => request('cron/runOnceTask', params)
export const orgListPage = (params) => request('org/listpage', params)
export const orgTreeList = (params) => request('org/treeList', params)
export const orgGet = (params) => request('org/get', params)
export const orgAdd = (params) => request('org/add', params)
export const orgEdit = (params) => request('org/edit', params)
export const orgDel = (params) => request('org/del', params)
export const parmListPage = (params) => request('parm/listpage', params)
export const parmGet = (params) => request('parm/get', params)
export const parmAdd = (params) => request('parm/add', params)
export const parmEdit = (params) => request('parm/edit', params)
export const parmDel = (params) => request('parm/del', params)
export const bulletinListPage = (params) => request('bulletin/listpage', params)
export const bulletinAdd = (params) => request('bulletin/add', params)
export const bulletinEdit = (params) => request('bulletin/edit', params)
export const bulletinDel = (params) => request('bulletin/del', params)
export const bulletinGet = (params) => request('bulletin/get', params)
