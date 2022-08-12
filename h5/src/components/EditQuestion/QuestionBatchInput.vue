<template>
  <div class="batch-input-container">
    <div class="left">
      <div class="btn-box">
        <el-button plain @click="preStep()" size="mini" >返回</el-button>
      </div>

      <mavon-editor
        class="qestion-box"
        :value="msg"
        :toolbars-flag="false"
        :short-cut="false"
        :subfield="false"
        @change="parseTxt"
      />
    </div>
    <div class="right">
      <!-- 操作按钮与提示 -->
      <div class="top-box">
        <div>
          <span>{{`检查区【共${total}题，`}}</span>
          <span class="err-color">错误：{{errNum}}题】</span>
        </div>

        <div>
          <!-- <el-button type="danger" size="mini" @click="btnHandler('location')" plain>定位错误</el-button> -->
          <el-button type="primary" size="mini" @click="txtImport('export')">导入</el-button>
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
    },
    isBack: {
      type: Boolean,
      default: true
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
      total: 0, // 题目总数量
      errNum: 0, // 错误的题目数量
      parsingMsg: '', // 解析的题目
      msg: '', // markdrow输入的内容
      errTopicArr: [] // 接口上传题目报错后存储的错误
    }
  },
  methods: {
    parseTxt(txt) {
      // 拆分文本，每个文本为一道题
      let questionTxtArr = this.splitTxt2QuestionTxt(txt)

      // 文本解析为试题可读字段
      let questionList = []
      for (let i in questionTxtArr) {
        let question = this.parseQuestion(questionTxtArr[i], i)
        questionList.push(question)
        console.log(question)
      }

      this.list.questionList = questionList
    },
    // 拆分文本，每段为一道试题
    splitTxt2QuestionTxt(txt) {
      let singleQuestion = [] // 单题
      let questionList = [] // 存放单题列表
      for (let curRowTxt of txt.split(/\n/)) { // 文本按回车分割
        if (/^\d[.。、]/.test(curRowTxt)) {// 如果已数字开头，后跟.。、号开头
          if (singleQuestion.length > 0) {
            questionList.push(singleQuestion)// 解析为一道题
            singleQuestion = []
          }
        }

        singleQuestion.push(curRowTxt)// 当前行文本存入单题
      }
      questionList.push(singleQuestion) // 最后一行处理

      return questionList
    },
    parseQuestion(questionTxtArr) {
      // 拆分字段并校验格式
      let optionIndexArr = [], answerIndex = -1, analysisIndex = -1
      for (let i in questionTxtArr) {
        if (/^\[解析\]/.test(questionTxtArr[i])) { // 已[解析]开头
          analysisIndex = parseInt(i)
        } else if (/^\[(?!解析)/.test(questionTxtArr[i])) {// 已[号开头，并且不包含解析
          answerIndex = parseInt(i)
        } else if (/^[A-Za-z][.。、]?/.test(questionTxtArr[i])) {// 已abcdefg中的一个开头，后面跟.。、或没有
            optionIndexArr.push(parseInt(i))
        }
      }
      if (answerIndex === -1) {
        return {title : questionTxtArr.join('<br/>'), errs : '缺少答案'}
      }
      if (optionIndexArr.length > 0) {// 如果存在（单多选）选项，并且选项在答案之后
        for (let optionIndex of optionIndexArr) {
          if (optionIndex >= answerIndex) {
            return {title : questionTxtArr.join('<br/>'), errs : '选项 和 答案 顺序错误'}
          }
        }
      }
      if (analysisIndex !== -1 && answerIndex > analysisIndex) {// 如果存在解析，并且答案在解析之后
        return {title : questionTxtArr.join('<br/>'), errs : '答案 和 解析 顺序错误'}
      }


      let type = 5; // 试题类型
      let title = questionTxtArr.slice(0, optionIndexArr.length > 0 ? optionIndexArr[0] : answerIndex).join("<br/>").replace(/^\d[.。、]?/, '') // 题干（换行转br标签）
      if (optionIndexArr.length > 0) {// 如果有选项
        type = /^\[[A-Ga-g]{2,}\]/.test(questionTxtArr[answerIndex]) ? 2 : 1 // 找到大于一个答案就是多选，否则单选（格式不对可能没有答案，后面处理）
      } else if (/_{5,}/.test(title)) { // 如果题干有连续大于等于五个的下划线，类型为填空
        type = 3
      } else if (/^\[[对错是否√×]\]/.test(questionTxtArr[answerIndex])) {// 答案行包含对错是否√×，类型为判断
        type = 4
      }

      let answerEndIndex = analysisIndex !== -1 ? analysisIndex : questionTxtArr.length // 有解析截取到解析，否则剩余内容都是
      let answerGroup = questionTxtArr.slice(answerIndex, answerEndIndex).join('<br/>').match(/\[(.+?)\]/g) // 按中括号拆分出答案、分数、阅卷选项
      let scoreArr = [], answerArr = [], markTypeArr = [], subjective = false
      for (let answer of answerGroup) {
        answer = answer.substring(1, answer.length - 1)
        if (/^\d+分$/.test(answer)) {
          scoreArr.push(parseInt(answer.replace('分', '')))
        } else if (/^答案无顺序$/.test(answer)) {
          markTypeArr.push(2)
        } else if (/^大小写不敏感$/.test(answer)) {
          markTypeArr.push(3)
        }  else if (/^主观题$/.test(answer)) {
          subjective = true
        } else {
          answerArr.push(answer) // 剩下都归答案
        }
      }


      let options = [], fillBlanksCount = 0
      if (type === 1 || type === 2) {// 如果是单多选
        if (type === 2 && scoreArr.length > 1) {// 如果是多选并且有第二个分数
          if (scoreArr[0] <= scoreArr[1]) {
            return {title : questionTxtArr.join('<br/>'), errs : '漏选分值不能大于总分'}
          }
        }
        if (optionIndexArr.length < 2) {
          return {title : questionTxtArr.join('<br/>'), errs : '最少2个选项'}
        }
        if (optionIndexArr.length > 7) {
          return {title : questionTxtArr.join('<br/>'), errs : '最多7个选项'}
        }
        for (let i = 0; i < optionIndexArr.length - 1; i++) {
          let optionContent = questionTxtArr.slice(optionIndexArr[i], optionIndexArr[i + 1]).join("<br/>") // 回车行转br标签
          let optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt() - 65 
          optionContent = optionContent.replace(/^[A-Za-z][.。、]?/, '')
          options.push(optionContent)
          
          if (i !== optionIndex) {
            return {title : questionTxtArr.join('<br/>'), errs : '选项顺序错误'}
          }
        }
        let optionContent = questionTxtArr.slice(optionIndexArr[optionIndexArr.length - 1], answerIndex).join("<br/>") // 最后一个选项从当前行到答案行之间的都是
        let optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt() - 65
        optionContent = optionContent.replace(/^[A-Za-z][.。、]?/, '')
        options.push(optionContent)
        if (optionIndexArr.length - 1 !== optionIndex) {
          return {title : questionTxtArr.join('<br/>'), errs : '选项顺序错误'}
        }

        if (!/^\[[A-Ga-g]+\]/.test(questionTxtArr[answerIndex])) {// 如果答案不是abcdefg中的一个或多个
          return {title : questionTxtArr.join('<br/>'), errs : '答案超出范围'}
        }
      } else if (type === 3) {
        fillBlanksCount = title.match(/_{5,}/g).length
        if (answerArr.length === 1) {// 一个中括号内空格分开的词，对应题干的空。
          let answerCount = answerArr[0].split(/ +/).length//  + 1-n个空格
          if (fillBlanksCount !== answerCount) {
            return {title : questionTxtArr.join('<br/>'), errs : '填空数量和答案数量不相等'}
          }
        } else if (fillBlanksCount !== answerArr.length){// 如果填空有多个备选答案，一个中括号表示一个填空，一个填空内空格分开的词，表示该空的备选答案
          return {title : questionTxtArr.join('<br/>'), errs : '填空数量和答案数量不相等'}
        }
      } 


      // 拼装试题
      let analysis = analysisIndex === -1 ? '' : questionTxtArr.slice(analysisIndex, questionTxtArr.length - 1).join("<br/>").replace(/^\[解析]/, '')
      let markType = (type === 5 || (type === 3 && subjective)) ? 2 : 1  // 如果是问答题或（填空带主观题）设置为主观题，剩下是客观题
      let score = 1, answerScores = []
      if (type === 1) {// 单选
        score = scoreArr.length > 0 ? scoreArr[0] : 1 // 如果有则使用，没有默认为1分
        answerArr.length = 1 // 去除多余答案
      } else if (type === 4) {
        score = scoreArr.length > 0 ? scoreArr[0] : 1 // 如果有则使用，没有默认为1分
        answerArr.length = 1 // 去除多余答案
        answerArr[0] = answerArr[0].replace(/^[是√]/, '对').replace(/[否×]/, '错') 
      } else if (type === 5) {// 问答
        score = scoreArr.length > 0 ? scoreArr[0] : 1 // 如果有则使用，没有默认为1分
        answerArr.length = 1 // 去除多余答案
        answerArr[0] = answerArr[0].replace(/<br\/>/g, '\n')
      } else if (type === 2) {// 多选
        score = scoreArr.length > 0 ? scoreArr[0] : 1
        answerScores.push(scoreArr.length > 1 ? scoreArr[1] : score / 2) // 如果有第二个分数则使用，没有默认为总分一半
        if (answerArr.length > 2) {
          answerArr.length = 2
        }
        answerArr = answerArr[0].split("") // 答案拆分，满足接口
      } else if (type === 3) {// 填空
        score = 0;
        for (let i = 0; i < fillBlanksCount; i++) {
          answerScores.push(scoreArr[i] ? scoreArr[i] : 1)
          score += answerScores[i]
        }
        if (fillBlanksCount > 1 && answerArr.length === 1) {
          answerArr = answerArr[0].split(/ +/) // 如果多个空一个答案则进行拆分，满足接口需求
        }
        if (markType === 2) {
          markTypeArr = [] // 主观题没有阅卷选项
          answerScores = [] // 主观题没有子分数
        }
      }

      let question = {
        type, 
        title,
        options,
        answers : type === 5 ? answerArr[0] : answerArr,
        score,
        answerScores,
        analysis,
        markType,
        markTypeOptions: markTypeArr,
        state: 1,
        questionTypeId: this.questionTypeId,
        errs: [],
      }

      return question
    },
    preStep() {
      if (this.isBack) {
        this.$router.back()
      } else {
        this.$parent.activeIndex = 0
      }
    },
    async txtImport() {
      if (this.errNum > 0) {
        this.$message.warning('错误格式' + this.errNum + '处，请处理')
        return
      }

      for (let question of this.list.questionList) {
        await questionAdd(question).catch((err) => {
          console.error(err)
        })
      }
    }
  }, 
  watch: {
    'list.questionList': {
      handler() {
        this.errNum = 0, this.total = this.list.questionList.length
        for (let question of this.list.questionList) {
          if (question.errs.length > 0) {
            this.errNum++
          }
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.batch-input-container {
  width: 100%;
  height: calc(100% - 38px);
  padding: 20px 20px;
  display: flex;
  .left {
    width: 40%;
    height: 100%;
    background-color: #fff;
    .qestion-box {
      height: calc(100% - 38px);
      overflow: scroll;
      border: 1px solid #D4D4D4;
      margin: 10px 0;
      // background: #fff;
    }
  }
  .right {
    width: 60%;
    height: 100%;
    background-color: #fff;
    .top-box {
      display: flex;
      justify-content: space-between;
      color: #202E36;
      font-weight: 600;
      .success-color {
        color: #6DB39B
      }
      .err-color {
        color: #F3A0A4
      }
    }
    .qestion-box {
      height: calc(100% - 38px);
      overflow: scroll;
      border: 1px solid #D4D4D4;
      margin: 10px 0;
      // background: #fff;
    }
    .err-msg {
      padding: 10px;
      background-color: #f00;
      color: #fff;
      font-weight: 900;
      text-indent: 2em;
    }
  }
}
</style>
