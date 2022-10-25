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
      anonState: 2, // 匿名阅卷（1：是；2：否）
      scoreState: 2, // 成绩公开（1：是；2：否）
      rankState: 2, // 排名公开（1：是；2：否）
      genType: 1, // 组卷方式（1：人工组卷；2：随机组卷）
      state: 1 // 状态（1：发布；2：暂停；）
    },
    examQuestions: [],// 试卷信息
    examRules: [],// 试卷规则
    examUsers: [],// 用户信息
  }
}

const getters = {
  // 总分数
  totalScore: (state) => {
    let totalScore = 0
    if (state.examInfo.exam.genType === 1) {
      state.examInfo.examQuestions.forEach(examQuestion => {
        totalScore += examQuestion.type === 1 ? 0 : examQuestion.question.score
      });
    } else if (state.examInfo.exam.genType === 2) {
      state.examInfo.examRules.forEach(examQuestion => {
        if (examQuestion.type === 2) {
          if (examQuestion.score && examQuestion.num) {
            totalScore += examQuestion.score * examQuestion.num
          }
        }
      });
    }
    return totalScore
  },
  // 阅卷类型
  markType: (state) => {
    if (state.examInfo.exam.genType === 1) {// 如果是人工组卷
      return state.examInfo.examQuestions.some(examQuestion => {// 如果有主观题，阅卷类型为人工阅卷
        return examQuestion.type === 2 && examQuestion.question.markType === 2
      }) ? 2 : 1
    }

    if (state.examInfo.exam.genType === 2) {// 如果是随机组卷
      return state.examInfo.examRules.some(examQuestion => {// 如果有主观题，阅卷类型为人工阅卷
        return examQuestion.type === 2 && examQuestion.markType === 2
      }) ? 2 : 1
    }
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
  markUserNum: (state, getters) => {
    if (getters.markType === 2) { // 如果有主观题
      return state.examInfo.examUsers.length
    }
    return 0
  },
  questionNum: (state) => {
    if (state.examInfo.exam.genType === 1) {// 如果是人工组卷
      return state.examInfo.examQuestions.length
    }
    if (state.examInfo.exam.genType === 2) {// 如果是随机组卷
      let total = 0
      state.examInfo.examRules.forEach(examQuestion => {
        if (examQuestion.type === 2) {
          total += examQuestion.num
        }
      })
      return total
    }
  },
  markQuestionNum: (state) => {
    if (state.examInfo.exam.genType === 1) {// 如果是人工组卷
      let total = 0
      state.examInfo.examQuestions.forEach(examQuestion => {
        if (examQuestion.type === 2 && examQuestion.question.markType === 2) {
          total++
        }
      })
      return total 
    }

    if (state.examInfo.exam.genType === 2) {// 如果是随机组卷
      let total = 0
      state.examInfo.examRules.forEach(examQuestion => {
        if (examQuestion.markType === 2) {
          total += examQuestion.num
        }
      })
      return total 
    }
  }
}

const mutations = {
  // 组卷方式修改
  genTypeUpdate: (state, value) => {
    state.examInfo.exam.genType = value
  }, 
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
  // 章节添加
  chapterAddForRule: (state, chapter) => {
    state.examInfo.examRules.push({
      type: 1,
      chapterName: chapter.name,
      chapterTxt: chapter.txt,
    })
  },
  // 章节删除
  chapterDel: (state, index) => {
    state.examInfo.examQuestions.splice(index, 1)
  },
  // 章节删除
  chapterDelForRule: (state, index) => {
    state.examInfo.examRules.splice(index, 1)
  },
  // 章节修改名称
  chapterNameUpdate(state, { index, value }) {
    state.examInfo.examQuestions[index].chapterName = value
  },
  // 章节修改名称
  chapterNameUpdateForRule(state, { index, value }) {
    state.examInfo.examRules[index].chapterName = value
  },
  // 章节修改描述
  chapterTxtUpdate(state, { index, value }) {
    state.examInfo.examQuestions[index].chapterTxt = value
  },
  // 章节修改描述
  chapterTxtUpdateForRule(state, { index, value }) {
    state.examInfo.examRules[index].chapterTxt = value
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
    state.examInfo.examRules.splice(0)
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

  // 规则添加
  ruleAdd(state) {
    state.examInfo.examRules.push({
      type: 2,
      questionTypeId: null,
      questionType: 1,
      markType: 1,
      markOptions: [],
      num: 10,
      score: 1,
      scores: [],
      no: 1
    })
  },
  // 规则删除
  ruleDel(state, index) {
    state.examInfo.examRules.splice(index, 1)
  },
  // 规则题库修改
  examRuleQuestionTypeIdUpdate(state, {index, value}) {
    if (!value) {
      value = null
    }
    state.examInfo.examRules[index].questionTypeId = value
  },
  // 规则试题类型修改
  examRuleQuestionTypeUpdate(state, {index, value}) {
    state.examInfo.examRules[index].questionType = value
  },
  // 规则题数修改
  examRuleNumUpdate(state, {index, value}) {
    state.examInfo.examRules[index].num = value
  },
  // 规则分数修改
  examRuleScoreUpdate(state, {index, value}) {
    state.examInfo.examRules[index].score = value
  },
  // 规则分数修改
  examRuleScoresUpdate(state, {index, value}) {
    state.examInfo.examRules[index].scores = value
  },
  // 规则阅卷类型修改
  examRuleMarkTypeUpdate(state, {index, value}) {
    state.examInfo.examRules[index].markType = value
  },
  // 规则阅卷选项修改
  examRuleMarkOptionsUpdate(state, {index, value}) {
    state.examInfo.examRules[index].markOptions = value
  },
  // 规则排序
  ruleSort(state) {
    let no = 1
    state.examInfo.examRules = state.examInfo.examRules.map((examQuestion) => {
      if (examQuestion.type === 2) {
        examQuestion.no = no++ // 直接改变值，Draggable下v-model检测不到变化
      }
      return examQuestion
    });
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  getters,
}
