<template>
  <div class="batch-input-container">
    <div class="left">
      <div class="btn-box">
        <el-button plain @click="preStep()" size="mini" >返回</el-button>
        <el-button plain @click="showEg()" size="mini" >示例</el-button>
      </div>

      <mavon-editor
        class="qestion-box"
        :value="questionTxt"
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

        <div v-if="process === 0">
          <el-button type="danger" size="mini" @click="locationErr()" plain>定位错误</el-button>
          <el-button type="primary" size="mini" @click="txtImport()">导入</el-button>
        </div>
        <div v-else style="width: 200px">
            <el-progress :percentage="process"></el-progress>
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
      questionTxt: '',
      process: 0, //进度条
      questionList: [], // 试题列表（接口格式）
      list: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 5, // 每页多少条
        questionList: [] // 列表数据（显示格式）
      },
      total: 0, // 题目总数量
      errNum: 0, // 错误的题目数量
      eg: false, // 示例
      egBak: '',
      egTxt: `1.这是一道单选题的题干，简单写法
A.单选题的A选项
B。单选题的B选项
C、单选题的C选项
D单选题的D选项
[B]

1.这是一道多选题的题干，
可换行，可选写法
A.多选题的A选项，
B。多选题的B选项
C、多选题的C选项
D多选题的D选项
[AB][3分][1分]
[解析]中括号内【字母】表示答案，一个答案表示单选题，一个以上答案表示多选题
中括号内【数字分】表示该题分数，第一项为该题分数，不填默认1分，第二项为漏选分数，多选题有效，不填默认为总分一半

3、这是一道填空题_____，_____，简单写法
[北京 上海][主观题]

3、这是一道填空题________，________，可选写法
[山西 山西省 晋][老婆 媳妇 内人][2分][2分][答案无顺序][大小写不敏感]
[解析]连续五个或五个以上的下划线，表示一个填空
中括号内【主观题】表示这道题需要人工阅卷，[答案无顺序][大小写不敏感]无效
中括号内【答案无顺序】【大小写不敏感】，表示智能阅卷时判分规则，默认答案有顺序，大小写敏感
中括号内【数字分】表示该题分数，一个空对应一个分数，不填或少填，对应的空默认为1分
中括号内【文字】表示答案，n个填空就有n个答案，用空格分割表示。如：[北京 上海]
中括号内【文字】表示答案，如果填空有多个备选答案，则用多组答案表示，一组答案表示对应填空的答案，一个答案内用空格分割表示多个备选答案。如：[山西 山西省 晋][老婆 媳妇 内人]

4、这是一道判断题的题干，简单写法
[对]

4、这是一道判断题的题干，可选写法
[√][2分]
[解析]中括号内【文字】表示答案，可填“对错√×是否”
中括号内【数字分】表示该题分数，不填默认1分

5、这是一道问答题的题干，简单写法
[我是问答题的答案]

5、这是一道问答题的题干，可选写法
[我是问答题的答案，
可换行
换行][10分]
[解析]中括号内【文字】表示答案
中括号内【数字分】表示该题分数，不填默认1分
默认为主观题，需要人工阅卷
      `,
    }
  },
  methods: {
    parseTxt(txt) {
      // 拆分文本，每个文本为一道题
      this.questionTxt = txt // 用于切换到示例 
      let questionTxtArr = this.splitTxt2QuestionTxt(txt)

      // 文本解析为试题可读字段
      this.questionList.splice(0)
      this.list.questionList.splice(0)
      for (let i in questionTxtArr) {
        let question = this.parseQuestion(questionTxtArr[i])
        question.id = parseInt(i) // 用于错题定位
        if (question.errs && question.errs.length > 0) {
          question.title = question.txt
        }
        this.questionList.push(question)

        // 组装成组件需要的格式
        let _question = JSON.parse(JSON.stringify(question)) 
        if (_question.type === 1) {
          _question.answers = _question.answers[0]
          for (let i = 0; i < _question.options.length; i++) {
            _question.options[i] = {'no' : String.fromCharCode(65 + i), option : _question.options[i]}
          }
        } else if (_question.type === 2) {
          for (let i = 0; i < _question.options.length; i++) {
            _question.options[i] = {'no' : String.fromCharCode(65 + i), option : _question.options[i]}
          }
        } else if (_question.type === 4) {
          _question.answers = _question.answers[0]
        }
        this.list.questionList.push(_question)
      }
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
        return {txt : questionTxtArr.join('<br/>'), errs : '缺少答案'}
      }
      if (optionIndexArr.length > 0) {// 如果存在（单多选）选项，并且选项在答案之后
        for (let optionIndex of optionIndexArr) {
          if (optionIndex >= answerIndex) {
            return {txt : questionTxtArr.join('<br/>'), errs : '选项 和 答案 顺序错误'}
          }
        }
      }
      if (analysisIndex !== -1 && answerIndex > analysisIndex) {// 如果存在解析，并且答案在解析之后
        return {txt : questionTxtArr.join('<br/>'), errs : '答案 和 解析 顺序错误'}
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
            return {txt : questionTxtArr.join('<br/>'), errs : '漏选分值不能大于总分'}
          }
        }
        if (optionIndexArr.length < 2) {
          return {txt : questionTxtArr.join('<br/>'), errs : '最少2个选项'}
        }
        if (optionIndexArr.length > 7) {
          return {txt : questionTxtArr.join('<br/>'), errs : '最多7个选项'}
        }
        for (let i = 0; i < optionIndexArr.length - 1; i++) {
          let optionContent = questionTxtArr.slice(optionIndexArr[i], optionIndexArr[i + 1]).join("<br/>") // 回车行转br标签
          let optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt() - 65 
          optionContent = optionContent.replace(/^[A-Za-z][.。、]?/, '')
          options.push(optionContent)
          
          if (i !== optionIndex) {
            return {txt : questionTxtArr.join('<br/>'), errs : '选项顺序错误'}
          }
        }
        let optionContent = questionTxtArr.slice(optionIndexArr[optionIndexArr.length - 1], answerIndex).join("<br/>") // 最后一个选项从当前行到答案行之间的都是
        let optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt() - 65
        optionContent = optionContent.replace(/^[A-Za-z][.。、]?/, '')
        options.push(optionContent)
        if (optionIndexArr.length - 1 !== optionIndex) {
          return {txt : questionTxtArr.join('<br/>'), errs : '选项顺序错误'}
        }

        if (!/^\[[A-Ga-g]+\]/.test(questionTxtArr[answerIndex])) {// 如果答案不是abcdefg中的一个或多个
          return {txt : questionTxtArr.join('<br/>'), errs : '答案超出范围'}
        }
      } else if (type === 3) {
        fillBlanksCount = title.match(/_{5,}/g).length
        if (answerArr.length === 1) {// 一个中括号内空格分开的词，对应题干的空。
          let answerCount = answerArr[0].split(/ +/).length//  + 1-n个空格
          if (fillBlanksCount !== answerCount) {
            return {txt : questionTxtArr.join('<br/>'), errs : '填空数量和答案数量不相等'}
          }
        } else if (fillBlanksCount !== answerArr.length){// 如果填空有多个备选答案，一个中括号表示一个填空，一个填空内空格分开的词，表示该空的备选答案
          return {txt : questionTxtArr.join('<br/>'), errs : '填空数量和答案数量不相等'}
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
        if (fillBlanksCount > 1 && answerArr.length === 1) {// 如果答案是写在一块按空格分割的，则进行拆分，满足接口需求
          answerArr = answerArr[0].split(/ +/) 
        } else {// 如果答案分开写，一个答案按回车分割备选答案，满足接口需求
          answerArr = answerArr.map(answer => {
            return answer.split(/ +/).join('\n')
          })
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
        txt: questionTxtArr.join('\n'), // 保留原始文本，用于上传失败二次处理
        errs: [],
      }

      return question
    },
    preStep() {
      if (this.isBack) {
        this.$router.back()
      } else {
        this.$parent.activeIndex = 1
        this.$parent.createdType = 0
      }
    },
    // 文本导入
    async txtImport() {
      if (this.errNum > 0) {
        this.$message.warning('错误格式' + this.errNum + '处，请处理')
        return
      }

      let errTxt = ''
      for (let i in this.questionList) {
        await questionAdd(this.questionList[i]).catch((err) => {
          errTxt += this.questionList[i].txt + '\n'
        })
        this.process = Math.round(i  / this.questionList.length * 100 )
      }

      this.process = 0 // 上传完成恢复进度条

      if (errTxt.length > 0) {
        errTxt = '以下为错误试题文本，请联系管理员处理：\n' + errTxt
        this.questionTxt = errTxt.replace(/<br\/>/gm, '\n') // 正确的删除，展示错误的
      } else {
        this.questionTxt = '添加成功，请继续添加或返回'
      }
    }, 
    // 定位错误
    locationErr() {
      for (let i in this.list.questionList) {
        if (this.list.questionList[i].errs.length > 0) {
          let scrollContainer = document.getElementsByClassName('qestion-box')[1]
          let eleOffSetTop = document.getElementById('p-' + i).offsetTop - 130
          scrollContainer.scrollTop = eleOffSetTop
        }
      }
      
    },
    // 显示示例
    showEg() {
      if (!this.eg) {
        this.eg = true
        this.egBak = this.questionTxt
        this.questionTxt = this.egTxt
      } else {
        this.eg = false
        this.questionTxt = this.egBak
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
    .btn-box {
      padding: 4px;
    }
    .qestion-box {
      height: calc(100% - 38px);
      overflow: scroll;
      border: 1px solid #D4D4D4;
      margin: 10px 0;
      // background: #fff;
    }
    ::-webkit-scrollbar {
      display: block;
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
      padding: 4px;
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
