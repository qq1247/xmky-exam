<template>
  <el-row class="container">
    <template v-if="examUserList.length > 0">
      <el-col v-for="item in examUserList" :key="item.userId" :span="6">
        <div :class="['line-user', item.online ? 'line' : '']">
          <i class="common common-onLine" />
          <span class="line-name">{{ item.userName }}</span>
          <span class="line-time">{{ item.onlineTime }}</span>
        </div>
      </el-col>
    </template>
    <el-empty v-else description="暂无在线用户" />
  </el-row>
</template>

<script>
import { onlineUser, examGet } from 'api/exam'

export default {
  data() {
    return {
      id: null,
      timeLine: null,
      examUserList: []
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const {
        data: { state }
      } = await examGet({ id: this.id })
      this.onLine({ id: this.id, state })
    }
  },
  beforeDestroy() {
    clearInterval(this.timeLine)
    this.timeLine = null
  },
  methods: {
    async onLine({ id, state }) {
      if (state === 2) {
        this.$message.error('请先发布考试！')
        return
      }

      const resList = await onlineUser({ id })
      resList?.code === 200 && (this.examUserList = resList.data.list)
      this.timeLine = setInterval(async() => {
        const timeUserList = await onlineUser({ id })
        resList?.code === 200 && (this.examUserList = timeUserList.data.list)
      }, 30 * 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  width: 1200px;
}
.line-user {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;
  width: 96%;
  border: 1px solid #ccc;
  border-radius: 10px;
  margin-bottom: 10px;
  &:hover {
    border: 1px solid #1e9fff;
  }
  .common-onLine {
    font-size: 70px;
  }
  .line-name {
    font-size: 16px;
  }

  .line-time {
    font-size: 13px;
    line-height: 40px;
  }
}
.line {
  color: #1e9fff;
  border: 1px solid #1e9fff;
}
</style>
