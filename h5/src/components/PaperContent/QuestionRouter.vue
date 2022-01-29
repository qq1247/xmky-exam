<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-16 13:27:05
 * @LastEditors: Che
 * @LastEditTime: 2022-01-18 16:20:53
-->
<template>
  <el-scrollbar wrap-style="overflow-x:hidden;" class="content-left">
    <div class="user-info">
      <el-avatar :size="64" v-if="$store.getters.name">{{
        ($store.getters.name && $store.getters.name.slice(0, 1)) || '头像'
      }}</el-avatar>
      <div class="user-name">
        {{ $store.getters.name || '***' }}&nbsp;/&nbsp;{{
          $store.getters.orgName || '***'
        }}
      </div>
    </div>
    <!-- <div class="user-info">
      <el-avatar :size="80" v-if="userInfo">{{
        (userInfo.userName && userInfo.userName.slice(0, 1)) || '头像'
      }}</el-avatar>
      <div class="user-name">
        {{ userInfo.userName || '***' }}
      </div>
    </div>
    <div class="user-intro">
      <div class="intro-item">
        <div class="item-title"><span>得分</span></div>
        <div class="item-num">
          {{ userInfo.totalScore === null ? '--' : userInfo.totalScore }}
        </div>
      </div>
      <div class="intro-item">
        <div class="item-title"><span>答题用时</span></div>
        <div class="item-num">
          {{ computeMinute(userInfo.answerStartTime, userInfo.answerEndTime) }}
        </div>
      </div>
      <div class="intro-item">
        <div class="item-title"><span>成绩</span></div>
        <div class="item-num">
          {{
            (
              (userInfo.totalScore / userInfo.paperTotalScore) *
              100
            ).toFixed() >= userInfo.paperPassScore
              ? '通过'
              : '未通过'
          }}
        </div>
      </div>
      <div class="intro-item">
        <div class="item-title"><span>阅卷用时</span></div>
        <div class="item-num">
          {{ computeMinute(userInfo.markStartTime, userInfo.markEndTime) }}
        </div>
      </div>
    </div> -->
    <div class="exam-head">
      <span>答题卡</span>
    </div>
    <div
      class="router-content"
      v-for="(item, index) in paperQuestion"
      :key="index"
      :style="{
        marginBottom: index + 1 === paperQuestion.length ? '50px' : '0',
      }"
    >
      <div class="router-title" v-if="item.questionList">
        第{{ $tools.intToChinese(index + 1) }}章（共{{
          item.questionList.length
        }}题，合计{{ computeChapterScore(item.questionList) }}分）
      </div>
      <div class="router-link" v-if="item.questionList">
        <a
          :class="[
            'router-index',
            child.submit ? 'router-submit' : '',
            child.sign ? 'router-sign' : '',
            routerIndex === child.id ? 'router-active' : '',
          ]"
          @click="toHref(child.id)"
          @dblclick="sign(child.id)"
          v-for="(child, index) in item.questionList"
          :key="child.id"
          >{{ index + 1 }}</a
        >
      </div>
    </div>
    <div class="exam-footer" v-if="!preview">
      <div class="exam-time">
        剩余：<CountDown :time="systemTime" @finish="forceExamEnd"></CountDown>
      </div>
      <div class="exam-finish" @click="examEnd">提交</div>
    </div>
    <!-- <template v-if="Number(showType) === 3">
        <div class="question-router">
          <a
            :class="[
              'router-index',
              item.submit ? 'router-submit' : '',
              item.sign ? 'router-sign' : '',
              Number(routerIndex) === index ? 'router-active' : '',
            ]"
            @click="toHref(index)"
            @dblclick="sign(index)"
            v-for="(item, index) in paperQuestion"
            :key="item.id"
            >{{ index + 1 }}</a
          >
        </div>
      </template> -->
  </el-scrollbar>
</template>

<script>
import CountDown from 'components/CountDown.vue'
export default {
  props: {
    preview: {
      type: Boolean,
      default: false,
    },
    paperQuestion: {
      type: Array,
      default: () => [],
    },
    systemTime: {
      type: Number,
      default: 0,
    },
    routerIndex: {
      type: [String, Number],
      default: '',
    },
  },
  components: {
    CountDown,
  },
  data() {
    return {
      userInfo: {},
    }
  },
  methods: {
    // 计算分数
    computeChapterScore(data) {
      const num = data.reduce((acc, cur) => {
        return acc + cur.score
      }, 0)
      return num
    },
    // 计算分钟数
    computeMinute(startTime, endTime) {
      if (!startTime || !endTime) {
        return '--'
      }
      const diffTime =
        new Date(endTime).getTime() - new Date(startTime).getTime()
      const minutes = diffTime / (60 * 1000)
      return `${minutes.toFixed(2)}分钟`
    },
    forceExamEnd() {
      this.$emit('forceExamEnd')
    },
    examEnd() {
      this.$emit('examEnd')
    },
    toHref(index) {
      this.$emit('toHref', index)
    },
    sign(index) {
      this.$emit('sign', index)
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
.question-router {
  border-top: 10px solid #f7f8f9;
  font-size: 14px;
  position: relative;
}
</style>
