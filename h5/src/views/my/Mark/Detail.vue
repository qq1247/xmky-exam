<template>
  <div class="content">
    <!-- 考生信息 -->
    <div class="content-left">
      <div class="user-info">
        <el-avatar
          v-if="userInfo.userHeadFileId"
          :size="64"
          :src="`/api/file/download?id=${Number(userInfo.userHeadFileId)}`"
          ><i class="common common-wo"
        /></el-avatar>
        <el-select v-model="userId" placeholder="请选择" @change="changeUser">
          <el-option
            v-for="(item, index) in userList"
            :key="item.userId"
            :label="item.userName || `匿名${index + 1}`"
            :value="item.userId"
          >
            <span style="float: left">{{
              item.userName || `匿名${index + 1}`
            }}</span>
            <span style="float: right; color: #8492a6; font-size: 12px">{{
              item.orgName || '--'
            }}</span>
          </el-option>
        </el-select>
      </div>
      <div class="user-intro">
        <div class="intro-item">
          <img
            class="item-icon"
            src="~@/assets/img/mark/mark-score.png"
            alt=""
          />
          <div class="item-num">
            {{ userInfo.totalScore === null ? '--' : userInfo.totalScore }}
          </div>
          <div class="item-title">得分</div>
        </div>
        <div class="intro-item">
          <img
            class="item-icon"
            src="~@/assets/img/mark/mark-time.png"
            alt=""
          />
          <div class="item-num">
            {{
              $tools.computeMinute(
                userInfo.answerStartTime,
                userInfo.answerEndTime
              )
            }}
          </div>
          <div class="item-title">答题用时</div>
        </div>
        <div class="intro-item">
          <img
            class="item-icon"
            src="~@/assets/img/mark/mark-pass.png"
            alt=""
          />
          <div class="item-num">
            {{
              userInfo.totalScore === null
                ? '--'
                : computePass(
                    userInfo.totalScore,
                    userInfo.paperTotalScore,
                    userInfo.paperPassScore
                  )
            }}
          </div>
          <div class="item-title">成绩</div>
        </div>
        <div class="intro-item">
          <img
            class="item-icon"
            src="~@/assets/img/mark/mark-answer-time.png"
            alt=""
          />
          <div class="item-num">
            {{
              $tools.computeMinute(userInfo.markStartTime, userInfo.markEndTime)
            }}
          </div>
          <div class="item-title">阅卷用时</div>
        </div>
      </div>
      <div v-if="!preview" class="user-handler">
        <div class="handler-title">
          只看未阅：<el-switch
            v-model="isFilter"
            active-color="#0094e5"
            inactive-color="#dcdfe6"
            @change="filterUserList"
          />
        </div>
        <div class="handler-content">
          <div class="percentage-bg">
            <div class="percentage-content">
              <p style="color: #8b989d">当前进度</p>
              <p style="color: #0094e5; font-size: 18px">
                {{ markEndNum }}&nbsp;/&nbsp;{{ userList.length }}
              </p>
            </div>
          </div>
          <el-progress type="circle" :percentage="percentage" color="#0094e5" />
        </div>
      </div>
    </div>

    <!-- 内容 -->
    <div class="content-center">
      <template v-if="questionList.length">
        <!-- 试题 -->
        <div
          v-for="(item, indexQuestion) in questionList"
          :key="item.id"
          class="question-content"
          :style="{ display: routerQuestionId === item.id ? 'block' : 'none' }"
        >
          <template v-if="routerQuestionId === item.id">
            <div class="question-title tag">
              <template v-if="item.type === 5">
                <div>{{ indexQuestion + 1 }}、</div>
                <div v-html="`${item.title}`" />
              </template>
              <template v-if="item.type === 3">
                <div>{{ indexQuestion + 1 }}、</div>
                <ClozeTitle
                  :disabled="true"
                  :title="item.title"
                  :question-id="item.id"
                  :question-detail="item"
                  :my-exam-detail-cache="myExamDetailCache"
                />
              </template>
            </div>

            <el-input
              disabled
              resize="none"
              type="textarea"
              :autosize="true"
              class="user-answer tag"
              v-model="myExamDetailCache[routerQuestionId].answers[0]"
              v-if="item.type === 5 && myExamDetailCache[routerQuestionId]"
            />

            <div class="user-plate tag">
              <span>本题得</span>
              <span v-if="preview">&nbsp;{{ item.scorePlate }}&nbsp;</span>
              <el-input
                v-if="!preview"
                v-model.number="item.scorePlate"
                class="score-input"
                @change="scoreChange"
                @input="(e) => computeScore(e, indexQuestion, item.score)"
              />
              <span>分</span>
              <span style="color: #0094e5"
                >（本题满分：{{ item.score }}分）</span
              >
            </div>

            <div class="children-analysis">
              <div class="analysis-title">
                <div>查看解析</div>
              </div>
              <div class="analysis-content">
                <el-row :gutter="10">
                  <template v-if="item.type === 3">
                    <el-col :span="2.5">答案：</el-col>
                    <el-col :span="21">
                      <div class="cloze-answers">
                        <span
                          v-for="answer in item.answers"
                          :key="answer.id"
                          class="answers-items"
                        >
                          {{ answer.answer.join(' | ') }}
                        </span>
                      </div>
                    </el-col>
                  </template>
                  <template v-if="item.type === 5">
                    <el-col :span="2.5">答案： </el-col>
                    <el-col :span="21">
                      <div v-html="`${item.answers[0].answer}`" />
                    </el-col>
                  </template>
                </el-row>
                <el-row :gutter="10">
                  <el-col :span="2.5">解析： </el-col>
                  <el-col :span="21">
                    <div v-html="`${item.analysis}`" />
                  </el-col>
                </el-row>
              </div>
            </div>
          </template>
        </div>

        <!-- 答题卡 -->
        <div class="router-content">
          <div class="router-title">
            <div class="router-mark">正确题</div>
            <div class="router-mark">未批阅</div>
            <div class="router-mark">当前题</div>
            <div class="router-mark">错误题</div>
          </div>
          <div class="router-list">
            <a
              v-for="(item, indexRoute) in questionList"
              :key="item.id"
              :class="[
                'router-index',
                item.scorePlate > 0 ? 'router-success' : 'router-error',
                routerQuestionId === item.id ? 'router-active' : '',
              ]"
              @click="routerQuestionId = item.id"
              >{{ indexRoute + 1 }}</a
            >
          </div>
        </div>

        <ScorePlate
          v-if="!preview"
          ref="scorePlate"
          v-el-drag-dialog
          :score="questionDetail.score"
          :data="questionDetail"
          @nextPaper="nextPaper"
          @selectScore="selectScore"
          @nextQuestion="nextQuestion"
        />
      </template>
      <el-empty v-else description="暂无试卷" />
    </div>
  </div>
