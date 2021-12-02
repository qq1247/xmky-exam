<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <el-link
        :underline="false"
        @click="goBack"
        class="head-left"
        icon="el-icon-back"
        >返回</el-link
      >
      <span>{{ paperName }}</span>
      <el-link :underline="false" class="head-right">
        <i class="common common-explain"></i>
      </el-link>
    </div>

    <!-- 内容 -->
    <div class="content">
      <div class="content-center">
        <div class="paper-title">{{ paper.name }}</div>

        <page-show
          v-if="showType === '1'"
          :preview="preview"
          :paperQuestion="paperQuestion"
          :myExamDetailCache="myExamDetailCache"
          @updateAnswer="updateAnswer"
        ></page-show>

        <question-show
          v-if="showType === '3'"
          :preview="preview"
          :router-index="routerIndex"
          :paperQuestion="paperQuestion"
          :myExamDetailCache="myExamDetailCache"
          @updateAnswer="updateAnswer"
          @prevQuestion="prevQuestion"
          @nextQuestion="nextQuestion"
        ></question-show>
      </div>

      <question-router
        v-if="paperQuestion.length"
        :preview="preview"
        :show-type="showType"
        :system-time="systemTime"
        :router-index="routerIndex"
        :href-pointer="hrefPointer"
        :paperQuestion="paperQuestion"
        :question-router="questionRouter"
        @sign="sign"
        @toHref="toHref"
        @examEnd="examEnd"
        @forceExamEnd="forceExamEnd"
      ></question-router>
    </div>
  </div>
</template>
<script>
import { loginSysTime } from 'api/common'
import { paperGet, paperQuestionList } from 'api/paper'
import { myExamAnswerList, myExamAnswer, myExamFinish } from 'api/my'
import PageShow from 'components/PaperContent/PageShow.vue'
import QuestionShow from 'components/PaperContent/QuestionShow.vue'
import QuestionRouter from 'components/PaperContent/QuestionRouter.vue'
export default {
  components: {
    PageShow,
    QuestionShow,
    QuestionRouter,
  },
  data() {
    return {
      id: 0,
      examId: null,
      paperId: null,
      paperName: '',
      hrefPointer: '',
      showType: 1,
      preview: false,
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      paperList: [],
      paperQuestion: [],
      myExamDetailCache: {},
      selectOption: '',
      paper: {},
      questionRouter: [],
      examEndTime: '',
      systemTime: 0,
      routerIndex: 0,
    }
  },
  created() {
    const { id, examId, paperId, preview, examEndTime, showType } =
      this.$route.query
    this.id = id
    this.examId = examId
    this.paperId = paperId
    this.preview = preview
    this.examEndTime = examEndTime
    this.showType = showType
    this.init()
  },
  methods: {
    // 返回
    goBack() {
      this.$router.back()
    },
    // 初始化
    async init() {
      await this.setTime()
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
      try {
        const res = await paperGet({
          id: this.paperId,
        })
        this.paper = { ...res.data }
      } catch (error) {}
    },
    // 查询试卷信息
    async queryPaperInfo() {
      try {
        const res = await paperQuestionList({
          id: this.paperId,
        })
        if (this.showType === '1') {
          this.paperQuestion = res.data
          this.questionRouter = Array.from(res.data.keys())
        } else {
          const paperQuestion = res.data.reduce((acc, cur) => {
            acc.push(...cur.questionList)
            return acc
          }, [])
          this.paperQuestion = paperQuestion
        }
      } catch (error) {}
    },
    // 查询我的答案信息
    async queryAnswerInfo() {
      try {
        const res = await myExamAnswerList({
          examId: this.examId,
        })

        let paperQuestion
        if (this.showType === '1') {
          paperQuestion = this.paperQuestion.reduce((acc, cur) => {
            acc.push(...cur.questionList)
            return acc
          }, [])
        } else {
          paperQuestion = this.paperQuestion
        }

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
        if (this.showType === '1') {
          this.paperQuestion.map((item, indexi) => {
            item.questionList.map((question, indexq) => {
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
                this.paperQuestion[indexi].questionList[indexq],
                'submit',
                submit
              )
            })
          })
        } else {
          this.paperQuestion.map((item, index) => {
            const submit = res.data.some((answer) => {
              if (answer.questionId === item.id) {
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
            this.$set(this.paperQuestion[index], 'submit', submit)
          })
        }
      } catch (error) {
        this.$message.error(error)
      }
    },
    // 更新答案
    async updateAnswer(questionId) {
      if (this.preview === 'true') {
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
        answers: this.myExamDetailCache[questionId].answers,
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
      if (this.showType === '1') {
        this.paperQuestion.map((item, indexi) => {
          item.questionList.some((question, indexq) => {
            if (question.id === questionId) {
              this.$set(
                this.paperQuestion[indexi].questionList[indexq],
                'submit',
                status
              )
              return true
            }
          })
        })
      } else {
        this.paperQuestion.some((item, index) => {
          if (item.id === questionId) {
            this.$set(this.paperQuestion[index], 'submit', status)
            return true
          }
        })
      }
    },
    // 考试结束
    async examEnd() {
      this.$confirm('确认要提交试卷吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await myExamFinish({ examId: this.examId })
        res?.code === 200
          ? this.$router.replace({
              path: '/my',
            })
          : this.$message.warning('请重新提交试卷！')
      })
    },
    // 倒计时结束，强制交卷
    async forceExamEnd() {
      this.$alert('考试时间到，已强制交卷！', {
        confirmButtonText: '确定',
        type: 'info',
        showClose: false,
      }).then(async () => {
        const res = await myExamFinish({ examId: this.examId })
        res?.code === 200
          ? this.$router.replace({
              path: '/my',
            })
          : this.$message.warning('请重新提交试卷！')
      })
    },
    // 定位锚点
    toHref(index) {
      if (this.showType === '1') {
        this.hrefPointer = `#p-${index}`
        document.documentElement.scrollTop =
          document.querySelector(this.hrefPointer).offsetTop - 50
      } else {
        this.routerIndex = index
      }
    },
    // 双击标记
    sign(index) {
      if (this.showType === '1') {
        this.paperQuestion.map((item, indexi) => {
          item.questionList.some((question, indexq) => {
            if (question.id === index) {
              const sign = this.paperQuestion[indexi].questionList[indexq].sign
              this.$set(
                this.paperQuestion[indexi].questionList[indexq],
                'sign',
                !sign
              )
              return true
            }
          })
        })
      } else {
        this.paperQuestion.some((item, indexi) => {
          if (indexi === index) {
            const sign = this.paperQuestion[indexi].sign
            this.$set(this.paperQuestion[indexi], 'sign', !sign)
            return true
          }
        })
      }
    },
    // 上一题
    prevQuestion() {
      if (this.routerIndex === 0) {
        this.$message.warning('请您继续作答！')
        return
      }
      this.routerIndex -= 1
    },
    // 下一题
    nextQuestion() {
      if (this.routerIndex === this.paperQuestion.length - 1) {
        this.$message.warning('恭喜您已经作答完毕！')
        return
      }
      this.routerIndex += 1
    },
  },
}
</script>
<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
