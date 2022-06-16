import request from './request'

export const dictGet = (params) => request('dict/get', params)
export const dictAdd = (params) => request('dict/add', params)
export const dictEdit = (params) => request('dict/edit', params)
export const dictDel = (params) => request('dict/del', params)
export const dictListPage = (params) => request('dict/listpage', params)

export const cronGet = (params) => request('cron/get', params)
export const cronAdd = (params) => request('cron/add', params)
export const cronEdit = (params) => request('cron/edit', params)
export const cronDel = (params) => request('cron/del', params)
export const cronrunOnceTask = (params) => request('cron/runOnceTask', params)
export const cronStartTask = (params) => request('cron/startTask', params)
export const cronStopTask = (params) => request('cron/stopTask', params)
export const cronListPage = (params) => request('cron/listpage', params)

export const orgGet = (params) => request('org/get', params)
export const orgAdd = (params) => request('org/add', params)
export const orgEdit = (params) => request('org', params)
export const orgDel = (params) => request('org/del', params)
export const orgTreeList = (params) => request('org/treeList', params)
export const orgListPage = (params) => request('org/listpage', params)
export const orgTemplate = (params, type) =>
  request('org/template', params, undefined, type)
export const orgImport = (params) => request('org/import', params)

export const parmGet = (params) => request('parm/get', params)
export const parmEmail = (params) => request('parm/email', params)
export const parmFile = (params) => request('parm/file', params)
export const parmLogo = (params) => request('parm/logo', params)
export const parmDb = (params) => request('parm/db', params)
export const parmPwd = (params) => request('parm/pwd', params)

export const bulletinAdd = (params) => request('bulletin/add', params)
export const bulletinEdit = (params) => request('bulletin/edit', params)
export const bulletinDel = (params) => request('bulletin/del', params)
export const bulletinGet = (params) => request('bulletin/get', params)
export const bulletinListPage = (params) => request('bulletin/listpage', params)

export const sensitiveEdit = (params) => request('sensitive/edit', params)
export const sensitiveGet = (params) => request('sensitive/get', params)