</template>
<script>
import { paperGet } from 'api/paper'
import { myMarkPaper } from 'api/my'
import ClozeTitle from '@/components/ClozeTitle.vue'
import {
  myMarkUser,
  myMarkUserList,
  myMarkAnswerList,
  myMarkScore,
  myMarkFinish,
} from 'api/my'
import elDragDialog from '@/directive/el-drag-dialog'
import ScorePlate from 'components/ScorePlate.vue'
export default {
  components: {
    ClozeTitle,
    ScorePlate,
  },
  directives: { elDragDialog },
  data() {
    return {
      questionDetail: {},
      routerQuestionId: 0,
      paperId: null,
      examId: null,
      userId: null,
      allUserList: [],
      userList: [],
      userInfo: {},
      isFilter: false,
      paperQuestion: [],
      questionList: [],
      chapter: {},
      myExamDetailCache: {},
      paper: {},
      preview: false,
      dialogPlateVisible: false,
      percentage: 0,
      markEndNum: 0,
      score: 0,
    }
  },
  watch: {
    routerQuestionId: {
      deep: true,
      immediate: true,
      handler(n) {
        this.questionList.length && this.getQuestion(n)
      },
    },
  },
  created() {
    const { examId, paperId, preview, userId } = this.$route.params
    this.examId = examId
    this.paperId = paperId
    this.preview = JSON.parse(preview)
    this.dialogPlateVisible = !this.preview
    this.userId = Number(userId)
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      await this.queryPaper()
      await this.queryExamineeInfo()
      await this.queryPaperInfo()
      await this.queryAnswerInfo()
      this.getQuestion()
      this.updatePercentage()
    },
    // 更新阅卷进度条
    updatePercentage() {
      const num = this.userList.filter((item) => item.markState === 3)
      const percentage = (num.length / this.userList.length) * 100
      this.percentage = percentage ? Number(percentage.toFixed()) : 0
      this.markEndNum = num.length
    },
    // 查询试卷
    async queryPaper() {
      const res = await paperGet({
        id: this.paperId,
      })
      this.paper = res.data
    },
    // 查询试卷信息
    async queryPaperInfo() {
      const res = await myMarkPaper({
        examId: this.examId,
        userId: this.userId
      })
      this.paperQuestion = res.data
      this.questionList = res.data.reduce((acc, cur) => {
        const filterQuestion = cur.questionList.filter(
          (question) => question.ai === 2
        )
        acc.push(...filterQuestion)
        return acc
      }, [])
    },
    // 获取当前试题
    getQuestion(routerQuestionId) {
      this.routerQuestionId = Number(
        routerQuestionId || this.questionList[0].id
      )

      this.$nextTick(() => {
        this.questionDetail = this.questionList.find(
          (question) => question.id === this.routerQuestionId
        )
      })

      // 查找试题所在的章节信息
      this.paperQuestion.some((item) => {
        const question = item.questionList.some(
          (question) => question.id === this.routerQuestionId
        )
        if (question) {
          this.chapter = item.chapter
          return true
        }
      })
    },
    // 查询所有考生信息
    async queryExamineeInfo() {
      const userList = await myMarkUserList({
        examId: Number(this.examId),
      })
      this.allUserList = userList.data.filter((user) => user.state !== 1)
      this.userList = userList.data.filter((user) => user.state !== 1)
      this.userId = this.userId || this.userList[0].userId
      this.queryOneExamineeInfo()
    },
    // 查询单个考生信息
    async queryOneExamineeInfo() {
      const userInfo = await myMarkUser({
        examId: Number(this.examId),
        userId: this.userId,
      })
      this.userInfo = userInfo.data
      this.routerQuestionId = userInfo.data.id
    },
    // 选择考生
    changeUser(userId) {
      this.userId = userId
      this.queryOneExamineeInfo()
      this.queryAnswerInfo()
      if (!this.preview) {
        this.$refs.scorePlate.createScores(0.5)
        this.$refs.scorePlate.step = ''
      }
    },
    // 未阅考生列表
    filterUserList(e) {
      const isAllMark = this.allUserList.every((item) => item.markState === 3)
      if (isAllMark) {
        this.$message.success('已经全部阅卷！')
        this.isFilter = !e
        return
      }
      const filterUserNum = this.allUserList.filter(
        (item) => item.markState === 1
      )
      this.userList = e ? filterUserNum : this.allUserList
      this.userId = this.userList[0].userId
      this.queryOneExamineeInfo()
      this.queryAnswerInfo()
    },
    // 查询答案信息
    async queryAnswerInfo() {
      try {
        const res = await myMarkAnswerList({
          examId: this.examId,
          userId: this.userId,
        })

        // 组合试卷答案信息
        this.myExamDetailCache = res.data.reduce((acc, cur) => {
          this.questionList.map((question) => {
            if (question.id === cur.questionId) {
              question.scorePlate = cur.score === null ? '' : cur.score
              acc[cur.questionId] = cur
            }
          })
          return acc
        }, {})
      } catch (error) {
        this.$message.error(error)
      }
    },
    // 填写打分
    scoreChange() {
      this.updateScore()
    },
    // 计算实时分数阈值
    computeScore(e, index, score) {
      this.$forceUpdate()

      if (Number(e) > score) {
        this.$set(this.questionList[index], 'scorePlate', score)
      }
      if (Number(e) < 0) {
        this.$set(this.questionList[index], 'scorePlate', 0)
      }
    },
    // 点击打分板分值
    async selectScore(e) {
      await this.setScore(e)
      this.updateScore()
    },
    // 设置分数
    async setScore(e) {
      if (this.preview) {
        this.$message.warning('阅卷未开始或已结束')
        return false
      }

      const indexActivate = this.questionList.findIndex(
        (question) => question.id === this.routerQuestionId
      )

      const source = this.questionList[indexActivate]
      this.$set(this.questionList[indexActivate], 'scorePlate', Number(e))

      if (Number(e) < 0) {
        this.$set(this.questionList[indexActivate], 'scorePlate', 0)
      }
      if (Number(e) > source.score) {
        this.$set(this.questionList[indexActivate], 'scorePlate', source.score)
      }
      this.$forceUpdate()
    },
    // 打分
    async updateScore() {
      const indexActivate = this.questionList.findIndex(
        (question) => question.id === this.routerQuestionId
      )
      const source = this.questionList[indexActivate]
      const res = await myMarkScore({
        examId: this.examId,
        questionId: source.id,
        userId: this.userId,
        score: source.scorePlate || 0,
      })

      if (res?.code === 200) {
        this.$message('打分成功！')
        const { markType, isNextQuestion } = this.$refs.scorePlate
        if (!markType) {
          return false
        }
        if (markType && isNextQuestion) {
          setTimeout(() => {
            this.nextQuestion()
          }, 1000)
        }
        if (markType && !isNextQuestion) {
          setTimeout(() => {
            this.nextPaper()
          }, 1500)
        }
      } else {
        this.$message.error(res.msg || '打分失败！')
      }
    },
    // 下一题
    async nextQuestion() {
      const indexActivate = this.questionList.findIndex(
        (question) => question.id === this.routerQuestionId
      )
      const questionLength = this.questionList.length

      if (indexActivate < questionLength - 1) {
        this.markEnd()
        this.routerQuestionId = this.questionList[indexActivate + 1].id
      } else {
        this.nextPaper(1)
      }
    },
    // 下一卷
    async nextPaper(state) {
      const indexPaper = this.userList.findIndex(
        (item) => item.userId === this.userId
      )
      await this.markEnd()
      if (indexPaper === this.userList.length - 1) {
        this.$message.warning('已经是最后一卷了！')
        return
      }
      const { markType, isNextQuestion } = this.$refs.scorePlate
      // 自动且下一题 || 人工且未阅卷
      if ((markType && isNextQuestion) || (!markType && state === 1)) {
        this.routerQuestionId = this.questionList[0].id
      }

      this.userId = this.userList[indexPaper + 1].userId
      this.userInfo = this.userList[indexPaper + 1]
      await this.queryAnswerInfo()
    },
    // 完成阅卷
    async markEnd() {
      const isEnd = this.questionList.every((item) => item.scorePlate !== '')
      if (!isEnd) return false
      const indexPaper = this.userList.findIndex(
        (item) => item.userId === this.userId
      )
      this.$set(this.userList[indexPaper], 'markState', 3)
      this.updatePercentage()
      await myMarkFinish({
        examId: this.examId,
        userId: this.userId,
      })
    },
    // 计算分数通过否
    computePass(totalScore, paperTotalScore, paperPassScore) {
      const isPass =
        ((totalScore / paperTotalScore) * 100).toFixed() >= paperPassScore
      return isPass ? '通过' : '未通过'
    },
  },
}
</script>
<style lang="scss" scoped>
@import 'assets/style/exam.scss';
/deep/ .el-select {
  width: 80%;
}

