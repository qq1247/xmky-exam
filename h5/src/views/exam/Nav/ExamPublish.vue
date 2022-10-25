<template>
  <div class="exam-publish-box">
    <!-- 考试信息 -->
    <div class="cross-line-box">
      <div class="left-cross-line"></div>
      <div class="cross-text">考试概览</div>
      <div class="right-cross-line"></div>
    </div>

    <div class="quick-info">
      <div class="info-name">考试名称：</div>
      <div class="info-content">{{examInfo.exam.name}}</div>
    </div>
    <div v-if="examInfo.exam.timeType === 2" class="quick-info">
      <div class="info-name">考试类型：</div>
      <div class="info-content">不限时</div>
    </div>
    <div v-if="examInfo.exam.timeType === 1" class="quick-info">
      <div class="info-name">考试时间：</div>
      <div class="info-content">{{formartDateTime(examInfo.exam.examTimes[0])}} - {{formartDateTime(examInfo.exam.examTimes[1])}}</div>
    </div>
    <div v-if="examInfo.exam.timeType === 1 && markQuestions.length" class="quick-info">
      <div class="info-name">阅卷时间：</div>
      <div class="info-content">{{formartDateTime(examInfo.exam.markTimes[0])}} - {{formartDateTime(examInfo.exam.markTimes[1])}}</div>
    </div>
    <div v-if="examInfo.exam.timeType === 1 && markQuestions.length" class="quick-info">
      <div class="info-name">及格分数：</div>
      <div class="info-content">{{examInfo.exam.passScore}} / {{totalScore}}</div>
    </div>
    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">防 作 弊：</div>
        <div class="info-content">
          {{examInfo.exam.sxes.length ? '' : '无'}}
          {{examInfo.exam.sxes.indexOf(1) !== -1 ? '试题乱序' : ''}} 
          {{examInfo.exam.sxes.indexOf(2) !== -1 ? '选项乱序' : ''}}
        </div>
      </div>
    </div>
    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">展示方式：</div>
        <div class="info-content">{{$tools.getDictValue('PAPER_SHOW_TYPE', examInfo.exam.showType)}}</div>
      </div>
    </div>
    <div class="quick-info">
      <div class="info-name">匿名阅卷：</div>
      <div class="info-content">{{examInfo.exam.anonState === 1 ? '是' : '否'}}</div>
    </div>
    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">成绩公开：</div>
        <div class="info-content">{{examInfo.exam.scoreState === 1 ? '是' : '否'}}</div>
      </div>
    </div>
    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">排名公开：</div>
        <div class="info-content">{{examInfo.exam.rankState === 1 ? '是' : '否'}}</div>
      </div>
    </div>
    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">用户人数：</div>
        <div class="info-content">考试{{examUserNum}}人，阅卷{{markUserNum}}人</div>
      </div>
    </div>
    <div class="quick-info">
      <div class="info-name">试题数量：</div>
      <div class="info-content">共{{questionNum}}道题，其中主观题{{markQuestions.length}}道</div>
    </div>
  </div>
</template>

<script>
import { examPublish } from 'api/exam'
import _ from 'lodash'
import * as dayjs from 'dayjs'
import { mapState } from 'vuex'
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      
    }
  },
  computed: {
    ...mapState('exam', [
      'examInfo',
    ]),
    ...mapGetters('exam',[
      'totalScore',
      'markType',
      'markQuestions',
      'examUserNum',
      'markUserNum',
      'questionNum',
    ])
  },
  methods: {
    async publish() {
      let examInfo = _.cloneDeep(this.examInfo)
      examInfo.exam.state = 1
      examInfo.exam.markType = this.markType
      examInfo.exam.totalScore = this.totalScore
      if (examInfo.exam.timeType === 1) {
        examInfo.exam.startTime = dayjs(examInfo.exam.examTimes[0]).format('YYYY-MM-DD HH:mm:ss')
        examInfo.exam.endTime = dayjs(examInfo.exam.examTimes[1]).format('YYYY-MM-DD HH:mm:ss')
        if (this.markType === 2) {
          examInfo.exam.markStartTime = dayjs(examInfo.exam.markTimes[0]).format('YYYY-MM-DD HH:mm:ss')
          examInfo.exam.markEndTime = dayjs(examInfo.exam.markTimes[1]).format('YYYY-MM-DD HH:mm:ss')
        }
      }

      examInfo.examQuestions.forEach(examQuestion => {
        if (examQuestion.type === 2 // 如果是填空或客观问答题
        && (examQuestion.question.type === 3 || (examQuestion.question.type === 5 && examQuestion.question.markType === 1))) {
          examQuestion.question.answers.forEach((answer, index) => {
            examQuestion.question.answers[index] = answer.join('\n')
          })
        }
      });

      const res = await examPublish(JSON.stringify(examInfo))
      if (res?.code === 200) {
        this.$message.success('发布成功！')
        this.$router.push('/')
      } else {
        this.$message.error(res.msg)
      }
    },
    formartDateTime(date) {
      if (!date) {
        return ''
      }
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-publish-box {
  padding: 15px 150px;
  .cross-line-box {
    display: flex;
    align-items: center;
    padding: 20px 0;
    .left-cross-line {
      width: 30px;
      border-bottom: 1px solid #D8D8D8;
    }
    .cross-text {
      padding: 0 10px;
      font-size: 18px;
      font-weight: bold;
      color: #0D2332
    }
    .right-cross-line {
      flex: 1;
      border-bottom: 1px solid #D8D8D8;
    }
  }
  .quick-info-more {
    justify-content: space-between;
    .info-more-2-width {
      width: calc(100% / 2);
      display: flex;
    }
    .info-more-4-width {
      width: calc(100% / 4);
      display: flex;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  .quick-info {
    width: 100%;
    display: flex;
    line-height: 30px;
    .info-name {
      color: #999;
    }
    .info-content {
      flex: 1;
      padding: 0 10px;
    }
  }
}
</style>
