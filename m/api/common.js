import qs from 'qs'

const http = uni.$u.http

// 登录
export const login = (params, config = {custom: { auth: true }}) => http.post('login/in', params, config)

// 修改密码
export const loginPwd = (params, config = {custom: { auth: true }}) => http.post('login/pwd', params, config)

// 公告列表
export const bulletinListPage = (params, config = {custom: { auth: true }}) => http.post('bulletin/listpage', params, config)

// 获取系统时间
export const loginSysTime = (params, config = {custom: { auth: true }}) => http.post('login/sysTime', params, config)