import qs from 'qs'

const http = uni.$u.http

// 我的统计信息
export const getUserInfo = (params = {}, config = {custom: { auth: true }}) => http.post('report/home/user', params, config)

// 我的考试信息
export const myExamListPage = (params = {}, config = {custom: { auth: true }}) => http.post('myExam/listpage', params, config)

// 我的模拟试题
export const questionTypeOpenListPage = (params, config = {custom: { auth: true }}) => http.post('questionTypeOpen/listpage', params, config)

// 考试作答答案
export const myExamAnswerList = (params, config = {custom: { auth: true }}) => http.post('myExam/answerList', params, config)

// 提交考试答案
export const myExamAnswer = (params, config = {custom: { auth: true }}) => http.post('myExam/answer', params, config)

// 结束考试
export const myExamFinish = (params, config = {custom: { auth: true }}) => http.post('myExam/finish', params, config)