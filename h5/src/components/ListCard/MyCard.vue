<!--
 * @Description: 业务卡片
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-13 14:52:40
 * @LastEditors: Che
 * @LastEditTime: 2022-01-12 18:16:22
-->
<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 考试，阅卷 标签 -->
      <div class="tag-group">
        <el-tag
          size="mini"
          v-if="name == 'myExamList'"
          :type="data.state == 3 ? '' : 'danger'"
          >{{ data.state | examStateName }}</el-tag
        >
        <el-tag
          size="mini"
          v-if="name == 'myExamMarkList'"
          :type="data.examMarkState == 3 ? '' : 'danger'"
          >{{ data.examMarkState | markStateName }}</el-tag
        >
      </div>
      <!-- 标题 -->
      <div class="title ellipsis">{{ data.name || data.examName }}</div>
      <el-row class="content-info">
        <el-col>创建者：{{ data.updateUserName }}</el-col>
      </el-row>
      <el-row class="content-info">
        <el-col>{{
          name === 'myExamList' ? data.examStartTime : data.markStartTime
        }}</el-col>
      </el-row>
      <el-row class="content-info">
        <el-col :span="8">总分：{{ data.paperTotalScore }}</el-col>
        <el-col :span="8"
          >及格：{{
            Math.ceil((data.paperPassScore / 100) * data.paperTotalScore) || 0
          }}</el-col
        >
        <el-col :span="8" v-if="name === 'myExamList'"
          >得分：{{ data.totalScore === null ? '-' : data.totalScore }}</el-col
        >
      </el-row>

      <CountDown
        class="count-down"
        v-if="remainTime > 0"
        :time="remainTime"
      ></CountDown>

      <div class="handler">
        <!-- 我的考试 -->
        <template v-if="name === 'myExamList'">
          <span :data-title="data.state | examStateName" @click="exam(data)">
            <i :class="['common', stateIcon[data.state]]"></i>
          </span>
        </template>
        <!-- 我的阅卷 -->
        <template v-if="name === 'myExamMarkList'">
          <span
            :data-title="data.examMarkState | markStateName"
            @click="mark(data)"
          >
            <i :class="['common', markStateIcon[data.examMarkState]]"></i>
          </span>
          <span data-title="考生列表" @click="markUser(data)">
            <i class="common common-user"></i>
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
    CountDown,
  },
  props: {
    data: {
      type: Object,
      default: () => {},
    },
    name: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      markStateIcon: ['', 'common-wait', 'common-marking', 'common-mark'],
      stateIcon: ['', 'common-wait', 'common-examing', 'common-exam'],
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
    },
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
    },
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
    },
  },
}
</script>
vue
<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
