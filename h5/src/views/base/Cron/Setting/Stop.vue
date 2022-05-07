<template>
  <div class="form-footer">
    <el-button type="primary" @click="stopTask">停止</el-button>
  </div>
</template>

<script>
import { cronStopTask } from 'api/base'

export default {
  data() {
    return {
      id: null
    }
  },
  mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    // 停止任务
    async stopTask() {
      const res = await cronStopTask({ id: this.id })
      if (res?.code !== 200) {
        this.$message.error(res.msg)
        return
      }

      this.$message.success('停止成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
