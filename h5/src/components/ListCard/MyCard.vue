<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 考试，阅卷 标签 -->
      <div class="tag-group">
        <el-tag
          v-if="name === 'myExamList'"
          size="mini"
          :type="data.state === 3 ? '' : 'danger'"
        >{{ data.state | examStateName }}</el-tag>
        <el-tag
          v-if="name === 'myMarkList'"
          size="mini"
          :type="data.examMarkState === 3 ? '' : 'danger'"
        >{{ data.examMarkState | markStateName }}</el-tag>
      </div>
      <!-- 标题 -->
      <div class="title ellipsis">{{ data.name || data.examName }}</div>
      <el-row>
        <el-col
          class="content-info"
        >考试开始：{{ data.examStartTime }}（{{
          $tools.computeMinute(data.examStartTime, data.examEndTime)
        }}）</el-col>
        <el-col
          v-if="name === 'myMarkList'"
          class="content-info"
        >阅卷开始：{{ data.examMarkStartTime }}（{{
          $tools.computeMinute(data.examMarkStartTime, data.examMarkEndTime)
        }}）</el-col>
        <el-col>
          <el-row v-if="name === 'myExamList'" class="content-info">
            <el-col
              :span="8"
            >答题：{{
              $tools.computeMinute(data.answerStartTime, data.answerEndTime)
            }}</el-col>
            <el-col
              :span="8"
              :style="{
                color:
                  data.totalScore !== null &&
                  (
                    (data.paperPassScore / 100) *
                    data.paperTotalScore
                  ).toFixed() > data.totalScore
                    ? 'red'
                    : '',
              }"
            >分数：{{ data.totalScore !== null ? data.totalScore : '-' }} /
              {{ data.paperTotalScore }}</el-col>
            <el-col
              v-if="name === 'myExamList'"
              :span="8"
            >排名：{{ data.no === null ? '-' : data.no }} /
              {{ data.userNum === null ? '-' : data.userNum }}</el-col>
          </el-row>
        </el-col>
        <template v-if="name === 'myMarkList'">
          <el-col
            class="content-info"
          >分数：{{
            ((data.paperPassScore / 100) * data.paperTotalScore).toFixed()
          }}
            / {{ data.paperTotalScore }}</el-col>
        </template>
      </el-row>
      <CountDown v-if="remainTime > 0" class="count-down" :time="remainTime" />

      <div class="handler">
        <!-- 我的考试 -->
        <template v-if="name === 'myExamList'">
          <span :data-title="data.state | examStateName" @click="exam(data)">
            <i :class="['common', stateIcon[data.state]]" />
          </span>
        </template>
        <!-- 我的阅卷 -->
        <template v-if="name === 'myMarkList'">
          <span
            :data-title="data.examMarkState | markStateName"
            @click="mark(data)"
          >
            <i :class="['common', markStateIcon[data.examMarkState]]" />
          </span>
          <span data-title="考生列表" @click="markUser(data)">
            <i class="common common-user" />
          </span>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import CountDown from '../CountDown.vue'
import { getOneDict } from '@/utils/getDict'
export default {
  components: {
    CountDown
  },
  filters: {
    examStateName(data) {
      return getOneDict('MY_EXAM_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    markStateName(data) {
      return getOneDict('MY_EXAM_MARK_STATE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    }
  },
  props: {
    data: {
      type: Object,
      default: () => {}
    },
    name: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      markStateIcon: ['', 'common-wait', 'common-marking', 'common-mark'],
      stateIcon: ['', 'common-wait', 'common-examing', 'common-exam']
    }
  },
  computed: {
    remainTime() {
      const startTime =
        this.name === 'myExamList'
          ? this.data.examStartTime
          : this.data.markStartTime
      const diffTime = new Date(startTime).getTime() - new Date().getTime()
      return diffTime
    }
  },
  methods: {
    // 开始考试
    exam(data) {
      this.$emit('exam', data)
    },
    // 开始考试
    mark(data) {
      this.$emit('mark', data)
    },
    // 考生列表
    markUser(data) {
      this.$emit('markUser', data)
    }
  }
}
</script>
vue
<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
