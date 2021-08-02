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
                v-for="(child, index) in item.questionList"
              >
                <p
                  class="question-title"
                  v-html="index + 1 + '、' + child.title"
                ></p>

                <!-- 单选 -->
                <template v-if="child.type === 1">
                  <el-radio-group
                    @change="
                      (val) => {
                        updateAnswer(child.id, val)
                      }
                    "
                    class="children-option"
                    v-model="myExamDetailCache[child.id].answers[0]"
                  >
                    <el-radio
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
                    @change="
                      (val) => {
                        updateAnswer(child.id, val)
                      }
                    "
                    class="children-option"
                    v-model="myExamDetailCache[child.id].answers"
                  >
                    <el-checkbox
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
                    class="question-text"
                    @change="
                      (val) => {
                        updateFillBlanksAnswer(
                          child.id,
                          val,
                          child.answers,
                          index
                        )
                      }
                    "
                    placeholder="请输入内容"
                    :key="index"
                    v-for="(answer, index) in myExamDetailCache[child.id]
                      .answers"
                    v-model="myExamDetailCache[child.id].answers[index]"
                  >
                    <template slot="prepend">第{{ index + 1 }}空</template>
                  </el-input>
                </template>

                <!-- 判断 -->
                <template v-if="child.type === 4">
                  <el-radio-group
                    @change="
                      (val) => {
                        updateAnswer(child.id, val)
                      }
                    "
                    class="children-option"
                    v-model="myExamDetailCache[child.id].answers[0]"
                  >
                    <el-radio
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
                    :rows="2"
                    class="question-text"
                    @change="
                      (val) => {
                        updateAnswer(child.id, val)
                      }
                    "
                    placeholder="请输入内容"
                    type="textarea"
                    v-model="myExamDetailCache[child.id].answers[0]"
                  ></el-input>
                </template>
              </div>
            </template>
          </div>
        </template>
        <div class="data-null" v-if="paperQuestion.length == 0">
          <img alt class="data-img" src="../../assets/img/data-null.png" />
          <span class="data-tip">暂无试卷信息</span>
        </div>
      </div>

      <el-collapse
        class="exam-card"
        v-if="paperQuestion.length > 0"
        v-model="collapseShow"
      >
        <template v-if="view == 'false'">
          <div class="exam-head">答题卡</div>
          <div class="exam-time">
            倒计时：<CountDown :time="time" @finish="forceExamEnd"></CountDown>
          </div>
        </template>
        <el-collapse-item
          :key="item.id"
          :name="index"
          :title="item.chapter.name"
          v-for="(item, index) in paperQuestion"
          v-model="questionRouter"
        >
          <a
            @click="toHref(child.id)"
            v-for="(child, index) in item.questionList"
            :key="child.id"
            >{{ index + 1 }}</a
          >
        </el-collapse-item>
        <div v-if="view == 'false'" class="exam-footer" @click="examEnd">
          提交
        </div>
      </el-collapse>
    </div>
  </div>
