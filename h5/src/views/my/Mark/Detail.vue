<template>
  <div class="content">
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
            <span>{{ item.userName }}&nbsp;/&nbsp;{{ item.orgName }}</span>
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
        <div class="chapter">
          <div class="chapter-item">
            <div class="item-title">{{ chapter.name }}</div>
            <div></div>
          </div>
          <div class="chapter-description" v-html="chapter.description"></div>
        </div>

        <div
          class="children-content"
          v-for="(item, indexQuestion) in questionList"
          :key="item.id"
          style="border-bottom: 1px solid #f3f3f3"
        >
          <template v-if="index === indexQuestion">
            <div class="question-title">
              <div>{{ index + 1 }}、</div>
              <div v-html="`${item.title}`"></div>
            </div>

            <div class="children-analysis">
              <el-row :gutter="10">
                <template v-if="item.type === 3">
                  <el-col :span="2.5">【答案】：</el-col>
                  <el-col :span="21">
                    <div
                      v-for="(answer, index) in item.answers"
                      :key="answer.id"
                      class="answers-item"
                    >
                      <span>{{
                        `填空${$tools.intToChinese(index + 1)}、`
                      }}</span>
                      <span
                        class="answers-tag"
                        v-for="(ans, index) in answer.answer"
                        :key="index"
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
                        v-for="(answer, index) in item.answers"
                        :key="answer.id"
                        class="answers-item"
                      >
                        <span>{{
                          `关键词${$tools.intToChinese(index + 1)}、`
                        }}</span>
                        <span
                          class="answers-tag"
                          v-for="(ans, index) in answer.answer"
                          :key="index"
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
                    v-if="item.type === 3 && myExamDetailCache[routerIndex]"
                  >
                    <div
                      v-for="(answer, index) in myExamDetailCache[routerIndex]
                        .answers"
                      :key="answer.id"
                      class="answers-item"
                    >
                      <span>{{
                        `填空${$tools.intToChinese(index + 1)}、${answer}`
                      }}</span>
                    </div>
                  </template>
                  <div
                    v-if="item.type === 5"
                    v-html="`${myExamDetailCache[routerIndex].answers}`"
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
                    item.scorePlate || 0
                  }}</span>
                  <div v-if="!preview">
                    <el-input-number
                      v-model="item.scorePlate"
                      controls-position="right"
                      @blur="scoreChange"
                      @change="scoreChange"
                      :min="0"
                      :max="item.score"
                    ></el-input-number>
                    <span>（本题满分：{{ item.score }}）</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </div>
        <div class="mark-router">
          <div class="router-content">
            <p class="router-title">答题卡</p>
            <div>
              <a
                :class="[
                  'router-index',
                  routerIndex === item.id ? 'router-active' : '',
                ]"
                v-for="(item, index) in questionList"
                :key="item.id"
                @click="routerIndex = item.id"
                >{{ index + 1 }}</a
              >
            </div>
          </div>
          <ScorePlate
            v-if="!preview"
            ref="scorePlate"
            :data="questionList[index]"
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
      index: 0,
      routerIndex: 0,
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
      answerList: [],
      preview: false,
      customColors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 },
      ],
      percentage: 0,
    }
  },
  computed: {
    markEndNum() {
      const num = this.userList.filter((item) => item.markState === 3)
      const percentage = (num.length / this.userList.length) * 100
      this.percentage = percentage ? Number(percentage.toFixed()) : 0
      return num.length
    },
  },
  watch: {
    routerIndex: {
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
      await this.queryPaperInfo()
      await this.queryExamineeInfo()
      await this.queryAnswerInfo()
      this.getQuestion()
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
        })
        this.paperQuestion = res.data
        const questionList = res.data.reduce((acc, cur) => {
          const filterQuestion = cur.questionList.filter(
            (question) => question.ai === 2
          )
          acc.push(...filterQuestion)
          return acc
        }, [])
        this.questionList = questionList
      } catch (error) {}
    },
    // 获取当前试题
    getQuestion(routerIndex) {
      this.routerIndex = Number(routerIndex || this.questionList[0].id)

      this.index = this.questionList.findIndex(
        (question) => question.id === this.routerIndex
      )
      // 查找试题所在的章节信息
      this.paperQuestion.some((item) => {
        const question = item.questionList.some(
          (question) => question.id === this.routerIndex
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
      this.allUserList = userList.data
      this.userList = userList.data
      this.userId = this.userId || this.userList[0].userId
      this.queryOneExamineeInfo()
    },
    // 查询单个考生信息
    async queryOneExamineeInfo() {
      const userList = await myMarkUserList({
        examId: Number(this.examId),
        userId: this.userId,
      })
      this.userInfo = userList.data[0]
    },
    // 选择考生
    changeUser(userId) {
      this.userId = userId
      this.queryOneExamineeInfo()
      this.queryAnswerInfo()
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
        this.myExamDetailCache = res.data.reduce((acc, cur, index) => {
          this.questionList.map((question) => {
            if (question.id === cur.questionId) {
              question.scorePlate = cur.score
              acc[cur.questionId] = cur
            }
          })
          return acc
        }, {})
      } catch (error) {
        this.$message.error(error)
      }
    },
    scoreChange() {
      this.updateScore()
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

      const source = this.questionList[this.index]
      this.$set(this.questionList[this.index], 'scorePlate', Number(e))

      if (Number(e) < 0)
        this.$set(this.questionList[this.index], 'scorePlate', 0)
      if (Number(e) > source.score)
        this.$set(this.questionList[this.index], 'scorePlate', source.score)
    },
    // 打分
    async updateScore() {
      const source = this.questionList[this.index]
      const res = await myMarkScore({
        examId: this.examId,
        questionId: source.id,
        userId: this.userId,
        score: source.scorePlate || 0,
      })

      if (res?.code === 200) {
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
      const questionLength = this.questionList.length
      if (this.index < questionLength - 1) {
        await this.markEnd()
        this.index += 1
      } else {
        this.nextPaper()
        this.index = 0
      }
    },
    // 下一卷
    async nextPaper() {
      const index = this.userList.findIndex(
        (item) => item.userId === this.userId
      )
      if (index === this.userList.length - 1) {
        this.$message.warning('已经是最后一卷了！')
        return
      }
      await this.markEnd()
      this.userId = this.userList[index + 1].userId
      this.userInfo = this.userList[index + 1]
      await this.queryAnswerInfo()
    },
    // 完成阅卷
    async markEnd() {
      const isEvery = this.questionList.every(
        (item) => item.scorePlate !== '' || item.scorePlate !== null
      )
      if (!isEvery) return false
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
      const minutes = diffTime / (60 * 1000)
      return `${minutes.toFixed(2)}分钟`
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

/deep/ .el-input-number .el-input .el-input__inner {
  width: 100%;
  background-color: transparent;
  border: none;
  border-bottom: 1px solid #333;
  border-radius: 0;
  height: 20px;
}
/deep/ .el-input-number .el-input-number__decrease,
/deep/ .el-input-number .el-input-number__increase {
  display: none;
}
</style>
