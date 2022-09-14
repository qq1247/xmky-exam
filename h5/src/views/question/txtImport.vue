<template>
  <div class="content">
    <QuestionTxtImport
      ref="QuestionTxtImport"
      :question-type-id="questionTypeId"
    >
      <template #leftOpt>
        <el-button plain @click="preStep()" size="mini" >返回</el-button>
      </template>
      <template #rightOpt="{ errNum, questionList }" >
        <el-button v-if="process === 0" type="primary" size="mini" @click="txtImport(errNum, questionList)">导入</el-button>
        <div v-else style="width: 200px">
            <el-progress :percentage="process"></el-progress>
        </div>
      </template>
    </QuestionTxtImport>
  </div>
</template>
<script>
import QuestionTxtImport from '@/components/Question/QuestionTxtImport.vue'
import { questionAdd } from 'api/question'
export default {
  components: {
    QuestionTxtImport
  },
  data() {
    return {
      questionTypeId: null, // 题库id
      process: 0, //进度条
    }
  },
  created() {
    this.questionTypeId = Number(this.$route.params.questionTypeId)
  },
  methods: {
     preStep() {
      this.$router.back()
    },
    // 文本导入
    async txtImport(errNum, questionList) {
      if (errNum > 0) {
        this.$message.warning('试题错误格式' + errNum + '处，请处理')
        return
      }

      let errTxt = ''
      for (let i in questionList) {
        let question = questionList[i]
        if (question.type === 3) {// 如果是hi填空题，处理下答案格式，符合接口
          question = JSON.parse(JSON.stringify(question))
          question.answers = question.answers.map(answer => {
            return answer.join("\n")
          })
        }
        await questionAdd(question).catch((err) => {
          errTxt += questionList[i].txt + '\n'
        })
        this.process = Math.round(i  / questionList.length * 100 )
      }

      this.process = 0 // 上传完成恢复进度条

      if (errTxt.length > 0) {
        errTxt = '以下为错误试题文本，请联系管理员处理：\n' + errTxt
        errTxt = errTxt.replace(/<br\/>/gm, '\n') // 正确的删除，展示错误的
      } else {
        errTxt = '添加成功，请继续添加或返回'
      }

      this.$message.warning(errTxt)
    }, 
  }
}
</script>
<style lang="scss" scoped>
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 200px);
  margin: 0 auto;
}
</style>
