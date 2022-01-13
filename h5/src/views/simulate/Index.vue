<!--
 * @Description: 模拟列表
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-22 16:57:28
 * @LastEditors: Che
 * @LastEditTime: 2021-12-22 17:21:28
-->
<template>
  <div class="container">
    <div class="info-list" v-if="questionTypeOpenList.length">
      <div
        class="info-item"
        :key="item.id"
        v-for="item in questionTypeOpenList"
        @click="goDetail(item)"
      >
        <div class="item-left">{{ item.questionTypeName }}</div>
        <div class="item-right">{{ item.startTime }} - {{ item.endTime }}</div>
      </div>
    </div>
    <el-empty v-else description="暂无模拟试卷"></el-empty>
  </div>
</template>

<script>
import { questionTypeOpenListPage } from 'api/question'
export default {
  data() {
    return {
      questionTypeOpenList: [],
    }
  },
  mounted() {
    this.getQuestionTypeOpenList()
  },
  methods: {
    // 获取开放题库
    async getQuestionTypeOpenList() {
      const res = await questionTypeOpenListPage({
        state: 1,
        pageSize: 10,
        curPage: 1,
      })
      res?.code === 200 && (this.questionTypeOpenList = res.data.list)
    },
    // 查看试题和评论
    goDetail({ questionTypeId, startTime, endTime, commentState }) {
      const $_startTime = new Date(startTime).getTime()
      const $_endTime = new Date(endTime).getTime()
      const now = new Date().getTime()
      if (now < $_startTime || now > $_endTime) {
        this.$message.warning('不在开放时间段，请重新确认时间点！')
        return
      }
      this.$router.push({
        path: '/simulate/comment',
        query: {
          id: questionTypeId,
          commentState,
        },
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  width: 1200px;
  margin: 0 auto 20px;
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
      color: #0095e5;
    }
  }
}
</style>
