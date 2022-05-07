<template>
  <div class="content-left">
    <div class="user-info">
      <el-avatar
        v-if="$store.getters.userAvatar"
        :size="64"
        :src="`/api/file/download?id=${Number($store.getters.userAvatar)}`"
      ><i
        class="common common-wo"
      /></el-avatar>
      <div class="user-name">
        {{ $store.getters.name || '***' }}&nbsp;/&nbsp;{{
          $store.getters.orgName || '***'
        }}
      </div>
    </div>
    <div class="exam-head">答题卡</div>
    <el-scrollbar wrap-style="overflow-x:hidden;">
      <div
        v-for="(item, index) in paperQuestion"
        :key="index"
        class="router-content"
      >
        <div v-if="item.questionList" class="router-title">
          第{{ $tools.intToChinese(index + 1) }}章（共{{
            item.questionList.length
          }}题，合计{{ computeChapterScore(item.questionList) }}分）
        </div>
        <div v-if="item.questionList" class="router-link">
          <a
            v-for="(question, indexQuestion) in item.questionList"
            :key="question.id"
            :class="[
              'router-index',
              question.submit ? 'router-submit' : '',
              question.sign ? 'router-sign' : '',
              routerIndex === question.id ? 'router-active' : '',
            ]"
            @click="toHref(question.id)"
            @dblclick="sign(question.id)"
          >{{ indexQuestion + 1 }}</a>
        </div>
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
    <div v-if="!preview" class="exam-footer">
      <div class="exam-btn">
        剩余：<CountDown :time="systemTime" @finish="forceExamEnd" />
      </div>
      <div class="exam-btn exam-finish" @click="examEnd">提交</div>
    </div>
  </div>
</template>

<script>
import CountDown from 'components/CountDown.vue'
export default {
  components: {
    CountDown
  },
  props: {
    preview: {
      type: Boolean,
      default: false
    },
    paperQuestion: {
      type: Array,
      default: () => []
    },
    systemTime: {
      type: Number,
      default: 0
    },
    routerIndex: {
      type: [String, Number],
      default: ''
    }
  },
  data() {
    return {
      userInfo: {}
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
    }
  }
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
