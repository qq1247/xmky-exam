<template>
  <div class="batch-input-container">
    <div class="left">
      <mavon-editor
        style="height: 100%"
        :value="msg"
        :toolbars-flag="false"
        :short-cut="false"
        :subfield="false"
        @change="msgChange"
      />
    </div>
    <div class="right">
      <!-- 操作按钮与提示 -->
      <div class="btn-tip">
        <div class="tip">
          <span>解析出{{ totalTopicCount }}题</span>
          <span>其中有{{ errTopicCount }}题格式错误</span>
        </div>
        <div class="btn-box">
          <div
            v-for="handler in handlerButtons"
            :key="`handler${handler.type}`"
            class="handler-btn"
            @click="otherHandler(handler.type)"
          >
            <i :class="handler.icon" />
            {{ handler.name }}
          </div>
        </div>
      </div>
      <div class="content-center qestion-box">
        <QuestionList
          ref="questionList"
          :list="list"
          :preview="true"
          :show-potic="false"
          :show-err="true"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { questionAdd } from 'api/question'
import QuestionList from './QuestionList.vue'
export default {
  components: { QuestionList },
  props: {
    questionTypeId: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      topicArr: [],
      list: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 5, // 每页多少条
        questionList: [] // 列表数据
      },
      totalTopicCount: 0, // 题目总数量
      errTopicCount: 0, // 错误的题目数量
      parsingMsg: '', // 解析的题目
      msg: '', // markdrow输入的内容
      handlerButtons: [
        // 左侧按钮组2
        {
          type: 1,
          name: '导入',
          icon: 'common common-template-down'
        },
        {
          type: 2,
          name: '返回',
          icon: 'common common-quick'
        }
      ],
      errTopicArr: [] // 接口上传题目报错后存储的错误
    }
  },
  methods: {
    // 文本解析
    textParsing() {
      // 按换行符将文本分割为行列表
      this.getMsgListByLineBreak()
      // 通过/^\d[.。、]/获取到单道题目放在问题列表中
      const questionLists = this.getQuestionList()

      this.parseQuestionList(questionLists)
      this.list.questionList = this.topicArr
    },

    getMsgListByLineBreak() {
      this.parsingMsg = this.msg.split(/\n/).reverse()
    },

    // 获取问题列表
    getQuestionList() {
      const questionList = []
      let tempList = []
      for (const str of this.parsingMsg) {
        tempList.push(str)
        if (/^\d[.。、]/.test(str)) {
          questionList.push(tempList)
          tempList = []
        }
      }
      return questionList.reverse()
    },

    parseQuestionList(qusetionList) {
      // 临时存储数据的列表
      for (let i = 0; i < qusetionList.length; i++) {
        this.addTopic(i)
        this.parseQuestion(i, qusetionList[i])
      }
    },

    parseQuestion(index, singleQuestion) {
      let tempArr = []
      for (let qusetionStr of singleQuestion) {
        tempArr.push(qusetionStr)
        if (/^\[解析\]/.test(qusetionStr)) {
          this.topicArr[index].analysis = tempArr.reverse().join()
          tempArr = []
        } else if (/^\[[^解析]/.test(qusetionStr)) {
          this.topicArr[index].answer = tempArr.reverse().join()
          tempArr = []
        } else if (/^[a-zA-Z][.。、]/.test(qusetionStr)) {
          // 选项
          if (tempArr.length > 1) {
            qusetionStr = tempArr.reverse().join('')
          }
          this.topicArr[index].options.push(qusetionStr)
          this.topicArr[index].options.sort()
          tempArr = []
        } else if (/^\d[.。、]/.test(qusetionStr)) {
          // 题干
          this.topicArr[index].title = tempArr.reverse().join().slice(2)
          this.parseSingleQuestion(this.topicArr[index])
          tempArr = []
        }
      }
    },

    // 添加试题对象
    addTopic(i) {
      const topic = {
        title: '', // 题干
        options: [], // 选项
        answers: '', // 答案
        analysis: '', // 解析
        score: 1, // 分数
        answerScores: [],
        ai: 2,
        aiOptions: [],
        state: 1,
        difficulty: 3, // 难易程度 1.极易 2.简单 3.适中 4.困难 5.极难
        questionTypeId: this.questionTypeId, // 试题分类ID
        type: '', // 试题类型 1.单选 2.多选 3.填空 4.判断 5.问答
        errs: [], // 错误
        id: parseFloat(i) + 1 // 题目序号
      }
      this.topicArr.push(topic)
    },

    // 单个问题的解析
    parseSingleQuestion(question) {
      const tempScore = this.parseAnswer(question)
      this.topicClassification(question)
      this.parseScore(tempScore, question)
      this.examPublicErr(question)
      this.exemChoiceErrs(question)
      this.examGapFillingErr(question)
      this.questionCountErrAndTotal(question)
    },

    // 解析答案
    parseAnswer(singleQuestion) {
      const tempScore = []
      if (singleQuestion.answer) {
        const tempAnswer = singleQuestion.answer.replace(/\]/g, '').split('[').reverse()
        tempAnswer.pop()
        for (let ele of tempAnswer) {
          ele = ele.trim()
          if (/[极易|简单|适中|困难|极难]/.test(ele)) {
            singleQuestion.difficulty =
              ele === '极易'
                ? 1
                : ele === '简单'
                  ? 2
                  : ele === '困难'
                    ? 4
                    : ele === '极难'
                      ? 5
                      : 3
          } else if (/^\d+分$/.test(ele)) {
            tempScore.push(ele.replace('分', ''))
          } else if (/^[对|错]$/.test(ele)) {
            singleQuestion.answers = ele
          } else if (/^答案无顺序$/.test(ele)) {
            singleQuestion.ai = 1
            singleQuestion.aiOptions.push(2)
          } else if (/^答案有顺序$/.test(ele)) {
            singleQuestion.ai = 1
            if (singleQuestion.aiOptions.length === 0) {
              singleQuestion.aiOptions = []
            }
          } else if (/^大小写不敏感$/.test(ele)) {
            singleQuestion.ai = 1
            singleQuestion.aiOptions.push(3)
          } else if (/^大小写敏感$/.test(ele)) {
            singleQuestion.ai = 1
            if (singleQuestion.aiOptions.length === 0) {
              singleQuestion.aiOptions = []
            }
          } else {
            singleQuestion.answers += `${ele}|`
          }
        }
        singleQuestion.answers = singleQuestion.answers.replace(/\|$/, '').split('|')
        if (singleQuestion.answers.length === 1) {
          singleQuestion.answers = singleQuestion.answers[0]
        }
      }
      return tempScore
    },

    // 题型归类
    topicClassification(singleQuestion) {
      if (singleQuestion.title.indexOf('_____') > -1) {
        if (singleQuestion.answers) {
          singleQuestion.ai = 1
        }
        singleQuestion.type = 3
      } else if (singleQuestion.answers === '对' || singleQuestion.answers === '错') {
        singleQuestion.type = 4
        singleQuestion.ai = 1
      } else if (singleQuestion.options.length > 0) {
        // 选择题
        singleQuestion.ai = 1
        if (singleQuestion.answers.length > 1) {
          singleQuestion.type = 2
          singleQuestion.answers = singleQuestion.answers.split('')
        } else {
          singleQuestion.type = 1
        }
      } else {
        singleQuestion.type = 5
      }
    },

    // 检查填空题目错误
    examGapFillingErr(singleQuestion) {
      if (singleQuestion.type === 3) {
        const kongCount = singleQuestion.title.match(/_____/g).length
        let answerCount = 0
        if (kongCount === 1 && Array.isArray(singleQuestion.answers)) {
          singleQuestion.errs.push('答案多于填空数量')
        } else if (kongCount > 1) {
          // 填空数量大于1的时候，答案是列表就表示一个元素一个答案
          // 否则就用空格将答案拆分成列表
          if (!Array.isArray(singleQuestion.answers) && singleQuestion.answers) {
            singleQuestion.answers = singleQuestion.answers.split(' ')
          }
          if (Array.isArray(singleQuestion.answers)) {
            singleQuestion.answers.reverse()
          }
          answerCount = singleQuestion.answers.length
          if (answerCount !== kongCount) {
            singleQuestion.errs.push('填空数量和答案数量不一致')
          }
        }
      }
    },
    // 检查选择题是否有错误
    exemChoiceErrs(singleQuestion) {
      const optionRange = []
      singleQuestion.options.forEach((item) => {
        optionRange.push(item[0])
      })
      if (singleQuestion.type === 1) {
        if (singleQuestion.options.length < 2) {
          singleQuestion.errs.push('选项最少包含两项')
        }
        if (!this.judgeSeries(singleQuestion.options)) {
          singleQuestion.errs.push('选项不连续')
        }
        if (!optionRange.includes(singleQuestion.answers)) {
          singleQuestion.errs.push('答案与选项不匹配')
        }
      }
      if (singleQuestion.type === 2) {
        if (!this.judgeSeries(singleQuestion.options)) {
          singleQuestion.errs.push('选项不连续')
        }
        // if (singleQuestion.options.length < 3) {
        //   singleQuestion.errs.push('选项最少包含三项')
        // }
        if (
          !singleQuestion.answers.every((item) => {
            return optionRange.includes(item)
          })
        ) {
          singleQuestion.errs.push('答案与选项不匹配')
        }
      }
    },

    examPublicErr(singleQuestion) {
      if (!singleQuestion.answers) {
        singleQuestion.errs.push('缺少答案')
      }
    },

    // 分数解析
    parseScore(scoreArr, singleQuestion) {
      if (scoreArr.length >= 1) {
        if (singleQuestion.type === 2) {
          scoreArr.sort((a, b) => {
            return b - a
          })
          if (singleQuestion.length === 2) {
            singleQuestion.answerScores = parseFloat(scoreArr[1])
            singleQuestion.score = parseFloat(scoreArr[0])
          } else {
            singleQuestion.score = parseFloat(singleQuestion.score[0])
            singleQuestion.answerScores = parseFloat(scoreArr[0]) / 2
          }
        } else if (singleQuestion.type === 3) {
          const count = singleQuestion.title.match(/_____/g).length
          if (count === 1) {
            singleQuestion.score = parseFloat(scoreArr[0])
          } else {
            singleQuestion.score = parseFloat(scoreArr[0])
            singleQuestion.answerScores = new Array(count).fill(
              parseFloat(scoreArr[0]) / count
            )
          }
        } else {
          singleQuestion.score = parseFloat(scoreArr[0])
        }
      } else {
        if (singleQuestion.type === 3) {
          const count = singleQuestion.title.match(/_____/g).length
          if (count === 1) {
            singleQuestion.score = 1
            singleQuestion.answerScores = 1
          } else {
            singleQuestion.score = count
            singleQuestion.answerScores = new Array(count).fill(1)
          }
        } else {
          singleQuestion.score = 1
        }
      }
    },

    // 问题数量统计
    questionCountErrAndTotal(singleQuestion) {
      // 统计总题数量
      this.totalTopicCount++
      // 统计错误题数量
      if (singleQuestion.errs.length > 0) {
        this.errTopicCount++
      }
    },

    initData() {
      this.topicArr = []
      this.totalTopicCount = 0
      this.errTopicCount = 0
    },

    msgChange(value, render) {
      this.msg = value
      // 内容变化后重新添加
      this.initData()
      this.textParsing()
    },

    // 判断选择题选项是否是连续的
    judgeSeries(data) {
      let lcontinuity = 0 // 连贯的计数
      for (let i = 1; i < data.length; i++) {
        if (
          data[i].slice(0, 1).toUpperCase().charCodeAt() -
            data[i - 1].slice(0, 1).toUpperCase().charCodeAt() ===
            1 ||
          data[i].slice(0, 1).toUpperCase().charCodeAt() -
            data[i - 1].slice(0, 1).toUpperCase().charCodeAt() ===
            -1
        ) {
          lcontinuity += 1
        }
      }
      if (lcontinuity > data.length - 2) {
        return true
      } else {
        return false
      }
    },

    // 点击右侧按钮触发的事件
    otherHandler(type) {
      switch (type) {
        case 1:
          if (this.errTopicCount === 0) {
            this.uploadTopics()
            this.$message.success('导入成功')
            if (this.errTopicArr.length > 0) {
              this.$emit('showTemplate', true)
            } else {
              this.$emit('showTemplate', false)
            }
            // this.$emit("showTemplate", false)
          } else {
            this.$message.warning('请先解决完错误')
          }
          break
        case 2:
          this.$router.back()
          break
        default:
          break
      }
    },

    // 上传题目
    async uploadTopics() {
      for (const upTopic of this.topicArr) {
        upTopic.options.forEach((item, index, arr) => {
          arr[index] = item.slice(2)
        })
        await questionAdd(upTopic).catch((err) => {
          console.log(err)
          this.errTopicArr.push(upTopic)
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.batch-input-container {
  width: 100%;
  height: 100%;
  padding: 20px 20px;
  display: flex;
  .left {
    width: 40%;
    height: 100%;
    overflow: scroll;
    background-color: #fff;
  }
  .right {
    width: 60%;
    height: 100%;
    border-left: 1px solid gray;
    background-color: #fff;
    .btn-tip {
      display: flex;
      flex-direction: column;
      margin: 0 15px;
      .tip {
        display: inline-flex;
        align-content: flex-end;
        font-weight: 600;
        span {
          margin-right: 20px;
          &:nth-child(1) {
            font-size: 18px;
          }
          &:nth-child(2) {
            color: #f56c6c;
            font-size: 14px;
          }
        }
      }
      .btn-box {
        display: flex;
        flex-direction: row;
        margin-right: 5px;
        .handler-btn {
          width: 100px;
          background: #eee;
          margin: 7px 10px 7px 0;
          text-align: center;
          line-height: 25px;
          cursor: pointer;
          font-size: 14px;
          display: flex;
          justify-content: center;
          align-items: center;
          user-select: none;
          border-radius: 3px;
          &:hover {
            background: #0094e5;
            color: #ffff;
          }
          i {
            width: 16px;
            height: 16px;
            line-height: 16px;
            text-align: center;
            font-size: 16px;
            display: block;
            margin-right: 10px;
          }
        }
      }
    }
    .qestion-box {
      height: calc(100% - 60px);
      overflow: scroll;
      // background: #fff;
    }
    .err-msg {
      background-color: #f00;
      color: #fff;
      font-weight: 900;
      text-indent: 2em;
    }
  }
}
</style>
