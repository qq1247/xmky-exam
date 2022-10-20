const state = {
  examInfo: {
    exam: {
      name: '',// 考试名称
      paperName: '点击这里输入试卷名称',// 试卷名称
      timeType: 1,// 试卷类型
      examTimes: [],// 考试时间
      markTimes: [],// 阅卷时间
      passScore: 0, // 及格%
      sxes: [], // 防作弊
      showType: 1, // 展示方式
      anonState: 1, // 匿名阅卷
      scoreState: 2, // 成绩公开
      rankState: 2, // 排名公开
    },
    examQuestions: [],// 试卷信息
    examUsers: [],// 用户信息
  }
}

const getters = {
  // 总分数
  totalScore: (state) => {
    let totalScore = 0
    state.examInfo.examQuestions.forEach(examQuestion => {
      totalScore += examQuestion.type === 1 ? 0 : examQuestion.question.score
    });
    return totalScore
  },
  // 阅卷类型
  markType: (state) => {
    return state.examInfo.examQuestions.filter(examQuestion =>
      (examQuestion.type === 2 && examQuestion.question.markType === 2) 
    ).length === 0 ? 1 : 2
  },
  // 阅卷试题
  markQuestions: (state) => {
    return state.examInfo.examQuestions.filter(examQuestion =>
      (examQuestion.type === 2 && examQuestion.question.markType === 2) 
    )
  },
  // 考试人数
  examUserNum: (state) => {
    let examUserNum = 0
    state.examInfo.examUsers.forEach(examUser => {
      examUserNum += examUser.examUserIds.length
    })
    return examUserNum
  },
  // 阅卷人数
  markUserNum: (state) => {
    let markUserNum = state.examInfo.examUsers.length
    return markUserNum
  },
}

const mutations = {
  // 试卷名称修改
  paperNameUpdate: (state, paperName) => {
    state.examInfo.exam.paperName = paperName
  },
  // 章节添加
  chapterAdd: (state, chapter) => {
    state.examInfo.examQuestions.push({
      type: 1,
      chapterName: chapter.name,
      chapterTxt: chapter.txt,
    })
  },
  // 章节删除
  chapterDel: (state, index) => {
    state.examInfo.examQuestions.splice(index, 1)
  },
  // 章节修改名称
  chapterNameUpdate(state, { index, value }) {
    state.examInfo.examQuestions[index].chapterName = value
  },
  // 章节修改描述
  chapterTxtUpdate(state, { index, value }) {
    state.examInfo.examQuestions[index].chapterTxt = value
  },
  // 试题添加
  questionAdd(state, question) {
    state.examInfo.examQuestions.push({
      type: 2,
      question
    })
  },
  // 试题删除
  questionDel(state, index) {
    state.examInfo.examQuestions.splice(index, 1)
  },
  //试题排序
  questionSort(state) {
    let no = 1
    state.examInfo.examQuestions = state.examInfo.examQuestions.map((examQuestion) => {
      if (examQuestion.type === 2) {
        examQuestion.question.no = no++ // 直接改变值，Draggable下v-model检测不到变化
      }
      return examQuestion
    });
  },
  // 试卷重置
  paperClear(state) {
    state.examInfo.examQuestions.splice(0)
  },
  // 试题分数修改
  questionScoreUpdate(state, {index, value }) {
    let examQuestion = state.examInfo.examQuestions[index]
    if (examQuestion.type !== 2) {
      return
    }
    examQuestion.question.score = value
  },
  // 试题子分数修改
  questionAnswerScoreUpdate(state, {index, answerIndex, value}) {
    let examQuestion = state.examInfo.examQuestions[index]
    if (examQuestion.type !== 2) {
      return
    }

    this._vm.$set(examQuestion.question.answerScores, answerIndex, value)
  },
  // 考试名称修改
  nameUpdate(state, name) {
    state.examInfo.exam.name = name
  },
  // 考试时间类型修改
  timeTypeUpdate(state, timeType) {
    state.examInfo.exam.timeType = timeType
  },
  // 考试时间修改
  examTimesUpdate(state, examTimes) {
    state.examInfo.exam.examTimes = examTimes
  },
  // 阅卷时间修改
  markTimesUpdate(state, markTimes) {
    state.examInfo.exam.markTimes = markTimes
  },
  // 及格分数修改
  passScoreUpdate(state, passScore) {
    state.examInfo.exam.passScore = passScore
  },
  // 防作弊修改
  sxesUpdate(state, sxes) {
    state.examInfo.exam.sxes = sxes
  },
  // 展示方式修改
  showTypeUpdate(state, showType) {
    state.examInfo.exam.showType = showType
  },
  // 匿名状态修改
  anonStateUpdate(state, anonState) {
    state.examInfo.exam.anonState = anonState
  },
  // 成绩公开修改
  scoreStateUpdate(state, scoreState) {
    state.examInfo.exam.scoreState = scoreState
  },
  // 排名公开修改
  rankStateUpdate(state, rankState) {
    state.examInfo.exam.rankState = rankState
  },
  // 用户组添加
  userGroupAdd(state) {
    state.examInfo.examUsers.push({ examUserIds: [], markUserId: null })
  },
  // 用户组删除
  userGroupDel(state) {
    state.examInfo.examUsers.pop()
  },
  // 考试用户修改
  examUsersUpdate(state, {index, value }) {
    state.examInfo.examUsers[index].examUserIds = value
  },
  // 阅卷用户修改
  markUserUpdate(state, {index, value }) {
    state.examInfo.examUsers[index].markUserId = value
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  getters,
}
