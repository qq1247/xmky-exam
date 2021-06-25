import http from '@/util/http' // 导入http中创建的axios实例
import qs from 'qs' // 根据需求是否导入qs模块

const post = async (
  url,
  params = {},
  responseType = 'json',
  headers = 'application/x-www-form-urlencoded'
) =>
  await http({
    url: url,
    method: 'post',
    data:
      headers == 'application/x-www-form-urlencoded'
        ? qs.stringify(params, { arrayFormat: 'repeat' })
        : params,
    responseType: responseType,
    headers: {
      'Content-Type': headers
    }
  })

export default {
  // 用户相关
  userListpage: params => post('user/listpage', params),
  userGet: params => post('user/get', params),
  userAdd: params => post('user/add', params),
  userEdit: params => post('user/edit', params),
  userDel: params => post('user/del', params),

  // 数据字典
  dictListpage: params => post('dict/listpage', params),
  dictGet: params => post('dict/get', params),
  dictAdd: params => post('dict/add', params),
  dictEdit: params => post('dict/edit', params),
  dictDel: params => post('dict/del', params),

  // 定时任务
  cronListpage: params => post('cron/listpage', params),
  cronGet: params => post('cron/get', params),
  cronAdd: params => post('cron/add', params),
  cronEdit: params => post('cron/edit', params),
  cronDel: params => post('cron/del', params),
  cronStartTask: params => post('cron/startTask', params),
  cronStopTask: params => post('cron/stopTask', params),
  cronrunOnceTask: params => post('cron/runOnceTask', params),

  // 组织机构相关
  orgListpage: params => post('org/listpage', params),
  orgTreeList: params => post('org/treeList', params),
  orgGet: params => post('org/get', params),
  orgAdd: params => post('org/add', params),
  orgEdit: params => post('org/edit', params),
  orgDel: params => post('org/del', params),

  // 参数相关
  parmListpage: params => post('parm/listpage', params),
  parmGet: params => post('parm/get', params),
  parmAdd: params => post('parm/add', params),
  parmEdit: params => post('parm/edit', params),
  parmDel: params => post('parm/del', params),

  // 考试相关
  examListPage: params => post('exam/listpage', params),
  examAdd: params => post('exam/add', params),
  examEdit: params => post('exam/edit', params),
  examDel: params => post('exam/del', params),
  examUpdateExamUser: params => post('exam/updateExamUser', params),
  examUpdateMarkUser: params => post('exam/updateMarkUser', params),
  examUserList: params => post('exam/userList', params),
  examQuestionList: params => post('exam/questionList', params),
  examPublish: params => post('exam/publish', params),

  // 考试分类相关
  examTypeListPage: params => post('examType/listpage', params),
  examTypeAdd: params => post('examType/add', params),
  examTypeEdit: params => post('examType/edit', params),
  examTypeDel: params => post('examType/del', params),
  examTypeGet: params => post('examType/get', params),
  examTypeMove: params => post('examType/move', params),

  // 试卷相关
  paperListPage: params => post('paper/listpage', params),
  paperAdd: params => post('paper/add', params),
  paperEdit: params => post('paper/edit', params),
  paperDel: params => post('paper/del', params),
  paperGet: params => post('paper/get', params),
  paperCopy: params => post('paper/copy', params),
  paperArchive: params => post('paper/archive', params),

  // 试卷试题相关
  paperChapterAdd: params => post('paper/chapterAdd', params),
  paperChapterEdit: params => post('paper/chapterEdit', params),
  paperChapterDel: params => post('paper/chapterDel', params),
  paperQuestionList: params => post('paper/paperQuestionList', params),
  paperQuestionAdd: params => post('paper/questionAdd', params),
  paperQuestionDel: params => post('paper/questionDel', params),
  paperQuestionClear: params => post('paper/questionClear', params),
  paperScoreUpdate: params => post('paper/scoreUpdate', params),
  paperScoreOptionsUpdate: params => post('paper/scoreOptionsUpdate', params),
  paperBatchScoreUpdate: params => post('paper/batchScoreUpdate', params),
  paperQuestionUp: params => post('paper/questionUp', params),
  paperQuestionDown: params => post('paper/questionDown', params),
  paperQuestionPublish: params => post('paper/publish', params),

  // 试卷分类相关
  paperTypeListPage: params => post('paperType/listpage', params),
  paperTypeAdd: params => post('paperType/add', params),
  paperTypeEdit: params => post('paperType/edit', params),
  paperTypeDel: params => post('paperType/del', params),
  paperTypeGet: params => post('paperType/get', params),
  paperTypeMove: params => post('paperType/move', params),

  // 试题相关
  questionListPage: params => post('question/listpage', params),
  questionGet: params => post('question/get', params),
  questionAdd: params => post('question/add', params),
  questionEdit: params => post('question/edit', params),
  questionDel: params => post('question/del', params),
  questionCopy: params => post('question/copy', params),
  questionTemplate: (params, type) =>
    post('question/wordTemplateExport', params, type),
  questionImport: (params, type, headers) =>
    post('question/wordImp', params, type, headers),

  // 试题分类相关
  questionTypeListPage: params => post('questionType/listpage', params),
  questionTypeAdd: params => post('questionType/add', params),
  questionTypeEdit: params => post('questionType/edit', params),
  questionTypeDel: params => post('questionType/del', params),
  questionTypeGet: params => post('questionType/get', params),
  questionTypeMove: params => post('questionType/move', params),
  questionTypeAuth: params => post('questionType/auth', params),

  // 试题分类开放相关
  questionTypeOpenListPage: params => post('questionTypeOpen/listpage', params),
  questionTypeOpenAdd: params => post('questionTypeOpen/add', params),
  questionTypeOpenDel: params => post('questionTypeOpen/del', params),

  // 基础功能相关
  login: params => post('login/in', params)
}
