<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2022-01-10 13:19:31
 * @LastEditors: Che
 * @LastEditTime: 2022-01-10 13:39:53
-->
<template>
  <div class="container">
    <div class="form-footer">
      <el-button @click="onceTask" type="primary">运行</el-button>
    </div>
  </div>
</template>

<script>
import { cronrunOnceTask } from 'api/base'

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
    // 运行一次
    async onceTask() {
      const res = await cronrunOnceTask({ id: this.id })
      if (res?.code != 200) {
        this.$message.error(res.msg)
        return
      }

      this.$message.success('运行成功')
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
