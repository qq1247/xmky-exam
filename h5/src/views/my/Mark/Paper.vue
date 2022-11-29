<template>
  <div class="content">
    <!-- 答题卡 -->
    <div class="content-left">
      <div class="paper-userinfo">
        <el-avatar
          class="active-border"
          :size="68"
          src="http://192.168.110.100:9000/api/login/entLogo"
          ><i class="common common-wo"
        /></el-avatar>
        <div class="user-name">{{user.name}} / {{user.orgName}}</div>
        <div v-if="myExam.state === 3 || myExam.markState === 3" class="userinfo-box">
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-time.png"
              alt=""
            />
            <div class="userinfo-score">{{myExam.objectiveScore}}</div>
            <div>客观题</div>
          </div>
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-pass.png"
              alt=""
            />
            <div class="userinfo-score">{{myExam.totalScore}}分</div>
            <div>总分</div>
          </div>
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-score.png"
              alt=""
            />
            <div class="userinfo-score">{{exam.totalScore}}分</div>
            <div>满分</div>
          </div>
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-pass.png"
              alt=""
            />
            <div>{{ $tools.getDictValue('ANSWER_STATE', myExam.answerState) }}</div>
            <div>成绩</div>
          </div>
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-score.png"
              alt=""
            />
            <div class="userinfo-score">第{{myExam.no || '--'}}名</div>
            <div>考试排名</div>
          </div>
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-answer-time.png"
              alt=""
            />
            <div>{{$tools.computeMinute(myExam.answerStartTime, myExam.answerEndTime)}}分钟</div>
            <div>答题用时</div>
          </div>
          <div class="userinfo-item">
            <img
              class="item-icon"
              src="~@/assets/img/mark/mark-time.png"
              alt=""
            />
            <div>{{$tools.computeMinute(myExam.markStartTime, myExam.markEndTime)}}分钟</div>
            <div>阅卷用时</div>
          </div>
        </div>
      </div>
      <div class="paper-router">
        <el-progress 
          :percentage="50" 
          style="width: 277px;" 
          :format="(percentage) => '试卷批阅进度：14/53'"
        ></el-progress>
        <el-switch
          style="display: block;margin-top: 15px;"
          v-model="subjectiveQuestionShow"
          inactive-text="全部试题显示"
          active-text="主观试题显示">
        </el-switch>
        <el-switch
          style="display: block;margin-top: 15px;"
          v-model="allPaperShow"
          inactive-text="未阅试卷显示"
          active-text="全部试卷显示">
        </el-switch>
      </div>
    </div>
    <!-- 试卷 -->
    <el-scrollbar class="paper-content">
      <div class="top">
        <div class="top-title">
          <span>{{exam.paperName}}</span>
          <!-- 已阅卷在显示分数 -->
          <div v-if="myExam.markState === 3" class="top-score">
            {{myExam.totalScore}}分
            <i class="common common-fenshudixian fenshudixian"></i>
          </div>
        </div>
      </div>
      <div class="content-center"
        v-for="(myQuestion, index) of myQuestionsWithFilter"
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
          :question="myQuestion.question"
          :no="myQuestion.question.no"
          :preview="true"
        >
          <template #bottom>
            <div v-if="myQuestion.question.markType === 2" class="question-bottom" >
              <div>
                <span>本题得</span>
                <el-input-number
                  v-model="myQuestion.question.userScore"
                  :min="0"
                  :max="myQuestion.question.score"
                  size="small"
                ></el-input-number>
                <span>分</span>
                <span style="color: #0094e5">（满分：{{ myQuestion.question.score }}分）</span>
                <el-button type="success" size="mini" @click="scoreUpdate(myQuestion, '')">确定</el-button>
                <el-button type="primary" size="mini" plain @click="nextPaper(myQuestion, 'next')">下一卷<i class="el-icon-arrow-right el-icon--right"></i></el-button>
                <el-button type="primary" size="mini" plain icon="el-icon-arrow-left" @click="scoreUpdate(myQuestion, 'pre')">上一卷</el-button>
                <el-tooltip placement="top" effect="light">
                  <div slot="content">
                    答案：{{myQuestion.question.answersBak}}<br/>
                    解析：{{myQuestion.question.analysis || '无'}}
                  </div>
                  <el-button type="warning" size="mini" plain>查看标准答案</el-button>
                </el-tooltip>
                <!-- <el-button :type="myQuestion.question.showUserAnswer?'info':'danger'" size="mini" :plain="myQuestion.question.showUserAnswer" @click="answerSwitch(myQuestion)">查看{{myQuestion.question.showUserAnswer?'标准':'用户'}}答案</el-button> -->
              </div>
              <div style="padding-top: 10px;">
                <el-button type="primary" size="mini" plain style="border: 0px;" @click="myQuestion.question.userScore = 0">0</el-button>
                <template v-if="myQuestion.question.score <= 5">
                  <template v-for="i in Math.ceil(myQuestion.question.score)">
                    <el-button :key="i" type="primary" size="mini" plain style="border: 0px;" @click="myQuestion.question.userScore = i - 0.5">{{i - 0.5}}</el-button>
                    <el-button :key="i*100" type="primary" size="mini" plain style="border: 0px;" @click="myQuestion.question.userScore = i">{{i}}</el-button>
                  </template>
                </template>
                <el-button 
                  v-else 
                  v-for="i in Math.ceil(myQuestion.question.score)"
                  :key="i" type="primary" size="mini" plain style="border: 0px;" @click="myQuestion.question.userScore = i">{{i}}</el-button>
              </div>
            </div>
          </template>
        </Question>
      </div>
      <div style="height:50px"></div>
    </el-scrollbar>
  </div>
