import qs from 'qs'

const http = uni.$u.http

// 获取试卷信息
export const paperGet = (params, config = {custom: { auth: true }}) => http.post('paper/get', params, config)

// 获取试卷试题（人工）
export const paperQuestions = (params, config = {custom: { auth: true }}) => http.post('paper/myPaper', params, config)

// 获取试卷试题（随机）
export const paperRandomQuestions = (params, config = {custom: { auth: true }}) => http.post('paper/myPaperOfRand', params, config)