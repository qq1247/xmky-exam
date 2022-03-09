/*
 * @Description: 试卷API
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 16:27:54
 * @LastEditors: Che
 * @LastEditTime: 2022-01-20 14:01:16
 */

import request from './request'

export const paperListPage = (params) => request('paper/listpage', params)
export const paperAdd = (params) => request('paper/add', params)
export const paperEdit = (params) => request('paper/edit', params)
export const paperDel = (params) => request('paper/del', params)
export const paperGet = (params) => request('paper/get', params)
export const paperCopy = (params) => request('paper/copy', params)
export const paperPublish = (params) => request('paper/publish', params)

export const paperChapterAdd = (params) => request('paper/chapterAdd', params)
export const paperChapterEdit = (params) => request('paper/chapterEdit', params)
export const paperChapterDel = (params) => request('paper/chapterDel', params)

export const paperQuestionList = (params) =>
  request('paper/paperQuestionList', params)
export const paperQuestionAdd = (params) => request('paper/questionAdd', params)
export const paperQuestionDel = (params) => request('paper/questionDel', params)
export const paperQuestionClear = (params) =>
  request('paper/questionClear', params)
export const paperQuestionPublish = (params) => request('paper/publish', params)

export const paperChapterMove = (params) => request('paper/chapterMove', params)
export const paperUpdateBatchScore = (params) =>
  request('paper/updateBatchScore', params)
export const paperQuestionMove = (params) =>
  request('paper/questionMove', params)
export const paperScoreUpdate = (params) => request('paper/scoreUpdate', params)
export const paperScoreOptionUpdate = (params) =>
  request('paper/scoreOptionUpdate', params)
export const paperBatchScoreUpdate = (params) =>
  request('paper/batchScoreUpdate', params)
export const paperTotalScoreUpdate = (params) =>
  request('paper/totalScoreUpdate', params)
export const paperTypeListPage = (params) =>
  request('paperType/listpage', params)

export const paperTypeAdd = (params) => request('paperType/add', params)
export const paperTypeEdit = (params) => request('paperType/edit', params)
export const paperTypeDel = (params) => request('paperType/del', params)
export const paperTypeGet = (params) => request('paperType/get', params)
export const paperTypeMove = (params) => request('paperType/move', params)
export const paperTypeAuth = (params) => request('paperType/auth', params)

export const paperQuestionRuleAdd = (params) =>
  request('paperQuestionRule/add', params)
export const paperQuestionRuleEdit = (params) =>
  request('paperQuestionRule/edit', params)
export const paperQuestionRuleDel = (params) =>
  request('paperQuestionRule/del', params)
export const paperQuestionRuleGet = (params) =>
  request('paperQuestionRule/get', params)
export const paperQuestionRuleList = (params) =>
  request('paperQuestionRule/paperQuestionRuleList', params)
export const paperQuestionTypeList = (params) =>
  request('paperQuestionRule/questionList', params)
