<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2022-01-10 13:19:14
 * @LastEditors: Che
 * @LastEditTime: 2022-01-10 13:39:47
-->
<template>
  <div class="container">
    <div class="form-footer">
      <el-button @click="stopTask" type="primary">停止</el-button>
    </div>
  </div>
</template>

<script>
import { cronStopTask } from 'api/base'

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
    // 停止任务
    async stopTask() {
      const res = await cronStopTask({ id: this.id })
      if (res?.code != 200) {
        this.$message.error(res.msg)
        return
      }

      this.$message.success('停止成功')
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
