<template>
  <div class="container">
    <div :class="['exam-top', activeIndex == 1 && createdType == 1 ? 'exam-top-height': '']">
      <div class="exam-nav-box">
        <div
          v-for="(item, index) in navList"
          :key="index"
          :class="['exam-nav-item', activeIndex == index ? 'exam-nav-active' : 'exam-nav-not']"
          @click="activeIndex == index ? switchTab(index) : ''"
        >
          {{ item }}
        </div> 
      </div>
      <PaperSetting v-if="activeIndex === 0" @switchTab="switchTab"></PaperSetting>
      <QuestionTxtImport v-if="activeIndex === 1 && createdType === 1">
        <template #leftOpt>
          <el-button plain @click="preStep()" size="mini" >返回</el-button>
        </template>
        <template #rightOpt="{ errNum, questionList }" >
          <el-button type="primary" size="mini" @click="txtImport(errNum, questionList)">导入</el-button>
        </template>
      </QuestionTxtImport>
      <ExamSetting v-if="activeIndex === 2" style="height: calc(100vh - 220px);" ref="ExamSetting"/>
      <MarkSetting v-if="activeIndex === 3" style="height: calc(100vh - 220px);" ref="MarkSetting"/>
      <ExamPublish v-if="activeIndex === 4" style="height: calc(100vh - 220px);" ref="ExamPublish"/>
    </div>

    <Paper v-if="activeIndex == 1 && createdType === 0"  @toEditor="toEditor" ref="Paper"></Paper>
    <PaperRule v-if="activeIndex === 1 && createdType === 2" ref="PaperRule"></PaperRule>

    <div v-if="activeIndex !=0 && createdType !== 1" class="paper-footer" :style="{marginTop: activeIndex == 1 ? '-47px' : '-67px'}">
      <el-button type="primary" @click="back" size="small">上一步</el-button>
      <el-button type="primary" @click="next" size="small">{{activeIndex == 4 ? '发布' : '下一步'}}</el-button>
    </div>
    
    <!-- <div class="exam-bottom" v-if="activeIndex == 0">
      <el-form ref="paperForm" :model="paperForm" :rules="paperForm.rules" label-width="140px" label-position="left">
        <el-form-item label="从考试中选择" prop="selectPaperId">
          <CustomSelect
            ref="paperSelect"
            style="width: 100%"
            placeholder="请选择考试"
            :multiple="false"
            :value="paperForm.selectPaperId"
            :total="paperForm.total"
            @input="searchPaper"
            @change="selectPaper"
            @currentChange="getMorePaper"
            @visibleChange="getPaperList"
          >
            <el-option
              v-for="item in paperForm.paperList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-form>
    </div> -->
  </div>
</template>

<script>
import PaperSetting from './PaperSetting.vue'
import Paper from './Paper.vue'
import QuestionTxtImport from '@/components/Question/QuestionTxtImport.vue'
import ExamSetting from './ExamSetting.vue'
import ExamPublish from './ExamPublish.vue'
import MarkSetting from './MarkSetting.vue'
import PaperRule from './PaperRule.vue'
import CustomSelect from 'components/CustomSelect.vue'
import { mapMutations } from 'vuex'
import { examDetail } from 'api/exam'
import * as dayjs from 'dayjs'

