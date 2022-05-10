<template>
  <div class="container">
    <div v-if="countDown" ref="countDown">
      {{ countDown }}
    </div>
    <div v-else class="exam-result">
      本场考试得分：<span v-if="scoreState">{{ score }}</span>
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
      scoreState: true,
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
            examId: this.examId,
          })
        })
        .catch((err) => {
          return err.data
        })

      if (!this.countDown) {
        this.score = scoreData.data?.list[0]?.totalScore || '--'
        return
      } else {
        this.getResult()
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  background: #fff;
  align-items: center;
  justify-content: center;
  .count-down {
    font-size: 100px;
    color: rgb(18, 240, 118);
  }
  .animate {
    animation: countDown 1s ease-in;
  }
  .exam-result {
    font-size: 18px;
  }
}
@keyframes countDown {
  from {
    opacity: 1;
    transform: scale(2);
  }
  to {
    opacity: 0;
    transform: scale(1);
  }
}
</style>
