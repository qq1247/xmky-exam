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
  <el-button @click="copy" type="primary"> 复制 </el-button>
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
