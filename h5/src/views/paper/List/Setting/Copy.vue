<template>
  <el-button type="primary" @click="copy"> 复制 </el-button>
</template>

<script>
import { paperCopy } from 'api/paper'

export default {
  data() {
    return {
      id: null
    }
  },
  async mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    async copy() {
      const res = await paperCopy({
        id: this.id
      })
      res?.code === 200
        ? (this.$message.success('复制成功！'), this.$router.back())
        : this.$message.error('复制失败！')
    }
  }
}
</script>
