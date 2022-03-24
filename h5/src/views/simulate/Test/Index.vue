<template>
  <div class="container">
    <TestRouter
      :questionIds="questionIds"
      :routerIndex="routerIndex"
      @toHref="toHref"
    ></TestRouter>
    <TestQuestion
      ref="testQuestion"
      :commentState="commentState"
      :questionDetail="questionDetail"
      @randomTest="randomTest"
      @prevQuestion="prevQuestion"
      @nextQuestion="nextQuestion"
    ></TestQuestion>
  </div>
</template>

<script>
import {
  questionTypeOpenQuestionIds,
  questionTypeOpenQuestionGet,
} from 'api/question'
import TestRouter from './TestRouter.vue'
import TestQuestion from './TestQuestion.vue'
export default {
  components: {
    TestRouter,
    TestQuestion,
  },
  data() {
    return {
      questionIds: [],
      commentList: [],
      questionList: [],
      initQuestionIds: [],
      questionDetail: {},
      commentState: 0,
      currentIndex: 0,
      routerIndex: null,
      questionTypeId: null,
    }
  },
  created() {
    const { questionTypeId, commentState } = this.$route.params
    this.questionTypeId = questionTypeId
    this.commentState = Number(commentState)
    this.query()
  },
  methods: {
    // 获取试题列表
    async query() {
      const { data } = await questionTypeOpenQuestionIds({
        questionTypeId: this.questionTypeId,
      })
      this.questionIds = data
      this.initQuestionIds = data
      this.questionList.length = this.questionIds.length
      this.questionList.fill({}, 0, this.questionIds.length)
      this.setQuestionList(this.questionIds[0])
    },
    // 更新试题列表
    async setQuestionList(questionId) {
      const index = this.questionIds.findIndex((item) => item === questionId)
      this.routerIndex = questionId
      if (Object.keys(this.questionList[index]).length) {
        this.questionDetail = this.questionList[index]
        return
      }
      const questionDetail = await this.getQuestionDetail(questionId)
      this.questionList[index] = this.questionDetail = {
        ...questionDetail,
        selected: questionDetail.type === 2 ? [] : '',
        finish: false,
      }
    },
    // 获取试题信息
    async getQuestionDetail(questionId) {
      const res = await questionTypeOpenQuestionGet({ questionId })
      if (res?.code != 200) {
        this.$message.error('获取详情失败！请重试')
        return
      }
      return res.data
    },
    // 锚点定位
    async toHref(questionId) {
      await this.setQuestionList(questionId)
      this.getComment()
    },
    // 上一题
    async prevQuestion() {
      const index = this.questionIds.findIndex(
        (item) => item === this.routerIndex
      )
      if (!index) {
        this.$message.warning('已经到达第一题')
        return
      }
      await this.setQuestionList(this.questionIds[index - 1])
      this.getComment()
    },
    // 下一题
    async nextQuestion() {
      const index = this.questionIds.findIndex(
        (item) => item === this.routerIndex
      )
      if (index === this.questionIds.length - 1) {
        this.$message.warning('已经到达最后一题')
        return
      }
      await this.setQuestionList(this.questionIds[index + 1])
      this.getComment()
    },
    // 随机测试
    randomTest(e) {
      this.questionIds = e
        ? this.questionIds.sort(() => Math.random() - 0.5)
        : [...this.initQuestionIds]
      this.questionList = []
      this.questionList.length = this.questionIds.length
      this.questionList.fill({}, 0, this.questionIds.length)
      this.setQuestionList(this.questionIds[0])
    },
    // 获取评论
    getComment() {
      if (this.$refs.testQuestion.commentDetail) {
        this.$refs.testQuestion.commentList = []
        this.$refs.testQuestion.getQuestionComment()
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  width: 1200px;
  margin: -20px auto 0;
}
.question-details {
  background: #fff;
  padding: 20px 30px;
  position: relative;
  flex: 1;
  .question-detail {
    padding: 0;
  }
  .question-pages {
    position: absolute;
    top: 20px;
    right: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
.question-comments {
  background: #f2f2f5;
  margin-top: 20px;
  padding: 20px;
}
</style>
