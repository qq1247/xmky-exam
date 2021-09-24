<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-16 13:27:05
 * @LastEditors: Che
 * @LastEditTime: 2021-09-16 17:44:22
-->

<template>
  <el-collapse class="exam-card" v-model="collapseShow">
    <template v-if="preview == 'false'">
      <div class="exam-head">答题卡</div>
      <div class="exam-time">
        倒计时：<CountDown
          :time="systemTime"
          @finish="forceExamEnd"
        ></CountDown>
      </div>
    </template>
    <template v-if="showType === '1'">
      <el-collapse-item
        :key="item.id"
        :name="index"
        :title="item.chapter.name"
        v-for="(item, index) in paperQuestion"
        v-model="questionRouter"
      >
        <a
          :class="[
            'router-index',
            child.submit ? 'router-submit' : '',
            child.sign ? 'router-sign' : '',
            hrefPointer === `#p-${child.id}` ? 'router-active' : '',
          ]"
          @click="toHref(child.id)"
          @dblclick="sign(child.id)"
          v-for="(child, index) in item.questionList"
          :key="child.id"
          >{{ index + 1 }}</a
        >
      </el-collapse-item>
    </template>
    <template v-if="showType === '3'">
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
    </template>

    <div v-if="preview == 'false'" class="exam-footer" @click="examEnd">
      提交
    </div>
  </el-collapse>
</template>

<script>
import CountDown from 'components/CountDown.vue'

export default {
  props: {
    preview: {
      type: [String, Boolean],
      default: '',
    },
    paperQuestion: {
      type: Array,
      default: () => [],
    },
    myExamDetailCache: {
      type: Object,
      default: () => {},
    },
    showType: {
      type: [Number, String],
      default: 1,
    },
    systemTime: {
      type: Number,
      default: 0,
    },
    questionRouter: {
      type: Array,
      default: () => [],
    },
    hrefPointer: {
      type: [String, Number],
      default: '',
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
      collapseShow: 0,
    }
  },
  methods: {
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
  border-top: 1px solid #e8e8e8;
  padding: 10px;
  font-size: 14px;
}
</style>
