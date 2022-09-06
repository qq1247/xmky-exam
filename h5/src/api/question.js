import request from './request'

export const questionListpage = (params) => request('question/listpage', params)
export const questionGet = (params) => request('question/get', params)
export const questionAdd = (params) => request('question/add', params)
export const questionEdit = (params) => request('question/edit', params)
export const questionDel = (params) => request('question/del', params)
export const questionCopy = (params) => request('question/copy', params)
export const questionPublish = (params) => request('question/publish', params)
export const questionTemplate = (params, type) =>
  request('question/wordTemplateExport', params, undefined, type)
export const questionImport = (params, timeout) =>
  request('question/wordImp', params, timeout)
export const questionTypeListpage = (params) =>
  request('questionType/listpage', params)

export const questionTypeAdd = (params) => request('questionType/add', params)
export const questionTypeEdit = (params) => request('questionType/edit', params)
export const questionTypeDel = (params) => request('questionType/del', params)
export const questionTypeGet = (params) => request('questionType/get', params)
export const questionTypeMove = (params) => request('questionType/move', params)
export const questionTypeAuth = (params) => request('questionType/auth', params)

export const questionTypeOpenListpage = (params) =>
  request('questionTypeOpen/listpage', params)
export const questionTypeOpenAdd = (params) =>
  request('questionTypeOpen/add', params)
export const questionTypeOpenDel = (params) =>
  request('questionTypeOpen/del', params)
export const questionTypeOpenQuestionGet = (params) =>
  request('questionTypeOpen/questionGet', params)
export const questionTypeOpenQuestionIds = (params) =>
  request('questionTypeOpen/questionIds', params)

export const questionCommentListpage = (params) =>
  request('questionComment/listpage', params)
export const questionCommentAdd = (params) =>
  request('questionComment/add', params)
