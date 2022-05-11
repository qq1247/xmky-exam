<template>
  <div class="container">
    <div class="result-content">
      <div v-if="countDown" ref="countDown">
        {{ countDown }}
      </div>
      <div v-else class="exam-result">
        本场考试得分：{{ scoreState ? score : '**' }}
        <div v-if="!scoreState" class="exam-tip">此次考试暂未公开成绩</div>
        <el-button
          type="primary"
          @click="$router.replace({ name: 'Home' })"
        >返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { myExamListPage } from '@/api/my'
export default {
  data() {
    return {
      score: 0,
      countDown: 5,
      examId: null,
      scoreState: true
    }
  },
  async mounted() {
    const { examId, scoreState } = this.$route.params
    this.examId = examId
    this.scoreState = JSON.parse(scoreState)
    this.getResult()
  },
  methods: {
    // 获取答案
    async getResult() {
      this.$refs.countDown.className = 'count-down animate'
      const scoreData = await this.$tools
        .delay()
        .then(() => {
          this.countDown--
          this.$refs.countDown.className = 'count-down'
          return myExamListPage({
            examId: this.examId
          })
        })
        .catch((err) => {
          return err.data
        })

      if (!this.countDown) {
        this.score = scoreData.data?.list[0]?.totalScore || '请稍后查询'
        return
      } else {
        this.getResult()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.result-content {
  height: 100%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  .count-down {
    font-size: 100px;
    color: rgb(18, 240, 118);
  }
  .animate {
    animation: countDown 1s ease-in-out;
  }
  .exam-result {
    font-size: 18px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .exam-tip {
    font-size: 14px;
    margin-top: 10px;
    color: #ff8e19;
  }
  .el-button {
    margin-top: 10px;
  }
}
@keyframes countDown {
  from {
    opacity: 1;
    transform: scale(3);
  }
  to {
    opacity: 0;
    transform: scale(1);
  }
}
</style>