</template>
<script>
import CountDown from '@/components/CountDown.vue'
export default {
  components: {
    CountDown,
  },
  data() {
    return {
      id: 0,
      view: false,
      labelPosition: 'left',
      paperName: '',
      hrefPointer: '',
      paperId: 0,
      pageSize: 10,
      curPage: 1,
      pageTotal: 0,
      collapseShow: 0,
      paperList: [],
      paperQuestion: [],
      myExamDetailCache: {},
      selectOption: '',
      paper: {},
      questionRouter: [],
      examEndTime: '',
      time: 0,
    }
  },
  created() {
    const { id, paperId, view, examEndTime } = this.$route.query
    this.id = id
    this.paperId = paperId
    this.view = view
    this.examEndTime = examEndTime
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
      await this.queryMyExamAnswerInfo()
    },
    // 校准时间差
    async setTime() {
      const systemTime = await this.$https.loginSysTime({})
      let times = new Date(systemTime.data) - new Date()
      this.time =
        new Date(this.examEndTime).getTime() - (new Date().getTime() + times)
    },
    // 查询试卷
    async queryPaper() {
      try {
        const res = await this.$https.paperGet({
          id: this.paperId,
        })
        this.paper = { ...res.data }
      } catch (error) {}
    },
    // 查询试卷信息
    async queryPaperInfo() {
      try {
        const res = await this.$https.paperQuestionList({
          id: this.paperId,
        })
        res.data.map((item) => {
          item.chapter.show = true
        })
        this.paperQuestion = res.data
        this.questionRouter = Array.from(res.data.keys())
      } catch (error) {}
    },
    // 查询我的答案信息
    async queryMyExamAnswerInfo() {
      try {
        const res = await this.$https.myExamAnswerList({
          id: this.id,
        })

        const paperQuestion = this.paperQuestion.reduce((acc, cur) => {
          acc.push(...cur.questionList)
          return acc
        }, [])

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
      } catch (error) {
        this.$tools.message(error, 'error')
      }
    },
    // 更新答案
    async updateAnswer(questionId, answers) {
      if (this.view === 'true') {
        return
      }

      if (!this.myExamDetailCache[questionId]) {
        this.$tools.message('提交答案失败，请联系管理员！', 'error')
        return
      }

      const res = await this.$https.myExamUpdateAnswer({
        myExamDetailId: this.myExamDetailCache[questionId].myExamDetailId,
        answers: answers,
      })
    },
    // 更新填空答案
    async updateFillBlanksAnswer(questionId, val, answers, index) {
      if (this.view === 'true') {
        return
      }
      if (!this.myExamDetailCache[questionId]) {
        this.$tools.message('提交答案失败，请联系管理员！', 'error')
        return
      }

      answers[index] = val
      const res = await this.$https.myExamUpdateAnswer({
        myExamDetailId: this.myExamDetailCache[questionId].myExamDetailId,
        answers: answers,
      })
    },
    // 定位锚点
    toHref(id) {
      this.hrefPointer = `#p-${id}`
      document.documentElement.scrollTop =
        document.querySelector(this.hrefPointer).offsetTop - 50
    },
    // 考试结束
    async examEnd() {
      this.$confirm('确认要提交试卷吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await this.$https.myExamDoAnswer({ myExamId: this.id })
        res?.code === 200
          ? this.$router.replace({
              path: '/my',
            })
          : this.$tools.message('请重新提交试卷！', 'warning')
      })
    },
    // 倒计时结束，强制交卷
    async forceExamEnd() {
      this.$alert('考试时间到，已强制交卷！', '', {
        confirmButtonText: '确定',
        type: 'info',
        showClose: false,
      }).then(async () => {
        const res = await this.$https.myExamDoAnswer({ myExamId: this.id })
        this.$router.replace({
          path: '/my',
        })
      })
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
  width: 960px;
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
  .option-item-text {
    border-bottom: 1px solid #d8d8d8;
    padding: 20px 10px 5px;
    color: #333;
    margin: 0 25px 0;
  }
  .question-text {
    margin: 4px 1%;
    width: 98%;
  }
  .children-analysis {
    line-height: 25px;
    padding-left: 20px;
    margin: 15px 0;
    font-size: 13px;
    color: #666;
    span {
      font-size: 15px;
      font-weight: bold;
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
  width: 38px;
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
.exam-card {
  width: 214px;
  font-family: 'Microsoft YaHei';
  border: 1px solid #e6e6e6;
  position: fixed;
  right: 50px;
  top: 70px;
}
.exam-time {
  line-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0094e5;
}
.exam-card .exam-head,
.exam-footer {
  background: #1e9fff;
  color: #fff;
  height: 40px;
  line-height: 40px;
  font-size: 18px;
  text-align: center;
  padding: 0px 18px;
  cursor: pointer;
}
.exam-card a {
  width: 32px;
  height: 25px;
  border: 1px solid #eceaea;
  color: #888;
  text-align: center;
  line-height: 23px;
  margin-left: 1px;
  display: inline-block;
  margin-top: 5px;
  margin-right: 5px;
  border-radius: 3px;
  text-decoration: none;
  cursor: pointer;
}
.exam-card a:hover {
  color: #1e9fff;
  border: 1px solid #1e9fff;
}
/deep/ #app {
  background: #fff;
}
</style>
