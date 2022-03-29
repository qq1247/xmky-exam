<template>
  <el-switch
    v-model="state"
    :active-value="1"
    :inactive-value="2"
    @change="score"
  >
  </el-switch>
</template>

<script>
import { examScore, examGet } from 'api/exam'

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
    this.state = res.data.scoreState
  },
  methods: {
    async score() {
      const res = await examScore({ id: this.id, state: this.state })
      if (res?.code == 200) {
        this.$message.success('成绩公开设置成功！')
        this.$router.back()
      } else {
        this.$message.error(res.msg || '成绩公开设置失败！')
      }
    },
  },
}
</script>
