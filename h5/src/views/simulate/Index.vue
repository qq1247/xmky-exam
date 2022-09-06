<template>
  <div class="container">
    <div v-if="questionTypeOpenList.length" class="info-list">
      <div
        v-for="item in questionTypeOpenList"
        :key="item.id"
        class="info-item"
        @click="goTest(item)"
      >
        <div class="item-left">{{ item.questionTypeName }}</div>
        <div class="item-right">{{ item.startTime }} - {{ item.endTime }}</div>
      </div>
    </div>
    <el-empty v-else description="暂无模拟试卷" />
  </div>
</template>

<script>
import { questionTypeOpenListpage } from 'api/question'
export default {
  data() {
    return {
      questionTypeOpenList: []
    }
  },
  mounted() {
    this.getQuestionTypeOpenList()
  },
  methods: {
    // 获取开放题库
    async getQuestionTypeOpenList() {
      const res = await questionTypeOpenListpage({
        state: 1,
        pageSize: 10,
        curPage: 1
      })
      res?.code === 200 && (this.questionTypeOpenList = res.data.list)
    },
    // 查看试题和评论
    goTest({ questionTypeId, startTime, endTime, commentState }) {
      const $_startTime = new Date(startTime).getTime()
      const $_endTime = new Date(endTime).getTime()
      const now = new Date().getTime()
      if (now < $_startTime || now > $_endTime) {
        this.$message.warning('不在开放时间段，请重新确认时间点！')
        return
      }
      this.$router.push({
        name: 'SimulateTest',
        params: {
          questionTypeId,
          commentState
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  width: 1200px;
  margin: 0 auto 20px;
  padding: 10px 20px;
  background: #fff;
  border-radius: 5px;
  border: 1px solid #ececec;
}
.info-list {
  display: flex;
  flex-direction: column;
  .info-item {
    line-height: 50px;
    font-size: 13px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e6ebf5;
    padding: 0 5px;
    cursor: pointer;
    &:hover {
      color: #0094e5;
    }
    &:last-child {
      border-bottom: none;
    }
  }
}
</style>