</template>
<script>
import Question from '@/components/Question/Question.vue'
import CountDown from '@/components/CountDown.vue'
import { myMarkPaper, myMarkGet, myMarkScore, myMarkUserListpage } from 'api/my'
import { examGet } from 'api/exam' 
import { userGet } from 'api/user' 
export default {
  components: {
    Question,
    CountDown,
  },
  data() {
    return {
      curUserId: 0,
      subjectiveQuestionShow: true, 
      allPaperShow: true, 
      user: {},
      exam: {},
      myExam: {},
      myQuestions: [],
      users: [],
      preview: true,
    }
  },
  computed: {
    myQuestionsWithFilter() {
      return this.myQuestions.filter(myQuestion => {
        return this.subjectiveQuestionShow ? myQuestion.type === 2 && myQuestion.question.markType === 2 : true
      })
    },
  },
  async mounted() {
    await this.initUsers()
    this.curUserId = this.$route.params.userId;

    examGet({
      id: this.$route.params.examId
    }).then(({data}) => {
      this.exam = data
    })
  },
  watch: {
    curUserId: function (newUserId) {
      userGet({
        id: newUserId
      }).then(({data}) => {
        this.user = data
      })

      myMarkPaper({
        examId: this.$route.params.examId,
        userId: newUserId,
      }).then(({data}) => {
        this.myQuestions = data

        let no = 1
        this.myQuestions.forEach((myQuestion) => {
          if (myQuestion.type === 2) {
            myQuestion.question.no = no++
            myQuestion.question.answersBak = myQuestion.question.answers
            myQuestion.question.answers = myQuestion.question.userAnswers
            myQuestion.question.showUserAnswer = true
          }
        });
      })
      
      myMarkGet({
        examId: this.$route.params.examId,
        userId: newUserId,
      }).then(({data}) => {
        this.myExam = data
      })
    }
  },
  methods: {
    // 加载所有考试用户，用于切换下一个试卷
    async initUsers() {
      let curPage = 1
      while(true) {
        const { data } = await myMarkUserListpage({
          examId: this.examId,
          curPage: curPage++,
          pageSize: 100,
        })
        this.users.push(...data.list)
        if (this.users.length >= data.total) {
          break
        }
      }

      this.users = this.users.filter(user => { // 未考试的不参与阅卷
        return user.state === 3
      })
    },
    // 阅题
    async scoreUpdate(myQuestion, param) {
      if (myQuestion.question.userScore == null) {
        this.$message.error("请填写分数")
        return;
      }

      let finish = this.myQuestions.every(myQuestion => {// 所有试题是否阅完
        if (myQuestion.type === 1) {
          return true
        }
        if (myQuestion.type === 2) {
          if (myQuestion.question.userScore != null) {
            return true
          }
        }
        
        return false
      })

      const res = await myMarkScore({
        examId: this.exam.id,
        userId: this.user.id,
        questionId: myQuestion.question.id,
        userScore: myQuestion.question.userScore,
        finish
      })

      this.$message('阅题成功')

      if (finish) {// 全部阅完，更新页面信息
          myMarkGet({
            examId: this.$route.params.examId
          }).then(({data}) => {
            this.myExam = data
          })
      }
    },
    // 切换答案
    // answerSwitch(myQuestion) {
    //   if (myQuestion.question.showUserAnswer) {
    //     myQuestion.question.answers = myQuestion.question.answersBak
    //     myQuestion.question.showUserAnswer = false
    //   } else {
    //     myQuestion.question.answers = myQuestion.question.userAnswers
    //     myQuestion.question.showUserAnswer = true
    //   }
    // },
    // 上一卷
    prePaper() {
      let curIndex =this.users.findIndex(user => {
        return user.userId == this.curUserId
      })
      if (this.users[curIndex]) {
        this.curUserId = this.users[--curIndex].userId
      } else {
        this.$message.warning("已经第一卷")
      }
    },
    // 下一卷
    nextPaper() {
      let curIndex =this.users.findIndex(user => {
        return user.userId == this.curUserId
      })
      if (this.users[curIndex]) {
        this.curUserId = this.users[++curIndex].userId
      } else {
        this.$message.warning("已经最后一卷")
      }
    },
  },
}
</script>
<style lang="scss" scoped>
/deep/ .el-input-number--small {
  width: 45px;
}
/deep/ .el-input-number--small .el-input-number__increase,
/deep/ .el-input-number--small .el-input-number__decrease {
  display: none;
  width: 45px;
}
/deep/ .el-input--small .el-input__inner {
  border: none;
  border-radius: 0;
  border-bottom: 1px solid #557587;
}
/deep/ .el-input--small .el-input__inner {
  width: 45px;
  height: 16px;
  padding-left: 0;
  padding-right: 0;
}
.question-bottom {
  padding-top: 15px;
}
.content {
  padding-top: 20px;
  flex-direction: row;
} 
.content-center :hover{
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
.content-left {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.paper-userinfo {
  width: 260px;
  // height: 200px;
  background: #fff;
  padding: 15px;
  margin-bottom: 10px;
  text-align: center;
  .active-border {
    border: 2px solid #0094e5;
  }
  .user-name {
    margin: 5px 0px 10px 0px;
    color: #0c2e41;
    font-weight: 800;
  }
  .userinfo-box {
    display: flex;
    // flex-direction: row;
    flex-wrap: wrap;
    .userinfo-item {
      width: 57px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding-top: 10px;
      div {
        &:nth-child(2) {
          color: #0c2e41; 
          font-weight: bold;
        } 
        &:nth-child(3) {
          font-size: 12px;
          color: #557587
        } 
      }
      .userinfo-icon-backgroup {
        width: 36px;
        height: 32px;
        margin-bottom: 5px;
        border-radius: 7px;
        line-height: 32px;
      }
      .item-icon {
        width: 38px;
        height: 38px;
        padding-bottom: 5px;
      }
    }
  }
}
.paper-router {
  // flex: 1;
  width: 260px;
  background: #fff;
  position: relative;
  padding: 15px;
  /deep/ .el-progress__text {
    margin-left: 0px;
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
  height: calc(100vh - 140px);
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
.answer-card-time {
  border: 1px dashed #0094e5;
  padding: 5px;
  font-size: 14px;
  font-weight: 600;
  color: #0094e5;
  text-align: center;
  margin-bottom: 10px;
}
</style>
<style lang="scss">
.paper-content {
  height: 100%;
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