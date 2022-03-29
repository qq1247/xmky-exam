<template>
  <el-switch
    v-model="state"
    :active-value="2"
    :inactive-value="1"
    @change="anonymous"
  >
  </el-switch>
</template>

<script>
import { examAnon, examGet } from 'api/exam'

export default {
  data() {
    return {
      id: null,
      state: 2,
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    const res = await examGet({ id: this.id })
    this.state = res.data.anonState
  },
  methods: {
    async anonymous() {
      const res = await examAnon({ id: this.id, state: this.state })
      if (res?.code == 200) {
        this.$message.success('匿名设置成功！')
        this.$router.back()
      } else {
        this.$message.error(res.msg || '匿名设置失败！')
      }
    },
  },
}
</script>
