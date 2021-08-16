/*
 * @Description: 我的API
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 17:06:11
 * @LastEditors: Che
 * @LastEditTime: 2021-08-11 22:52:28
 */
import request from './request'
export const myExamListPage = (params) => request('myExam/listpage', params)
export const myMarkListPage = (params) => request('myMark/listpage', params)
export const myExamAnswerList = (params) => request('myExam/answerList', params)
export const myMarkAnswerList = (params) =>
  request('myExam/markAnswerList', params)
export const myMarkExamListPage = (params) =>
  request('myMark/examListpage', params)
export const myExamUpdateAnswer = (params) =>
  request('myExam/updateAnswer', params)
export const myExamUpdateScore = (params) =>
  request('myMark/updateScore', params)
export const myExamDoAnswer = (params) => request('myExam/doAnswer', params)
export const myExamDoScore = (params) => request('myMark/doScore', params)
export const myExamAutoScore = (params) => request('myMark/autoScore', params)
export const myExamAiProgress = (params) => request('progressBar/get', params)
export const myMarksListPage = (params) =>
  request('myMark/markListpage', params)
