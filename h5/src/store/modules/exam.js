const state = {
  paperName: '点击这里输入试卷名称',
  examQuestions: [],
  exam: {
    name: '',
    timeType: 2,// 试卷类型
    examTimes: [new Date(), new Date()],// 考试时间
    markTimes: [new Date(), new Date()],// 阅卷时间
    passScore: 60, // 及格%
    sxes: [], // 防作弊
    showType: 1, // 展示方式
    anonState: 2, // 匿名阅卷
    scoreState: 2, // 成绩公开
    rankState: 2, // 排名公开
    examUsers: [{ "examUserIds": [], "markUserId": null }],  //考生用户 | 阅卷用户
  }
}

const mutations = {
  paperNameUpdate(state, paperName) {
    state.paperName = paperName
  },
  chapterAdd: (state, chapter) => {
    state.examQuestions.push({
      type: 1,
      chapterName: chapter.name,
      chapterTxt: chapter.txt,
    })
  },
  questionAdd(state, question) {
    state.examQuestions.push({
      type: 2,
      question
    })
  },
  questionSort(state) {
    let no = 1
    state.examQuestions = state.examQuestions.map((examQuestion, index) => {
      if (examQuestion.type === 2) {
        examQuestion.question.no = no++ // 直接改变值，Draggable下v-model检测不到变化
      }
      return examQuestion
    });
  },
}

export default {
  namespaced: true,
  state,
  mutations,
}
