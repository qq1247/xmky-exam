import qs from 'qs'

const http = uni.$u.http

// 开放试题id列表
export const questionTypeOpenQuestionIds = (params = {}, config = {custom: { auth: true }}) => http.post('questionTypeOpen/questionIds', params, config)

// 获取开放试题
export const questionTypeOpenQuestionGet = (params = {}, config = {custom: { auth: true }}) => http.post('questionTypeOpen/questionGet', params, config)