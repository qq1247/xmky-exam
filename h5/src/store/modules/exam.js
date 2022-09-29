const state = {
  paperName: '点击这里输入试卷名称',
  examQuestions: [],
  exam: {
    name: '',
    examTime: [new Date(), new Date()],// 考试时间
    examMarkTime: [new Date(), new Date()],// 阅卷时间
    passScore: 60, // 及格%
    options: [], // 防作弊
    showType: 1, // 展示方式
    anonState: 2, // 匿名阅卷
    scoreState: 2, // 成绩公开
    rankState: 2, // 排名公开
  }
}

const mutations = {
  updatePaperName(state, paperName) {
    state.paperName = paperName
  },
  addChapter: (state, chapter) => {
    state.examQuestions.push({
      type: 1,
      chapterName: chapter.name,
      chapterTxt: chapter.txt,
    })
  },
  addQuestion(state, question) {
    state.examQuestions.push({
      type: 2,
      score: question.score,
      answerScores: question.answerScores,
      markOptions: question.markOptions,
      questionId: question.id,
      question
    })
  },
}

export default {
  namespaced: true,
  state,
  mutations,
}
