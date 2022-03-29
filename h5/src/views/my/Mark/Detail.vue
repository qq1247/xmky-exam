<template>
  <div class="content">
    <!-- 考生信息 -->
    <el-scrollbar wrap-style="overflow-x:hidden;" class="content-left">
      <div class="user-info">
        <el-avatar :size="64" v-if="userInfo">{{
          (userInfo.userName && userInfo.userName.slice(0, 1)) || '头像'
        }}</el-avatar>
        <el-select v-model="userId" placeholder="请选择" @change="changeUser">
          <el-option
            v-for="item in userList"
            :key="item.userId"
            :label="item.userName"
            :value="item.userId"
          >
            <span style="float: left">{{ item.userName }}</span>
            <span style="float: right; color: #8492a6; font-size: 12px">{{
              item.orgName
            }}</span>
          </el-option>
        </el-select>
      </div>
      <div class="user-intro">
        <div class="intro-item">
          <div class="item-title"><span>得分</span></div>
          <div class="item-num">
            {{ userInfo.totalScore === null ? '--' : userInfo.totalScore }}
          </div>
        </div>
        <div class="intro-item">
          <div class="item-title"><span>答题用时</span></div>
          <div class="item-num">
            {{
              computeMinute(userInfo.answerStartTime, userInfo.answerEndTime)
            }}
          </div>
        </div>
        <div class="intro-item">
          <div class="item-title"><span>成绩</span></div>
          <div class="item-num">
            {{
              (
                (userInfo.totalScore / userInfo.paperTotalScore) *
                100
              ).toFixed() >= userInfo.paperPassScore
                ? '通过'
                : '未通过'
            }}
          </div>
        </div>
        <div class="intro-item">
          <div class="item-title"><span>阅卷用时</span></div>
          <div class="item-num">
            {{ computeMinute(userInfo.markStartTime, userInfo.markEndTime) }}
          </div>
        </div>
      </div>
      <div class="user-handler" v-if="!preview">
        <el-row>
          <el-col :span="14">只看未阅：</el-col>
          <el-col :span="10">
            <el-switch
              v-model="isFilter"
              active-color="#0094e5"
              inactive-color="#dcdfe6"
              @change="filterUserList"
            >
            </el-switch>
          </el-col>
        </el-row>
        <div>当前进度：{{ markEndNum }}/{{ userList.length }}</div>
        <div class="">
          <el-progress :percentage="percentage" :color="customColors">
          </el-progress>
        </div>
      </div>
    </el-scrollbar>

    <!-- 内容 -->
    <div class="content-center">
      <div class="paper-title">{{ paper.name }}</div>

      <template v-if="questionList.length">
        <!-- 章节 -->
        <div class="chapter">
          <div class="chapter-item">
            <div class="item-title">{{ chapter.name }}</div>
            <div></div>
          </div>
          <div class="chapter-description" v-html="chapter.description"></div>
        </div>

        <!-- 试题 -->
        <div
          class="children-content"
          v-for="(item, indexQuestion) in questionList"
          :key="item.id"
          style="border-bottom: 1px solid #f3f3f3"
        >
          <template v-if="routerQuestionId === item.id">
            <div class="question-title">
              <div>{{ indexQuestion + 1 }}、</div>
              <div v-html="`${item.title}`"></div>
            </div>

            <div class="children-analysis">
              <el-row :gutter="10">
                <template v-if="item.type === 3">
                  <el-col :span="2.5">【答案】：</el-col>
                  <el-col :span="21">
                    <div
                      v-for="(answer, indexAnswers) in item.answers"
                      :key="answer.id"
                      class="answers-item"
                    >
                      <span>{{
                        `填空${$tools.intToChinese(indexAnswers + 1)}、`
                      }}</span>
                      <span
                        class="answers-tag"
                        v-for="(ans, indexAnswer) in answer.answer"
                        :key="indexAnswer"
                        >{{ ans }}</span
                      >
                    </div>
                  </el-col>
                </template>
                <template v-if="item.type === 5">
                  <el-col :span="2.5"> 【答案】： </el-col>
                  <el-col :span="21">
                    <template v-if="item.ai === 1">
                      <div
                        v-for="(answer, indexAnswers) in item.answers"
                        :key="answer.id"
                        class="answers-item"
                      >
                        <span>{{
                          `关键词${$tools.intToChinese(indexAnswers + 1)}、`
                        }}</span>
                        <span
                          class="answers-tag"
                          v-for="(ans, indexAnswer) in answer.answer"
                          :key="indexAnswer"
                          >{{ ans }}</span
                        >
                      </div>
                    </template>
                    <div
                      v-if="item.ai === 2"
                      v-html="`${item.answers[0].answer}`"
                    ></div>
                  </el-col>
                </template>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="2.5"> 【解析】： </el-col>
                <el-col :span="21">
                  <div v-html="`${item.analysis}`"></div>
                </el-col>
              </el-row>
            </div>

            <div class="user-answer">
              <el-row :gutter="10" style="margin-bottom: 10px">
                <el-col :span="2.5">【用户答案】</el-col>
                <el-col :span="21">
                  <template
                    v-if="
                      item.type === 3 && myExamDetailCache[routerQuestionId]
                    "
                  >
                    <div
                      v-for="(answer, indexCache) in myExamDetailCache[
                        routerQuestionId
                      ].answers"
                      :key="answer.id"
                      class="answers-item"
                    >
                      <span>{{
                        `填空${$tools.intToChinese(indexCache + 1)}、${answer}`
                      }}</span>
                    </div>
                  </template>
                  <div
                    v-if="
                      item.type === 5 && myExamDetailCache[routerQuestionId]
                    "
                    v-html="`${myExamDetailCache[routerQuestionId].answers}`"
                  ></div>
                </el-col>
              </el-row>
            </div>

            <div class="user-plate">
              <el-row :gutter="10">
                <el-col :span="2.5" style="letter-spacing: 9px">
                  【得分】
                </el-col>
                <el-col :span="21">
                  <span style="margin-right: 15px" v-if="preview">{{
                    item.scorePlate
                  }}</span>
                  <div v-if="!preview">
                    <el-input
                      class="score-input"
                      v-model.number="item.scorePlate"
                      @change="scoreChange"
                      @input="(e) => computeScore(e, indexQuestion, item.score)"
                    ></el-input>
                    <span>（本题满分：{{ item.score }}）</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </div>

        <!-- 答题卡 -->
        <div class="mark-router">
          <div class="router-content">
            <p class="router-title">答题卡</p>
            <div>
              <a
                :class="[
                  'router-index',
                  routerQuestionId === item.id ? 'router-active' : '',
                ]"
                v-for="(item, indexRoute) in questionList"
                :key="item.id"
                @click="routerQuestionId = item.id"
                >{{ indexRoute + 1 }}</a
              >
            </div>
          </div>
          <ScorePlate
            v-if="!preview"
            ref="scorePlate"
            :data="questionDetail"
            @selectScore="selectScore"
            @nextQuestion="nextQuestion"
            @nextPaper="nextPaper"
          ></ScorePlate>
        </div>
      </template>
      <el-empty v-else description="暂无试卷"></el-empty>
    </div>
  </div>