export default {
  components: {
    CustomSelect,
    PaperSetting,
    Paper,
    QuestionTxtImport,
    ExamSetting,
    ExamPublish,
    MarkSetting,
    PaperRule,
  },
  data() {
    return {
      id: null,
      activeIndex: 0,
      createdType: 0,
      navList: ['1.选择试卷', '2.添加试题', '3.配置规则', '4.添加用户', '5.发布考试'],
    }
  },
  computed: {
  },
  mounted() {
    this.id = this.$route.params.id
    if (this.id) {
      examDetail({
        id: this.id,
      }).then(({data}) => {
        this.init()
        if (data.exam.genType === 1) {// 根据组卷类型自动跳到下一步
          this.activeIndex = 1
          this.createdType = 0
        } else {
          this.activeIndex = 1
          this.createdType = 2
        }

        this.idUpdate(data.exam.id)
        this.nameUpdate(data.exam.name)
        this.paperNameUpdate(data.exam.paperName)
        this.timeTypeUpdate(data.exam.timeType)
        if (data.exam.timeType === 1) {
          this.examTimesUpdate([dayjs(data.exam.startTime, 'YYYY-MM-DD HH:mm:ss').toDate(), dayjs(data.exam.endTime, 'YYYY-MM-DD HH:mm:ss').toDate()])
        }
        if (data.exam.timeType === 1 && data.exam.markType === 2) {
          this.markTimesUpdate([dayjs(data.exam.markStartTime, 'YYYY-MM-DD HH:mm:ss').toDate(), dayjs(data.exam.markEndTime, 'YYYY-MM-DD HH:mm:ss').toDate()])
        }
        this.passScoreUpdate(data.exam.passScore)
        if (data.exam.genType === 1) {
          this.sxesUpdate(data.exam.sxes)
        }
        this.showTypeUpdate(data.exam.showType)
        this.anonStateUpdate(data.exam.anonState)
        this.scoreStateUpdate(data.exam.scoreState)
        this.rankStateUpdate(data.exam.rankState)
        this.genTypeUpdate(data.exam.genType)
        this.stateUpdate(data.exam.state)

        if (data.exam.genType === 1) {
          data.examQuestions.forEach(examQuestion => {
            if (examQuestion.type === 1) {
              this.chapterAdd({name: examQuestion.chapterName, txt: examQuestion.chapterTxt})
            } else {
              this.questionAdd(examQuestion.question)
            }
          })
          this.questionSort()
        } else {
          data.examRules.forEach((examRule, index) => {
            if (examRule.type === 1) {
              this.chapterAddForRule({name: examQuestion.chapterName, txt: examQuestion.chapterTxt})
            } else {
              this.ruleAdd()
              this.examRuleQuestionTypeIdUpdate({index, value: examRule.questionTypeId})
              this.examRuleQuestionTypeUpdate({index, value: examRule.questionType})
              this.examRuleMarkTypeUpdate({index, value: examRule.markType})
              this.examRuleMarkOptionsUpdate({index, value: examRule.markOptions})
              this.examRuleNumUpdate({index, value: examRule.num})
              this.examRuleScoreUpdate({index, value: examRule.score})
              this.examRuleScoresUpdate(examRule.scores)
              this.ruleSort()
            }
          })
        }

        data.examUsers.forEach((examUser, index) => {
          this.userGroupAdd()
          this.examUsersUpdate({index, value: examUser.examUserIds})
          this.markUserUpdate({index, value: examUser.markUserId})
        })
      })
    }
  },
  methods: {
    ...mapMutations('exam', [
      'init',
      'idUpdate',
      'questionAdd',
      'questionSort',
      'nameUpdate',
      'paperNameUpdate',
      'timeTypeUpdate',
      'examTimesUpdate',
      'markTimesUpdate',
      'passScoreUpdate',
      'sxesUpdate',
      'showTypeUpdate',
      'anonStateUpdate',
      'scoreStateUpdate',
      'rankStateUpdate',
      'genTypeUpdate',
      'stateUpdate',
      'chapterAdd',
      'questionSort',
      'chapterAddForRule',
      'ruleAdd',
      'examRuleQuestionTypeIdUpdate',
      'examRuleQuestionTypeUpdate',
      'examRuleNumUpdate',
      'examRuleScoreUpdate',
      'examRuleScoresUpdate',
      'examRuleMarkTypeUpdate',
      'examRuleMarkOptionsUpdate',
      'ruleSort',
      'userGroupAdd',
      'examUsersUpdate',
      'markUserUpdate',
    ]),
    preStep() {
      this.activeIndex = 1
      this.createdType = 0
    },
     // 切换标签
    switchTab(index, createdType) {
      this.createdType = createdType
      this.activeIndex = index
    },

    //上一步 
    back() {
      this.activeIndex--
    },
    //下一步
    next() {
      if (this.activeIndex === 1 && this.createdType === 2) {
        this.$refs['PaperRule'].next()
      } else if (this.activeIndex == 2) {
        this.$refs['ExamSetting'].next()
      }
      else if (this.activeIndex == 3) {
        this.$refs['MarkSetting'].next()
      }
      else if (this.activeIndex == 4) {
        this.$refs['ExamPublish'].publish()
      } else {
        this.activeIndex++
      }
    },
    // 编辑页面
    toEditor() {
      this.activeIndex = 1
      this.createdType = 1
    },
    // 文本导入
    async txtImport(errNum, questionList) { 
      if (errNum > 0) {
        this.$message.warning('试题错误格式' + errNum + '处，请处理')
        return
      }

      questionList.forEach(question => {
        this.questionAdd(question)
      });

      this.questionSort()

      this.$message.info('导入成功')
      this.preStep();
    }, 
  }
}
</script>

<style lang="scss" scoped>
.exam-top-height {
  height: 99%;
}
.exam-top {
  width: 100%;
  // height: 260px;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  border-radius: 15px;
  background-color: #fff;
  .exam-nav-box {
    width: 100%;
    min-height: 40px;
    display: flex;
    align-items: center;
    background-color: #f0f9fe;
    border-top-left-radius: 15px;
    border-top-right-radius: 15px;
    .exam-nav-item {
      width: calc(100% / 5);
      min-height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      color: #687983;
      cursor: pointer;
      &:first-child {
        border-top-left-radius: 15px;
      }
      &:last-child {
        border-top-right-radius: 15px;
      }
    }
    .exam-nav-active {
      background: #0094e4;
      color: #DDF5FB;
    }
    .exam-nav-not {
      cursor: not-allowed;
    }
  }
  .footer {
    display: flex;
    margin: 15px;
    justify-content: flex-end;
  }
}

.paper-footer {
  display: flex;
  height: 32px;
  margin-right: 15px;
  margin-top: -47px;
  justify-content: flex-end;
  z-index: 9;
}
.exam-bottom {
  display: flex;
  padding: 15px;
  background-color: #fff;
  border-radius: 15px;
  /deep/ .el-form {
    width: 100%;
  }
  /deep/ .el-form-item {
    margin-bottom: 0;
  }
}
</style>
