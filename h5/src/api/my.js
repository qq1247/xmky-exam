import request from './request'

export const myExamListpage = (params) => request('myExam/listpage', params)
export const myExamAnswerList = (params) => request('myExam/answerList', params)
export const myExamAnswer = (params) => request('myExam/answer', params)
export const myExamFinish = (params) => request('myExam/finish', params)
export const myExamPaper = (params) => request('myExam/paper', params)
export const myExamGet = (params) => request('myExam/get', params)
export const myExamCreatePaper = (params) => request('myExam/createPaper', params)

export const myMarkListpage = (params) => request('myMark/listpage', params)
export const myMarkExamListpage = (params) =>
  request('myMark/examListpage', params)
export const myMarkScore = (params) => request('myMark/score', params)
export const myMarkFinish = (params) => request('myMark/finish', params)

export const myMarkAnswerList = (params) => request('myMark/answerList', params)
export const myMarkUserListpage = (params) => request('myMark/userListpage', params)
export const myMarkUser = (params) => request('myMark/user', params)
export const myMarkPaper = (params) => request('myMark/paper', params)
export const myMarkGet = (params) => request('myMark/get', params)