</template>
<script>
import { paperGet, paperQuestionList } from 'api/paper'
import {
  myMarkUser,
  myMarkUserList,
  myMarkAnswerList,
  myMarkScore,
  myMarkFinish,
} from 'api/my'
import ScorePlate from 'components/ScorePlate.vue'
import ClozeTitle from '@/components/ClozeTitle.vue'
export default {
  components: {
    ScorePlate,
    ClozeTitle,
  },
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
      customColors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 },
      ],
      percentage: 0,
      markEndNum: 0,
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
      try {
        const res = await paperGet({
          id: this.paperId,
        })
        this.paper = res.data
      } catch (error) {}
    },
    // 查询试卷信息
    async queryPaperInfo() {
      try {
        const res = await paperQuestionList({
          id: this.paperId,
          examId: this.examId,
          userId: this.userId,
        })
        this.paperQuestion = res.data
        this.questionList = res.data.reduce((acc, cur) => {
          const filterQuestion = cur.questionList.filter(
            (question) => question.ai === 2
          )
          acc.push(...filterQuestion)
          return acc
        }, [])
      } catch (error) {}
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
      this.userInfo = userInfo
      this.routerQuestionId = userInfo.id
    },
    // 选择考生
    changeUser(userId) {
      this.userId = userId
      this.queryOneExamineeInfo()
      this.queryAnswerInfo()
      this.$refs.scorePlate.createScores(0.5)
      this.$refs.scorePlate.step = ''
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

      if (Number(e) < 0)
        this.$set(this.questionList[indexActivate], 'scorePlate', 0)
      if (Number(e) > source.score)
        this.$set(this.questionList[indexActivate], 'scorePlate', source.score)
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
    // 计算分钟数
    computeMinute(startTime, endTime) {
      if (!startTime || !endTime) {
        return '--'
      }
      const diffTime =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      const minutes = Math.ceil(diffTime / (60 * 1000))
      return `${minutes}分钟`
    },
  },
}
</script>
<style lang="scss" scoped>
@import 'assets/style/exam.scss';
/deep/ .el-select {
  width: 80%;
  margin-top: 20px;
}

.content-left {
  top: 70px;
  left: calc(100% / 2 - 570px);
}

.mark-router {
  display: flex;
  margin: 20px 0;
}

.router-content {
  display: flex;
  flex-direction: column;
  border-radius: 5px;
  width: 210px;
  margin: 0 50px 0 0;
  padding: 0 13px;
  border: 1px solid #d9d9d9;
}

.user-answer {
  background: #404b65;
  padding: 10px 20px;
  color: #fff;
  line-height: 30px;
}

.user-plate {
  padding: 0 20px;
  line-height: 50px;
}

.score-input {
  width: 100px;
  /deep/ & .el-input__inner {
    width: 100%;
    background-color: transparent;
    border: none;
    border-bottom: 1px solid #333;
    border-radius: 0;
    height: 20px;
  }
}
</style>
