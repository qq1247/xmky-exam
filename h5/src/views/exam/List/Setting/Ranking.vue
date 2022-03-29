<template>
  <el-switch
    v-model="state"
    :active-value="1"
    :inactive-value="2"
    @change="ranking"
  >
  </el-switch>
</template>

<script>
import { examRank, examGet } from 'api/exam'

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
    this.state = res.data.rankState
  },
  methods: {
    async ranking() {
      const res = await examRank({ id: this.id, state: this.state })
      if (res?.code == 200) {
        this.$message.success('排名公开设置成功！')
        this.$router.back()
      } else {
        this.$message.error(res.msg || '排名公开设置失败！')
      }
    },
  },
}
</script>
