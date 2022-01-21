<template>
  <div class="container">
    <div class="content">
      <el-scrollbar wrap-style="overflow-x:hidden;" class="content-left">
        <div class="user-info">
          <el-avatar :size="80" v-if="userInfo">{{
            (userInfo.userName && userInfo.userName.slice(0, 1)) || '头像'
          }}</el-avatar>
          <div class="user-name">
            {{ userInfo.userName || '***' }}
          </div>
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
        <div class="user-handler">
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

        <template v-if="paperQuestion.length > 0">
          <div :key="index" v-for="(item, index) in paperQuestion">
            <div class="chapter">
              <div class="chapter-item">
                <div class="item-title">{{ item.chapter.name }}</div>
                <div></div>
              </div>
              <div
                class="chapter-description"
                v-html="item.chapter.description"
              ></div>
            </div>

            <template v-if="item.questionList.length > 0">
              <div
                :id="`p-${child.id}`"
                :key="child.id"
                class="children-content"
                v-for="(child, indexc) in item.questionList"
              >
                <div class="question-title" v-if="child.type !== 3">
                  <span>{{ index + 1 }}、</span>
                  <div v-html="`${child.title}`"></div>
                </div>
                <div
                  class="question-title"
                  v-if="child.type === 3 && child.examAnswers"
                >
                  <span>{{ index + 1 }}、</span>
                  <ClozeTitle
                    isMark
                    :title="child.title"
                    :questionId="child.id"
                    :paperQuestion="paperQuestion"
                    :myExamDetailCache="child.examAnswers"
                  ></ClozeTitle>
                </div>

                <!-- 单选 -->
                <template v-if="child.type === 1">
                  <el-radio-group
                    class="children-option"
                    v-if="child.examAnswers"
                    v-model="child.examAnswers[0]"
                  >
                    <el-radio
                      disabled
                      :key="index"
                      :label="String.fromCharCode(65 + index)"
                      class="option-item"
                      v-for="(option, index) in child.options"
                    >
                      <div
                        class="flex-items-center"
                        v-html="`${String.fromCharCode(65 + index)}、${option}`"
                      ></div>
                    </el-radio>
                  </el-radio-group>
                </template>

                <!-- 多选 -->
                <template v-if="child.type === 2">
                  <el-checkbox-group
                    class="children-option"
                    v-if="child.examAnswers"
                    v-model="child.examAnswers"
                  >
                    <el-checkbox
                      disabled
                      :key="index"
                      class="option-item"
                      :label="String.fromCharCode(65 + index)"
                      v-for="(option, index) in child.options"
                    >
                      <div
                        class="flex-items-center"
                        v-html="`${String.fromCharCode(65 + index)}、${option}`"
                      ></div>
                    </el-checkbox>
                  </el-checkbox-group>
                </template>

                <!-- 判断 -->
                <template v-if="child.type === 4">
                  <el-radio-group
                    class="children-option"
                    v-if="child.examAnswers"
                    v-model="child.examAnswers[0]"
                  >
                    <el-radio
                      disabled
                      :key="index"
                      :label="option"
                      class="option-item"
                      v-for="(option, index) in ['对', '错']"
                      >{{ option }}</el-radio
                    >
                  </el-radio-group>
                </template>

                <!-- 问答 -->
                <template v-if="child.type === 5">
                  <el-input
                    disabled
                    :rows="2"
                    class="question-text"
                    placeholder="请输入内容"
                    type="textarea"
                    v-if="child.examAnswers"
                    v-model="child.examAnswers[0]"
                  ></el-input>
                </template>

                <div class="children-analysis">
                  <el-row :gutter="10">
                    <template v-if="[1, 4].includes(child.type)">
                      <el-col :span="2.5"> 【答案】： </el-col>
                      <el-col :span="21">
                        <div v-html="`${child.answers[0].answer}`"></div>
                      </el-col>
                    </template>
                    <template v-if="child.type === 2">
                      <el-col :span="2.5"> 【答案】： </el-col>
                      <el-col :span="21">
                        <div v-if="child.answers && child.answers.length > 0">
                          <span
                            v-for="answer in child.answers"
                            :key="answer.id"
                            >{{ answer.answer.join('，') }}</span
                          >
                        </div>
                      </el-col>
                    </template>
                    <template v-if="child.type === 3">
                      <el-col :span="2.5">【答案】：</el-col>
                      <el-col :span="21">
                        <div
                          v-for="(answer, index) in child.answers"
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
                    <template v-if="child.type === 5">
                      <el-col :span="2.5"> 【答案】： </el-col>
                      <el-col :span="21">
                        <template v-if="child.ai === 1">
                          <div
                            v-for="(answer, index) in child.answers"
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
                          v-if="child.ai === 2"
                          v-html="`${child.answers[0].answer}`"
                        ></div>
                      </el-col>
                    </template>
                  </el-row>
                  <el-row :gutter="10">
                    <el-col :span="2.5"> 【解析】： </el-col>
                    <el-col :span="21">
                      <div v-html="`${child.analysis}`"></div>
                    </el-col>
                  </el-row>
                  <el-row :gutter="10">
                    <el-col :span="2.5"> 【分数】： </el-col>
                    <el-col :span="21">
                      <div>{{ child.score }}分</div>
                    </el-col>
                  </el-row>
                  <el-row :gutter="10">
                    <el-col :span="2.5"> 【得分】： </el-col>
                    <el-col :span="21">
                      <span
                        style="margin-right: 15px"
                        v-if="
                          preview === 'true' || preview === true || child.isEdit
                        "
                        >{{ child.scorePlate || 0 }}</span
                      >
                      <el-tooltip
                        content="人工阅卷"
                        effect="light"
                        placement="right"
                      >
                        <el-button
                          circle
                          size="mini"
                          type="primary"
                          icon="el-icon-edit"
                          v-if="
                            (preview === 'false' || preview === false) &&
                            child.isEdit
                          "
                          @click="showScorePlate(index, indexc)"
                        ></el-button>
                      </el-tooltip>
                      <ScorePlate
                        v-if="
                          !child.isEdit &&
                          (preview === 'false' || preview === false)
                        "
                        :key="child.id"
                        :data="child"
                        @input="scoreInput($event, child.id, index, indexc)"
                        @blur="scoreBlur($event, child.id, index, indexc)"
                        @selectScore="
                          selectScore($event, child.id, index, indexc)
                        "
                        @prevQuestion="prevQuestion"
                        @nextQuestion="nextQuestion"
                        @prevPaper="prevPaper"
                        @nextPaper="nextPaper"
                        @markEnd="markEnd"
                      ></ScorePlate>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </template>
          </div>
        </template>
        <el-empty v-else description="暂无试卷"></el-empty>
      </div>
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
      paperName: '',
      hrefPointer: '',
      paperId: null,
      examId: null,
      markId: null,
      userId: null,
      allUserList: [],
      userList: [],
      userInfo: {},
      isFilter: false,
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      collapseShow: 0,
      paperList: [],
      paperQuestion: [],
      selectOption: '',
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
  created() {
    const { examId, paperId, preview, userId } = this.$route.params
    this.examId = examId
    this.paperId = paperId
    this.preview = preview
    this.userId = userId
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      await this.queryPaper()
      await this.queryPaperInfo()
      await this.queryExamineeInfo()
      await this.queryAnswerInfo()
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
        })
        res.data.map((item) => {
          item.questionList.map((question) => {
            question.scorePlate = ''
          })
        })
        this.paperQuestion = res.data
      } catch (error) {}
    },
    // 查询考生信息
    async queryExamineeInfo() {
      const userList = await myMarkUserList({
        examId: Number(this.examId),
      })
      this.allUserList = userList.data
      this.userList = userList.data
      this.userId = this.userId || this.userList[0].userId
      const userIndex = this.userList.findIndex(
        (item) => item.userId == Number(this.userId)
      )
      this.userInfo = this.userList[userIndex]
    },
    // 未阅考生列表
    filterUserList(e) {
      const isAllMark = this.allUserList.every((item) => item.markState === 3)
      if (isAllMark) {
        this.$message.success('已经全部阅卷！')
        this.isFilter = !e
        return
      }
      this.userList = e ? filterUserNum : this.allUserList
      this.queryAnswerInfo()
    },
    // 查询答案信息
    async queryAnswerInfo() {
      try {
        const res = await myMarkAnswerList({
          examId: this.examId,
          userId: this.userId,
        })

        this.paperQuestion.map((cur, index) => {
          cur.questionList.map((item, indexi) => {
            const [{ myExamDetailId, myExamId, answers, score }] =
              res.data.filter((itemr) => itemr.questionId == item.id)
            this.$set(
              this.paperQuestion[index].questionList[indexi],
              'myExamDetailId',
              myExamDetailId
            )
            this.$set(
              this.paperQuestion[index].questionList[indexi],
              'myExamId',
              myExamId
            )
            this.$set(
              this.paperQuestion[index].questionList[indexi],
              'examAnswers',
              answers
            )
            this.$set(
              this.paperQuestion[index].questionList[indexi],
              'scorePlate',
              score
            )
            this.$set(
              this.paperQuestion[index].questionList[indexi],
              'isEdit',
              item.ai === 1
            )
          })
        })

        if (this.preview === 'false' || this.preview === false) {
          this.$nextTick(() => {
            this.toHref()
          })
        }
      } catch (error) {
        this.$message.error(error)
      }
    },
    // 显示分数编辑板
    showScorePlate(index, indexc) {
      this.$set(this.paperQuestion[index].questionList[indexc], 'isEdit', false)
    },
    // 打分输入
    scoreInput(e, questionId, idx, idxc) {
      this.setScore(e, questionId, idx, idxc)
    },
    // 失去焦点提交打分
    scoreBlur(e, questionId, idx, idxc) {
      this.updateScore(e, questionId, idx, idxc)
    },
    // 点击打分板分值
    selectScore(e, questionId, idx, idxc) {
      this.setScore(e, questionId, idx, idxc)
      this.updateScore(e, questionId, idx, idxc)
    },
    // 打分
    async updateScore(e, questionId, idx, idxc) {
      const source = this.paperQuestion[idx].questionList[idxc]
      const res = await myMarkScore({
        examId: this.examId,
        questionId,
        userId: this.userId,
        score: source.scorePlate || 0,
      })
      res?.code === 200 ? '' : this.$message.error(res.msg || '打分失败！')
    },
    // 设置分数
    async setScore(e, questionId, idx, idxc) {
      if (this.preview === 'true' || this.preview === true) {
        this.$message.warning('阅卷未开始或已结束')
        return false
      }
      const source = this.paperQuestion[idx].questionList[idxc]
      this.$set(source, 'scorePlate', e)
      if (e < 0) this.$set(source, 'scorePlate', 0)
      if (e > source.score) this.$set(source, 'scorePlate', source.score)
    },
    // 上下题定位
    toHref(position, status) {
      let toHref = ''

      const paperQuestion = this.paperQuestion.reduce((acc, cur) => {
        acc.push(...cur.questionList)
        return acc
      }, [])

      if (status === undefined) {
        const indexd = paperQuestion.findIndex(
          (item) => (item.ai === 1 && !item.isEdit) || item.ai === 2
        )
        toHref = paperQuestion[indexd].id
      } else {
        const index = paperQuestion.findIndex((item) => item.id == position)

        const newPaperQuestion =
          status === 'next'
            ? paperQuestion.slice(index + 1)
            : paperQuestion.slice(0, index)
        const indexd = newPaperQuestion.findIndex(
          (item) => (item.ai === 1 && !item.isEdit) || item.ai === 2
        )

        if (
          index === 0 ||
          index == this.answerList.length - 1 ||
          indexd === -1
        ) {
          this.$message.warning('没有可阅试题了哦！')
          return
        }

        toHref = newPaperQuestion[indexd].id
      }

      document
        .querySelector(this.hrefPointer)
        .scrollIntoView({ block: 'end', inline: 'nearest' })
      document.querySelector(`#i-${toHref}`).focus()
    },
    // 上一题
    prevQuestion(position) {
      this.toHref(position, 'prev')
    },
    // 下一题
    nextQuestion(position) {
      this.toHref(position, 'next')
    },
    // 上一卷
    prevPaper() {
      const index = this.userList.findIndex(
        (item) => item.userId === this.userId
      )
      if (index === 0) {
        this.$message.warning('已经是第一卷了！')
        return
      }
      this.userId = this.userList[index - 1].userId
      this.userInfo = this.userList[index - 1]
      this.queryAnswerInfo()
    },
    // 下一卷
    nextPaper() {
      const index = this.userList.findIndex(
        (item) => item.userId === this.userId
      )
      if (index === this.userList.length - 1) {
        this.$message.warning('已经是最后一卷了！')
        return
      }
      this.userId = this.userList[index + 1].userId
      this.userInfo = this.userList[index + 1]
      this.queryAnswerInfo()
    },
    // 完成阅卷
    async markEnd() {
      const allQuestions = this.paperQuestion.reduce((acc, cur) => {
        acc.push(...cur.questionList)
        return acc
      }, [])
      const isEvery = allQuestions.every(
        (item) => item.scorePlate !== '' || item.scorePlate !== null
      )
      if (!isEvery) {
        this.$message.warning('请给所有试题打分！')
        return
      }
      const res = await myMarkFinish({
        examId: this.examId,
        userId: this.userId,
      })
      res?.code === 200
        ? this.$message.warning('阅卷完成！')
        : this.$message.error('阅卷失败！')
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
.container {
  margin-top: -20px;
}

.content {
  display: flex;
  width: 100%;
  height: 100%;
  margin: 0 auto;
}

.content-left {
  width: 180px;
  position: fixed;
  height: calc(100vh - 50px);
  top: 50px;
  left: 50px;
  background: #fff;
  box-shadow: 0 1px 16px -3px rgba(0, 0, 0, 0.1);
  .user-info {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 20px 10px;
    border-bottom: 1px solid #ebeef5;
    .user-name {
      text-align: center;
      margin-top: 10px;
    }
  }
  .user-intro {
    display: flex;
    flex-wrap: wrap;
    padding: 20px 10px;
    border-bottom: 1px solid #ebeef5;
    .intro-item {
      width: 50%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      margin-bottom: 10px;
      .item-title {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: #0094e5;
        color: #fff;
        font-size: 12px;
        display: flex;
        justify-content: center;
        align-items: center;
        span {
          width: 24px;
          height: 30px;
          display: flex;
          justify-content: center;
          align-items: center;
        }
      }
      .item-num {
        font-size: 14px;
        margin-top: 10px;
      }
    }
  }
  .user-handler {
    width: 100%;
    padding: 10px;
    line-height: 40px;
  }
}

.content-center {
  width: 960px;
}
</style>
