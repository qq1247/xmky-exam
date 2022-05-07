<template>
  <div class="form-footer">
    <el-button type="primary" @click="startTask">启动</el-button>
  </div>
</template>

<script>
import { cronStartTask } from 'api/base'

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
    // 启动任务
    async startTask() {
      const res = await cronStartTask({ id: this.id })
      if (res?.code !== 200) {
        this.$message.error(res.msg)
        return
      }

      this.$message.success('启动成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
