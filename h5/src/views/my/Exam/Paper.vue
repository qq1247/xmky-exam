<template>
  <div class="content">
    <!-- 答题卡 -->
    <div class="paper-router">
      <el-divider>答题卡</el-divider>
      <div class="router-content">
        <div
          v-for="(myQuestion, index) in myQuestions"
          :key="index"
          :style="{'display': myQuestion.type === 1 ? 'block' : 'inline-block'}"
        >
          <div v-if="myQuestion.type === 1" class="router-title">
            {{myQuestion.chapterName}}
          </div>
          <div v-else>
            <a :class="['router-index']" @click="toHref(myQuestion.question.no)">
              {{myQuestion.question.no}} 
            </a>
          </div>
        </div>
      </div>
    </div>
    <!-- 试卷 -->
    <el-scrollbar class="paper-content">
      <div class="top">
        <div class="top-title">
          <span>{{exam.paperName}}</span>
          <div class="top-score">
            {{myExam.totalScore}}分
            <i class="common common-fenshudixian fenshudixian"></i>
          </div>
        </div>
      </div>
      <div class="content-center"
        v-for="(myQuestion, index) of myQuestions"
        :key="index"
      >
        <div v-if="myQuestion.type === 1" class="chapter">
          <el-input
            :value="myQuestion.chapterName"
            placeholder=""
            maxlength="16"
          />
          <el-input
            :value="myQuestion.chapterTxt"
            :rows="2"
            placeholder=""
            type="textarea"
            :autosize="true"
            maxlength="128"
          />
        </div>
        <Question
          v-else-if="myQuestion.type === 2"
          :question="myQuestion.question"
          :no="myQuestion.question.no"
          :preview="preview"
          @updateAnswer="updateAnswer"
        >
        </Question>
      </div>
      <div style="height:50px"></div>
    </el-scrollbar>
  </div>
</template>
<script>
import Question from '@/components/Question/Question.vue'
import { myExamGet, myExamPaper, myExamAnswer } from 'api/my' 
import { examGet } from 'api/exam' 
export default {
  components: {
    Question,
  },
  data() {
    return {
      exam: {},
      myExam: {},
      myQuestions: [],
      preview: true,
    }
  },
  computed: {
  },
  mounted() {
    examGet({
      id: this.$route.params.examId
    }).then(({data}) => {
      this.exam = data
    })

    myExamGet({
      examId: this.$route.params.examId
    }).then(({data}) => {
      this.myExam = data

      if (this.myExam.state === 3 // 不用计算方法是因为Question组件需要先确定值
        || (this.myExam.state === 1 && this.myExam.markState === 3)) {
        this.preview = true 
      } else {
        this.preview = false
      }
    })

    myExamPaper({
      examId: this.$route.params.examId
    }).then(({data}) => {
      this.myQuestions = data

      let no = 1
      this.myQuestions.forEach((myQuestion) => {
        if (myQuestion.type === 2) {
          myQuestion.question.no = no++
        }
      });
    })
  },
  methods: {
    async updateAnswer(question) {
      const res = await myExamAnswer({
        examId: this.exam.id,
        questionId: question.id,
        answers: question.answers
      })
    }
  },
}
</script>

<style lang="scss" scoped>
.content-center :hover{
  padding-top: 20px;
  padding-bottom: auto;
}
.btn:hover {
  padding-bottom: 5px;
} 
.question-opt {
  color: #557587;
  height: 32px;
  margin-top: 10px;
  display: none;
  /deep/ .el-input {
    width: 50px;
  }
  /deep/ .el-input-number--small {
    width: 50px;
  }
  /deep/ .el-input-number--small .el-input-number__increase, /deep/ .el-input-number--small .el-input-number__decrease{
    display: none;
    width: 50px;
  }
  /deep/ .el-input--small .el-input__inner {
    border: none;
    border-radius: 0;
    border-bottom: 1px solid #557587;
  }
  /deep/ .el-input--small .el-input__inner {
    width: 50px;
    padding-left: 0;
    padding-right: 0;
  }
}
.paper-router {
  width: 240px;
  background: #fff;
  position: relative;
  padding: 15px;
  /deep/ .el-divider__text.is-center {
    color: #596a76;
  }
  .total-score {
    background: #0094e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 40px;
    left: 0;
    z-index: 100;
  }
}
.router-content {
  padding: 10px;
  .router-title {
    line-height: 40px;
    font-size: 14px;
    font-weight: 600;
    color: #0c2e41;
  }

  .router-index {
    position: relative;
    width: 28px;
    height: 28px;
    color: #41baff;
    line-height: 28px;
    font-weight: 600;
    background: #e3f4fe;
    text-align: center;
    display: inline-block;
    margin-bottom: 10px;
    margin-right: 10px;
    border-radius: 3px;
    text-decoration: none;
    border-radius: 6px;
    cursor: pointer;
    user-select: none;
    &:nth-child(10n) {
      margin-right: 0;
    }
  }

  .router-active,
  a:hover {
    background: #0094e4;
    // border: 1px solid #7fc2f6;
    color: #fff;
  }
}
.content {
  display: flex;
  width: 1200px;
  height: calc(100vh - 240px);
  margin: 0 auto;
}
.paper-handler {
  width: 400px;
  background: #fff;
  position: relative;
  border-radius: 8px;
  .paper-title {
    line-height: 40px;
    background: #fff;
    width: 100%;
    height: 40px;
    font-size: 16px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding: 0 20px;
    border-radius: 8px 8px 0 0;
    border-bottom: 1px solid #eee;
    &::before {
      content: '';
      display: inline-block;
      position: relative;
      top: 5px;
      left: -10px;
      width: 2px;
      height: 20px;
      background: #0094e5;
    }
  }
}

.top {
  background: #fff;
  width: calc(1200px - 410px);
  height: 80px;
  padding: 30px 10px 10px 10px;
  color: #333;
  z-index: 100;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  border-radius: 8px 8px 0 0;
  .top-handler {
    display: none;
    position: absolute;
    right: 10px;
    top: 10px;
    /deep/ .el-button {
      padding: 5px 10px;
    }
    z-index: 1;// 滚动试题时不能点击
  }
  .top-title {
    flex: 1;
    text-align: center;
    font-size: 20px;
    vertical-align: middle;
    .top-score {
      position: relative;
      display: flex;
      float: right;
      font-size: 20px;
      flex-direction: column;
      top: -26px;
      right: 28px;
      z-index: 1;
      color: #FF5722;
    }
    .fenshudixian {
      font-size: 53px;
      margin-top: -25px;
      margin-right: -4px;
    }
    
    /deep/ .el-input__inner {
      text-align: center;
      border: 0;
      font-size: 20px;
      font-weight: bold;
    }
  }
}
/deep/ .el-drawer__header {
  margin-bottom: 0px;
}
.handler-content {
  padding: 10px 10px 10px;
}
.chapter {
  padding: 15px 0px 0px 17px;
  /deep/ .el-input__inner {
    border: none;
    font-size: 18px;
  }
  /deep/ .el-textarea__inner {
    border: none;
    color: #557587;
  }
}
</style>
<style lang="scss">
.paper-content {
  background: #fff;
  width: calc(100% - 400px);
  margin-left: 10px;
  border-radius: 8px;
  &:hover {
    .top {
      .top-handler {
        display: inline-block;
      }
    }
  }
}


</style>