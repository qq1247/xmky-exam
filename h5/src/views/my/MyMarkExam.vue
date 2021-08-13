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
        <div class="paper-intro">{{ paper.id }}</div>

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
                <p
                  class="question-title"
                  v-html="index + 1 + '、' + child.title"
                ></p>

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
                    v-model="child.examAnswers"
                  >
                    <el-checkbox
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
                    </el-checkbox>
                  </el-checkbox-group>
                </template>

                <!-- 填空 -->
                <template v-if="child.type === 3">
                  <el-input
                    disabled
                    class="question-text"
                    placeholder="请输入内容"
                    :key="index"
                    v-for="(answer, index) in child.examAnswers"
                    :value="answer"
                  >
                    <template slot="prepend">第{{ index + 1 }}空</template>
                  </el-input>
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
                    <template v-if="[1, 2, 4].includes(child.type)">
                      <el-col :span="2.5"> 【答案】： </el-col>
                      <el-col :span="21">
                        <div v-html="`${child.answers[0].answer}`"></div>
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
                          <span>{{ `填空${index + 1}、` }}</span>
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
                            <span>{{ `关键词${index + 1}、` }}</span>
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
                        @input="scoreInput($event, index, indexc)"
                        @blur="scoreBlur($event, index, indexc)"
                        @selectScore="selectScore($event, index, indexc)"
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

        <div class="data-null" v-if="paperQuestion.length == 0">
          <img alt class="data-img" src="../../assets/img/data-null.png" />
          <span class="data-tip">暂无试卷信息</span>
        </div>
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
              : item.markState === 3
              ? 'end'
              : '',
          ]"
          v-for="item in examUserIds"
          :key="item.id"
          @click="queryAnswerInfo(item.userId)"
        >
          <span style="margin-right: 10px">{{ item.userName }}</span>
          <span style="margin-right: 10px">{{ item.totalScore || 0 }}分</span>
          <i v-if="item.markState === 3" class="common common-finish"></i>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>
<script>
import { paperGet, paperQuestionList } from '@/api/paper'
import { myMarksListPage, myMarkAnswerList } from '@/api/my'
import ScorePlate from '@/components/ScorePlate.vue'
export default {
  components: {
    ScorePlate,
  },
  data() {
    return {
      labelPosition: 'left',
      paperName: '',
      hrefPointer: '',
      paperId: null,
      examId: null,
      markId: null,
      examUserIds: null,
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      collapseShow: 0,
      paperList: [],
      paperQuestion: [],
      myExamDetailCache: {},
      selectOption: '',
      paper: {},
      answerList: [],
      userId: null,
      preview: false,
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
      const infos = await myMarksListPage({
        curPage: this.curPage,
        pageSize: this.pageSize,
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
              item.ai === 1 ? true : false
            )
          })
        })

        if (this.preview === 'false') {
          this.$nextTick(() => {
            this.toHref()
          })
        }
      } catch (error) {
        this.$tools.message(error, 'error')
      }
    },
    // 显示分数编辑板
    showScorePlate(index, indexc) {
      this.$set(this.paperQuestion[index].questionList[indexc], 'isEdit', false)
    },
    // 设置分数
    async setScore(e, idx, idxc) {
      const source = this.paperQuestion[idx].questionList[idxc]
      this.$set(source, 'scorePlate', e)
      if (e < 0) this.$set(source, 'scorePlate', 0)
      if (e > source.score) this.$set(source, 'scorePlate', source.score)
    },
    // 打分输入
    scoreInput(e, idx, idxc) {
      this.setScore(e, idx, idxc)
    },
    // 失去焦点提交打分
    scoreBlur(e, idx, idxc) {
      this.updateScore(e, idx, idxc)
    },
    // 点击打分板分值
    selectScore(e, idx, idxc) {
      this.setScore(e, idx, idxc)
      this.updateScore(e, idx, idxc)
    },
    // 打分
    async updateScore(e, idx, idxc) {
      const source = this.paperQuestion[idx].questionList[idxc]
      const res = await this.$https
        .myExamUpdateScore({
          myExamDetailId: source.myExamDetailId,
          score: source.scorePlate,
        })
        .catch((err) => {})
      res?.code === 200
        ? this.$tools.message('打分成功！')
        : this.$tools.message(res.msg || '打分失败！', 'error')
    },
    // 上下题定位
    toHref(position, status) {
      let toHref = ''

      let paperQuestion = this.paperQuestion.reduce((acc, cur) => {
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
          this.$tools.message('没有可阅试题了哦！', 'warning')
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
        this.$tools.message('已经是第一卷了！', 'warning')
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
        this.$tools.message('已经是最后一卷了！', 'warning')
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
        (item) => item.scorePlate !== '' && item.scorePlate !== null
      )
      if (!isEvery) {
        this.$tools.message('请给所有试题打分！', 'warning')
        return
      }
      const res = await this.$https
        .myExamDoScore({
          examId: this.examId,
          userId: this.userId,
          markId: this.markId,
        })
        .catch((err) => {})
      res?.code === 200
        ? (this.$tools.message('阅卷完成！', 'warning'),
          this.queryExamineeInfo())
        : this.$tools.message('阅卷失败！', 'error')
    },
  },
}
</script>
<style lang="scss" scoped>
@import '@/assets/style/exam.scss';
</style>
