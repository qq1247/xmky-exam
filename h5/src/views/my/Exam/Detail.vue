<template>
  <div class="container">
    <div class="content">
      <!-- 导航 -->
      <question-router
        v-if="paperQuestion.length"
        :preview="preview"
        :system-time="systemTime"
        :router-index="routerIndex"
        :paper-question="paperQuestion"
        @sign="sign"
        @toHref="toHref"
        @examEnd="examEnd"
        @forceExamEnd="forceExamEnd"
      />
      <!-- 试题 -->
      <div class="content-center">
        <div class="paper-title">{{ examName }}</div>

        <page-show
          v-if="showType === 1 && Object.keys(myExamDetailCache).length"
          :preview="preview"
          :score-state="scoreState"
          :paper-question="paperQuestion"
          :my-exam-detail-cache="myExamDetailCache"
          @updateAnswer="updateAnswer"
        />

        <question-show
          v-if="showType === 3 && Object.keys(myExamDetailCache).length"
          :preview="preview"
          :router-index="routerIndex"
          :paper-question="paperQuestion"
          :my-exam-detail-cache="myExamDetailCache"
          @updateAnswer="updateAnswer"
          @prevQuestion="prevQuestion"
          @nextQuestion="nextQuestion"
        />
      </div>
    </div>
  </div>
