import qs from 'qs'

const http = uni.$u.http

// 获取考试信息
export const examGet = (params, config = {custom: { auth: true }}) => http.post('exam/get', params, config)