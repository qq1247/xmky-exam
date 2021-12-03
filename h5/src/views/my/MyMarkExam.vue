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

        <template v-if="paperQuestion.length > 0">
          <div :key="index" v-for="(item, index) in paperQuestion">
            <div class="chapter">
              <div class="chapter-item">
                <div class="item-title">{{ item.chapter.name }}</div>
                <div></div>
              </div>
              <div class="chapter-description">
                {{ item.chapter.description }}
              </div>
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

                <!-- 填空 -->
                <!-- <template v-if="child.type === 3">
                  <el-input
                    disabled
                    class="question-text"
                    placeholder="请输入内容"
                    :key="index"
                    v-for="(answer, index) in child.examAnswers"
                    :value="answer"
                  >
                    <template slot="prepend"
                      >第{{ $tools.intToChinese(index + 1) }}空</template
                    >
                  </el-input>
                </template> -->

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
                            >{{ answer.answer[0] }}</span
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
                        v-if="preview === 'true' || child.isEdit"
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
                          v-if="preview === 'false' && child.isEdit"
                          @click="showScorePlate(index, indexc)"
                        ></el-button>
                      </el-tooltip>
                      <ScorePlate
                        v-if="!child.isEdit && preview === 'false'"
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

    <!-- 考生列表 -->
    <div class="user-list">
      <div class="user-title">考生列表</div>
      <el-scrollbar
        wrap-style="overflow-x: hidden;width: 100%;display:flex;flex-direction: column;align-items: center"
      >
        <div
          :class="[
            'user-item',
            userId === item.userId
              ? 'active'
              : item.examMarkState === 3
              ? 'end'
              : '',
          ]"
          v-for="item in examUserIds"
          :key="item.id"
          @click="queryAnswerInfo(item.userId)"
        >
          <span style="margin-right: 10px">{{ item.userName }}</span>
          <span style="margin-right: 10px">{{ item.totalScore || 0 }}分</span>
          <i v-if="item.examMarkState === 3" class="common common-finish"></i>
        </div>
      </el-scrollbar>
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
      labelPosition: 'left',
      paperName: '',
      hrefPointer: '',
      paperId: null,
      examId: null,
      markId: null,
      examUserIds: [],
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      collapseShow: 0,
      paperList: [],
      paperQuestion: [],
      selectOption: '',
      paper: {},
      answerList: [],
      userId: null,
      preview: 'false',
    }
  },
  created() {
    const { examId, paperId, markId, preview } = this.$route.query
    this.examId = examId
    this.paperId = paperId
    this.markId = markId
    this.preview = preview
    this.init()
  },
  methods: {
    // 返回
    goBack() {
      this.$router.back()
    },
    // 初始化
    async init() {
      await this.queryPaper()
      await this.queryPaperInfo()
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
      const infos = await myMarkUserList({
        examId: Number(this.examId),
      })

      this.examUserIds = infos.data.list
    },
    // 查询答案信息
    async queryAnswerInfo(id) {
      await this.queryExamineeInfo()
      this.userId = id || this.examUserIds[0].userId
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

        if (this.preview === 'false') {
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
      res?.code === 200
        ? this.queryExamineeInfo()
        : this.$message.error(res.msg || '打分失败！')
    },
    // 设置分数
    async setScore(e, questionId, idx, idxc) {
      if ((this.preview = 'true')) {
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

      document.documentElement.scrollTop =
        document.querySelector(`#p-${toHref}`).offsetTop - 50
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
      const index = this.examUserIds.findIndex(
        (item) => item.userId === this.userId
      )
      if (index === 0) {
        this.$message.warning('已经是第一卷了！')
        return
      }
      this.queryAnswerInfo(this.examUserIds[index - 1].userId)
    },
    // 下一卷
    nextPaper() {
      const index = this.examUserIds.findIndex(
        (item) => item.userId === this.userId
      )
      if (index === this.examUserIds.length - 1) {
        this.$message.warning('已经是最后一卷了！')
        return
      }
      this.queryAnswerInfo(this.examUserIds[index + 1].userId)
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
        ? (this.$message.warning('阅卷完成！'), this.queryExamineeInfo())
        : this.$message.error('阅卷失败！')
    },
  },
}
</script>
<style lang="scss" scoped>
@import 'assets/style/exam.scss';
</style>
