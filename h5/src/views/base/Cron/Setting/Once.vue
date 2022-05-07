<template>
  <div class="form-footer">
    <el-button type="primary" @click="onceTask">运行</el-button>
  </div>
</template>

<script>
import { cronrunOnceTask } from 'api/base'

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
    // 运行一次
    async onceTask() {
      const res = await cronrunOnceTask({ id: this.id })
      if (res?.code !== 200) {
        this.$message.error(res.msg)
        return
      }

      this.$message.success('运行成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
