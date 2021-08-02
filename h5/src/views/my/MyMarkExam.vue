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
                    v-model="child.examAnswers[0]"
                  ></el-input>
                </template>

                <div class="children-analysis">
                  <el-row :gutter="10">
                    <template v-if="child.ai === 1">
                      <el-col :span="2.5">【答案】：</el-col>
                      <el-col :span="21">
                        <div
                          v-for="answer in child.answers"
                          :key="answer.id"
                          class="answers-item"
                        >
                          <span
                            :class="
                              child.answers.length > 1 ? 'answers-tag' : ''
                            "
                            v-for="(ans, index) in answer.answer"
                            :key="index"
                            >{{ ans }}</span
                          >
                        </div>
                      </el-col>
                    </template>
                    <template v-if="child.ai === 2">
                      <el-col :span="2.5"> 【答案】： </el-col>
                      <el-col :span="21">
                        <div v-html="`${child.answers[0].answer}`"></div>
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
                      <ScorePlate
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
          :class="['user-item', activeId === item ? 'active' : '']"
          v-for="(item, index) in examUserIds"
          :key="item"
          @click="queryExamAnswerInfo(item)"
        >
          {{ `考生${index + 1}` }}
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>
<script>
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
      paperId: 0,
      examId: null,
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
      activeId: null,
    }
  },
  created() {
    const { examId, paperId, examUserIds } = this.$route.query
    this.examId = examId
    this.paperId = paperId
    this.examUserIds = examUserIds
    this.init()
  },
  methods: {
    // 返回
    goBack() {
      this.$router.push('/my')
    },
    // 初始化
    async init() {
      await this.queryPaper()
      await this.queryPaperInfo()
      await this.queryExamAnswerInfo()
    },
    // 查询试卷
    async queryPaper() {
      try {
        const res = await this.$https.paperGet({
          id: this.paperId,
        })
        console.info(res)
        this.paper = res.data
      } catch (error) {}
    },
    // 查询试卷信息
    async queryPaperInfo() {
      try {
        const res = await this.$https.paperQuestionList({
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
    // 查询答案信息
    async queryExamAnswerInfo(id) {
      this.activeId = id || this.examUserIds[0]
      try {
        const res = await this.$https.myMarkAnswerList({
          examId: this.examId,
          userId: Number(id || this.examUserIds[0]),
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
          })
        })

        this.answerList = res.data
      } catch (error) {
        this.$tools.message(error, 'error')
      }
    },
    // 定位锚点
    toHref(id, index) {
      this.hrefPointer = `#p-${id}-${index}`
      document.documentElement.scrollTop =
        document.querySelector(this.hrefPointer).offsetTop - 50
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
      this.myExamUpdateScore(e, idx, idxc)
    },
    // 点击打分板分值
    selectScore(e, idx, idxc) {
      this.setScore(e, idx, idxc)
      this.myExamUpdateScore(e, idx, idxc)
    },
    // 打分
    async myExamUpdateScore(e, idx, idxc) {
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
      const index = this.answerList.findIndex(
        (item) => item.questionId == position
      )

      if (status == 'prev') {
        if (index == 0) {
          this.$tools.message('已经是第一题了哦！', 'warning')
          return
        }
        toHref = this.answerList[index - 1].questionId
      }

      if (status == 'next') {
        if (index == this.answerList.length - 1) {
          this.$tools.message('已经是最后一题了哦！', 'warning')
          return
        }
        toHref = this.answerList[index + 1].questionId
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
      let index = this.examUserIds.indexOf(this.activeId)
      if (index === 0) {
        this.$tools.message('已经是第一卷了！', 'warning')
        return
      }
      this.queryExamAnswerInfo(this.examUserIds[index - 1])
    },
    // 下一卷
    nextPaper() {
      let index = this.examUserIds.indexOf(this.activeId)
      if (index === this.examUserIds.length - 1) {
        this.$tools.message('已经是最后一卷了！', 'warning')
        return
      }
      this.queryExamAnswerInfo(this.examUserIds[index + 1])
    },
    // 完成阅卷
    markEnd() {
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
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 0;
  padding-bottom: 0;
  background: #fff;
}

.head {
  width: 100%;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  .head-left {
    color: #fff;
  }
  .head-right {
    color: #fff;
    .common {
      font-size: 20px;
    }
  }
}

.content {
  width: 100%;
  margin-top: 50px;
}

.content-center {
  background: #fff;
  margin: 0 auto;
  width: 1200px;
  .center-drag {
    width: 100%;
    padding: 10px;
    .drag-item {
      margin-bottom: 10px;
    }
  }
  .chapter {
    display: flex;
    flex-direction: column;
    padding: 0 10px;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .item-title {
        font-size: 16px;
      }
      /deep/.el-button {
        opacity: 0;
        .common {
          font-size: 12px;
          margin-right: 5px;
        }
      }
      &:hover {
        .el-button {
          opacity: 1;
        }
      }
    }
    .chapter-description {
      font-size: 14px;
      color: #999;
      padding-bottom: 10px;
      margin-top: -5px;
    }
  }
  .paper-title {
    font-size: 16px;
    color: #333;
    padding: 20px 0 10px 10px;
  }
  .paper-intro {
    font-size: 12px;
    color: #666;
    padding: 0 10px 15px;
    border-bottom: 1px solid #d8d8d8;
  }
  .item-title {
    line-height: 40px;
  }
  .children-content {
    border: 1px solid #d8d8d8;
    font-size: 14px;
    box-sizing: border-box;
    margin-bottom: 10px;
    .question-title {
      display: flex;
      line-height: 40px;
      padding: 0 10px;
      background: #e5f4fc;
      word-wrap: break-word;
      word-break: break-all;
    }
  }
  .children-option {
    padding: 10px 0 0 25px;
  }
  .option-item,
  .flex-items-center {
    display: flex;
    justify-items: center;
    line-height: 30px;
  }
  /deep/ .el-radio__input,
  /deep/ .el-checkbox__input {
    padding-top: 7px;
  }
  .question-text {
    margin: 10px 1% 0;
    width: 98%;
  }
  .children-analysis {
    line-height: 30px;
    padding-left: 20px;
    margin: 15px 0;
    font-size: 13px;
    color: #666;
  }
  .answers-item {
    width: 100%;
    span {
      width: 100%;
      display: inline;
      word-wrap: break-word;
      word-break: normal;
    }
    .answers-tag {
      background: #cdd2f6;
      color: #fff;
      padding: 3px 10px;
      border-radius: 3px;
      &:not(:last-child) {
        margin-right: 10px;
      }
    }
  }
  .el-tag {
    margin-right: 6px;
  }
  .children-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
  }
  .btn {
    padding: 5px 10px;
  }
}

.data-null {
  padding-top: 30px;
  .data-img {
    width: 64px;
    height: 64px;
  }
  .data-tip {
    margin: 0 auto 20px;
  }
}

.el-radio,
.el-checkbox {
  margin-right: 10px;
}

.box-card-no-border {
  border: none;
}

/deep/ .el-card__body {
  padding: 5px;
}

.el-input /deep/.el-input-group__prepend {
  background-color: #fff;
  border: 0px;
  padding: 0 15px 0;
}
/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}
/deep/ .el-collapse-item__header {
  height: 36px;
  line-height: 36px;
  background-color: #f2f2f2;
  padding-left: 20px;
}
/deep/ .el-collapse-item__content {
  padding: 10px;
  font-size: 14px;
}

/deep/ #app {
  background: #fff;
}
/deep/.el-textarea.is-disabled .el-textarea__inner,
/deep/.el-input.is-disabled .el-input__inner {
  background-color: #fff;
  border-color: #0094e5;
  color: #000;
}
/deep/.el-checkbox__input.is-disabled.is-checked + span.el-checkbox__label,
/deep/.el-radio__input.is-disabled.is-checked + span.el-radio__label {
  color: #0094e5;
}
/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner,
/deep/.el-radio__input.is-disabled.is-checked .el-radio__inner {
  background-color: #0094e5;
  border-color: #0094e5;
}
/deep/.el-checkbox__input.is-disabled + span.el-checkbox__label,
/deep/.el-radio__input.is-disabled + span.el-radio__label {
  color: #000;
}
/deep/.el-checkbox__input.is-disabled .el-checkbox__inner,
/deep/.el-radio__input.is-disabled .el-radio__inner {
  background-color: #fff;
}
/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner::after {
  border-color: #fff;
}
.user-list {
  position: fixed;
  width: 120px;
  height: calc(100% - 90px);
  background: #fff;
  z-index: 100;
  top: 70px;
  right: 10px;
  box-shadow: 0 0 13px 0 rgba(0, 0, 0, 0.13);
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  .user-title {
    line-height: 40px;
    background: #1e9fff;
    text-align: center;
    width: 100%;
    color: #fff;
    font-size: 14px;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
  }
  .user-item {
    line-height: 30px;
    width: 100px;
    text-align: center;
    border: 1px solid #1e9fff;
    color: #1e9fff;
    font-size: 13px;
    border-radius: 20px;
    cursor: pointer;
    margin-bottom: 10px;
    &:first-child {
      margin-top: 10px;
    }
    &:hover {
      transition: all 0.2s ease-in;
      background: #1e9fff;
      color: #fff;
    }
  }
  .active {
    transition: all 0.2s ease-in;
    background: #1e9fff;
    color: #fff;
  }
}
</style>
