<!--
 * @Description: 业务卡片
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-13 14:52:40
 * @LastEditors: Che
 * @LastEditTime: 2021-12-03 16:05:40
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
          >{{ data.stateName }}</el-tag
        >
        <el-tag
          size="mini"
          v-if="name == 'myExamMarkList'"
          :type="data.examMarkState == 3 ? '' : 'danger'"
          >{{ data.examMarkStateName }}</el-tag
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
      <el-row class="content-info" v-if="name === 'myExamList'">
        <el-col
          >及格：{{ data.totalScore || 0 }}/{{ data.paperTotalScore }}</el-col
        >
      </el-row>
      <el-row class="content-info" v-if="name === 'myExamMarkList'">
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
          <span :data-title="data.stateName" @click="exam(data)">
            <i :class="['common', stateIcon[data.state]]"></i>
          </span>
        </template>
        <!-- 我的阅卷 -->
        <template v-if="name === 'myExamMarkList'">
          <span :data-title="data.examMarkStateName" @click="mark(data)">
            <i :class="['common', markStateIcon[data.examMarkState]]"></i>
          </span>
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