.el-avatar {
  margin-bottom: 16px;
}

.user-info {
  margin-bottom: 0;
  border-radius: 8px 8px 0 0;
}

.user-handler {
  border-radius: 8px;
}

.content-center {
  background: transparent;
  .question-content {
    border-bottom: none;
    position: relative;
    padding: 0;
    margin: 0;
  }
  .children-analysis {
    margin-top: 16px;
    line-height: initial;
    background: #fff;
    border-radius: 8px;
    padding: 0;
  }
  .analysis-title {
    width: 100%;
    height: 35px;
    padding: 5px 20px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    div {
      width: 66px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      background: #e3f4fc;
      border-radius: 2px;
      color: #0094e5;
    }
  }

  .analysis-content {
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
}

.tag {
  background: #fff;
  &::before {
    content: '';
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    border-width: 16px;
    border-color: #2f7ffb transparent transparent #2f7ffb;
    border-style: solid;
    font-size: 12px;
    z-index: 1;
  }
  &::after {
    content: '';
    display: block;
    position: absolute;
    top: 0;
    left: 3px;
    font-size: 12px;
    color: #fff;
    z-index: 2;
  }
}

.question-title {
  padding: 30px 20px;
  position: relative;
  border-radius: 8px 8px 0 0;
  overflow: hidden;
  &::after {
    content: '题';
  }
}

.user-answer {
  background: #404b65;
  padding: 30px 20px;
  color: #fff;
  min-height: 60px;
  position: relative;
  &::before {
    border-color: #03cbc0 transparent transparent #03cbc0;
  }
  &::after {
    content: '答';
  }
}

.user-plate {
  padding: 30px 20px;
  position: relative;
  border-radius: 0 0 8px 8px;
  &::before {
    border-color: #f89124 transparent transparent #f89124;
  }
  &::after {
    content: '分';
  }
}

.score-input {
  width: 50px;
  /deep/ & .el-input__inner {
    width: 100%;
    background-color: transparent;
    border: none;
    border-bottom: 1px solid #333;
    border-radius: 0;
    height: 20px;
  }
}

.router-content {
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  background: #fff;
  margin: 16px 0 0;
  .router-title {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 12px 16px;
    font-weight: normal;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    line-height: initial;
  }
  .router-list {
    padding: 16px;
  }
}

.router-mark {
  margin-left: 45px;
  color: #8b989d;
  font-size: 12px;
  position: relative;
  &::before {
    content: '';
    display: block;
    position: absolute;
    top: 1px;
    left: -20px;
    width: 14px;
    height: 14px;
    background: #5fb878;
  }
  &:nth-child(2)::before {
    background: #e3f4fc;
  }
  &:nth-child(3)::before {
    background: #ff8e19;
  }
  &:nth-child(4)::before {
    background: #ff5722;
  }
}
.router-content {
  .router-success {
    background: #5fb878;
    color: #fff;
  }
  .router-error {
    background: #ff5722;
    color: #fff;
  }
  a:hover,
  .router-active {
    background: #ff8e19;
    color: #fff;
  }
}

/deep/ .el-dialog {
  box-shadow: 0 0 16px -3px rgba(0, 0, 0, 0.15) !important;
}

/deep/ .el-dialog__body {
  padding: 0;
}

/deep/ .el-textarea.is-disabled .el-textarea__inner {
  background-color: transparent;
  color: #fff;
}
</style>
