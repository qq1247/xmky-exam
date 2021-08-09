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
                    v-if="myExamDetailCache[child.id]"
                    v-model="myExamDetailCache[child.id].answers[0]"
                  >
                    <el-radio
                      :key="index"
                      :disabled="preview === 'true' ? true : false"
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
                    v-if="myExamDetailCache[child.id]"
                    v-model="myExamDetailCache[child.id].answers"
                  >
                    <el-checkbox
                      :key="index"
                      :label="String.fromCharCode(65 + index)"
                      class="option-item"
                      :disabled="preview === 'true' ? true : false"
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
                <template
                  v-if="child.type === 3 && myExamDetailCache[child.id]"
                >
                  <el-input
                    class="question-text"
                    @change="
                      (val) => {
                        updateClozeAnswer(child.id, val, child.answers, index)
                      }
                    "
                    placeholder="请输入内容"
                    :key="index"
                    :disabled="preview === 'true' ? true : false"
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
                    v-if="myExamDetailCache[child.id]"
                    v-model="myExamDetailCache[child.id].answers[0]"
                  >
                    <el-radio
                      :key="index"
                      :label="option"
                      class="option-item"
                      v-for="(option, index) in ['对', '错']"
                      :disabled="preview === 'true' ? true : false"
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
                    v-if="myExamDetailCache[child.id]"
                    :disabled="preview === 'true' ? true : false"
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
        <template v-if="preview == 'false'">
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
        <div v-if="preview == 'false'" class="exam-footer" @click="examEnd">
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
      preview: false,
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
    const { id, paperId, preview, examEndTime } = this.$route.query
    this.id = id
    this.paperId = paperId
    this.preview = preview
    this.examEndTime = examEndTime
    this.init()
  },
  methods: {
    // 返回
    goBack() {
      this.$router.back()
    },
    // 定位锚点
    toHref(id) {
      this.hrefPointer = `#p-${id}`
      document.documentElement.scrollTop =
        document.querySelector(this.hrefPointer).offsetTop - 50
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
    async queryAnswerInfo() {
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
      if (this.preview === 'true') {
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
    async updateClozeAnswer(questionId, val, answers, index) {
      if (this.preview === 'true') {
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
@import '@/assets/style/exam.scss';
</style>
