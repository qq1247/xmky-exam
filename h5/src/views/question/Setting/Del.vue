<template>
  <el-button type="danger" @click="del">确定</el-button>
</template>

<script>
import { questionTypeDel } from 'api/question'

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
    del() {
      questionTypeDel({ id: this.id }).then(() => {
        this.$message.success('删除成功')
        this.$tools.switchTab('QuestionIndex')
      })
    }
  }
}
</script>
