<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2022-01-10 13:18:52
 * @LastEditors: Che
 * @LastEditTime: 2022-01-10 13:39:30
-->
<template>
  <div class="container">
    <div class="form-footer">
      <el-button @click="startTask" type="primary">启动</el-button>
    </div>
  </div>
</template>

<script>
import { cronStartTask } from 'api/base'

export default {
  data() {
    return {
      id: null,
    }
  },
  mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    // 启动任务
    async startTask() {
      const res = await cronStartTask({ id: this.id })
      if (res?.code != 200) {
        this.$message.error(res.msg)
        return
      }

      this.$message.success('启动成功')
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
