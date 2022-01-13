<!--
 * @Description: 复制
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:07:54
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 09:47:03
-->
<template>
  <div class="container">
    <el-alert
      show-icon
      type="success"
      effect="dark"
      title="将复制相同数据的一张试卷"
    ></el-alert>
    <div class="form-footer">
      <el-button @click="copy" type="primary"> 复制 </el-button>
    </div>
  </div>
</template>

<script>
import { paperCopy } from 'api/paper'

export default {
  data() {
    return {
      id: null,
    }
  },
  async mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    async copy() {
      const res = await paperCopy({
        id: this.id,
      })
      res?.code == 200
        ? (this.$message.success('复制成功！'), this.$router.back())
        : this.$message.error('复制失败！')
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