</template>
<script>
import { loginSysTime } from 'api/common'
import { paperQuestions, paperRandomQuestions, paperGet } from 'api/paper'
import {
  myExamAnswer,
  myExamFinish,
  myExamAnswerList,
  myMarkAnswerList
} from 'api/my'
import { examGet } from 'api/exam'
import PageShow from 'components/PaperContent/PageShow.vue'
import QuestionShow from 'components/PaperContent/QuestionShow.vue'
import QuestionRouter from 'components/PaperContent/QuestionRouter.vue'
export default {
  components: {
    PageShow,
    QuestionShow,
    QuestionRouter
  },
  data() {
    return {
      id: 0,
      examId: null,
      examName: '',
      paperId: null,
      userId: null,
      showType: 1,
      preview: false,
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      paperList: [],
      paperQuestion: [],
      myExamDetailCache: {},
      selectOption: '',
      examEndTime: '',
      systemTime: 0,
      routerIndex: 0,
      scoreState: false,
      markType: 1
    }
  },
  created() {
    const { examId, userId, preview, examEndTime, showType } =
      this.$route.params
    this.userId = userId
    this.examId = examId
    this.preview = JSON.parse(preview)
    this.examEndTime = examEndTime
    this.showType = Number(showType)
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      await this.setTime()
      const res = await examGet({ id: this.examId })
      this.examName = res.data.name
      this.scoreState = res.data.scoreState === 1
      this.paperId = res.data.paperId
      await this.queryPaper()
      await this.queryPaperInfo()
      await this.queryAnswerInfo()
    },
    // 校准时间差
    async setTime() {
      const systemTime = await loginSysTime({})
      const times = new Date(systemTime.data) - new Date()
      this.systemTime =
        new Date(this.examEndTime).getTime() - (new Date().getTime() + times)
    },
    // 查询试卷
    async queryPaper() {
      const res = await paperGet({
        id: this.paperId
      })
      this.markType = res.data.markType
      this.genType = res.data.genType
    },
    // 查询试卷信息
    async queryPaperInfo() {
      let res
      if (this.genType === 1) {
        res = await paperQuestions({
          id: this.paperId
        })
      } else {
        res = await paperRandomQuestions({
          examId: this.examId,
          userId: this.userId || this.$store.getters.userId
        })
      }

      this.paperQuestion = res.data
    },
    // 查询我的答案信息
    async queryAnswerInfo() {
      try {
        let res
        if (this.userId) {
          res = await myMarkAnswerList({
            examId: this.examId,
            userId: this.userId
          })
        } else {
          res = await myExamAnswerList({
            examId: this.examId
          })
        }

        const paperQuestion = this.paperQuestion.reduce((acc, cur) => {
          acc.push(...cur.questionList)
          return acc
        }, [])

        // 组合试卷答案信息
        this.myExamDetailCache = res.data.reduce((acc, cur, index) => {
          if (
            cur.questionType === 3 &&
            paperQuestion[index].id === cur.questionId
          ) {
            cur.answers.length = paperQuestion[index].answers.length
          }

          acc[cur.questionId] = cur
          return acc
        }, {})

        // 添加是否作答标记submit
        this.paperQuestion.map((item, paperIndex) => {
          item.questionList.map((question, questionIndex) => {
            const submit = res.data.some((answer) => {
              if (answer.questionId === question.id) {
                const list = answer.answers
                let status
                if (list.length) {
                  status = list.some((item) => item.trim())
                } else {
                  status = false
                }
                return status
              }
            })
            this.$set(
              this.paperQuestion[paperIndex].questionList[questionIndex],
              'submit',
              submit
            )
          })
        })
      } catch (error) {
        this.$message.error(error)
      }
    },
    // 更新答案
    async updateAnswer(questionId) {
      if (this.preview) {
        return
      }

      if (!this.myExamDetailCache[questionId]) {
        this.$message.error('提交答案失败，请联系管理员！')
        return
      }
      const res = await myExamAnswer({
        examId: this.examId,
        questionId,
        myExamDetailId: this.myExamDetailCache[questionId].myExamDetailId,
        answers: this.myExamDetailCache[questionId].answers
      })

      res?.code === 200 && this.isEmpty(questionId)
    },
    // 标记是否作答
    isEmpty(questionId) {
      const list = this.myExamDetailCache[questionId].answers
      let status
      if (list.length) {
        status = list.some((item) => item.trim())
      } else {
        status = false
      }
      this.paperQuestion.map((item, paperIndex) => {
        item.questionList.some((question, questionIndex) => {
          if (question.id === questionId) {
            this.$set(
              this.paperQuestion[paperIndex].questionList[questionIndex],
              'submit',
              status
            )
            return true
          }
        })
      })
    },
    // 考试结束
    async examEnd() {
      this.$confirm('确认要提交试卷吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        this.examFinish()
      })
    },
    // 倒计时结束，强制交卷
    async forceExamEnd() {
      this.$alert('考试时间到，已强制交卷！', {
        confirmButtonText: '确定',
        type: 'info',
        showClose: false
      }).then(async() => {
        this.examFinish()
        this.$router.replace({
          name: 'Home'
        })
      })
    },
    // 结束考试
    async examFinish() {
      const res = await myExamFinish({ examId: this.examId })
      res?.code === 200
        ? this.markType === 1
          ? this.$router.replace({
            name: 'MyExamResult',
            params: {
              examId: this.examId,
              scoreState: this.scoreState
            }
          })
          : this.$router.replace({
            name: 'Home'
          })
        : this.$message.warning('请重新提交试卷！')
    },
    // 定位锚点
    toHref(index) {
      this.routerIndex = index
      if (this.showType === 1) {
        document
          .querySelector(`#p-${index}`)
          .scrollIntoView({ block: 'end', inline: 'nearest' })
      }
    },
    // 双击标记
    sign(index) {
      this.paperQuestion.map((item, paperIndex) => {
        item.questionList.some((question, questionIndex) => {
          if (question.id === index) {
            const sign =
              this.paperQuestion[paperIndex].questionList[questionIndex].sign
            this.$set(
              this.paperQuestion[paperIndex].questionList[questionIndex],
              'sign',
              !sign
            )
            return true
          }
        })
      })
    },
    // 上一题
    prevQuestion(index) {
      this.routerIndex = index
    },
    // 下一题
    nextQuestion(index) {
      this.routerIndex = index
    }
  }
}
</script>
<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
