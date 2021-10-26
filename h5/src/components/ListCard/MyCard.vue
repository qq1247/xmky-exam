<!--
 * @Description: 业务卡片
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-13 14:52:40
 * @LastEditors: Che
 * @LastEditTime: 2021-10-22 10:29:44
-->
<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- 考试，阅卷 标签 -->
      <div class="tagGroup">
        <el-tag size="mini" :type="data.state == 3 ? '' : 'danger'">{{
          data.stateName
        }}</el-tag
        >&nbsp;&nbsp;
        <el-tag size="mini" :type="data.markState == 3 ? '' : 'danger'">{{
          data.markStateName
        }}</el-tag
        >&nbsp;&nbsp;
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
      <el-row class="content-info" v-if="name === 'myExamList'">
        <el-col
          >及格：{{ data.totalScore || 0 }}/{{ data.paperTotalScore }}</el-col
        >
      </el-row>
      <el-row class="content-info" v-if="name === 'myMarkExamList'">
        <el-col
          >及格：{{
            (data.paperPassScore * data.paperTotalScore) / 100 || 0
          }}/{{ data.paperTotalScore }}</el-col
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
          <span
            v-if="data.exam === 'unStart' || data.exam === 'end'"
            :data-title="data.exam == 'unStart' ? '待考试' : '已考试'"
            @click="exam(data)"
          >
            <i
              :class="[
                'common',
                data.exam === 'unStart' ? 'common-wait' : 'common-exam',
              ]"
            ></i>
          </span>
          <span
            v-if="data.exam === 'start'"
            data-title="考试中"
            @click="exam(data)"
          >
            <i class="common common-examing"></i>
          </span>
        </template>
        <!-- 我的阅卷 -->
        <template v-if="name === 'myMarkExamList'">
          <span
            v-if="['unStart', 'end'].includes(data.mark)"
            :data-title="data.mark == 'unStart' ? '待阅卷' : '已阅卷'"
            @click="mark(data)"
          >
            <i
              :class="[
                'common',
                data.mark === 'unStart' ? 'common-wait' : 'common-mark',
              ]"
            ></i>
          </span>
          <span
            v-if="data.mark === 'start'"
            data-title="阅卷中"
            @click="mark(data)"
          >
            <i class="common common-marking"></i>
          </span>
          <!-- 智能阅卷 -->
          <el-progress
            type="circle"
            :percentage="percentage"
            :width="35"
            :stroke-width="2"
            data-title="智能阅卷中"
            :color="colors"
            v-if="
              data.mark === 'start' &&
              percentage > 0 &&
              markId !== null &&
              markId === data.examId
            "
          ></el-progress>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import CountDown from '../CountDown.vue'
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
    percentage: {
      type: Number,
      default: 0,
    },
    markId: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      colors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 },
      ],
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
  methods: {
    // 开始考试
    exam(data) {
      this.$emit('exam', data)
    },
    // 开始考试
    mark(data) {
      this.$emit('mark', data)
    },
  },
}
</script>
vue
<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
