<template>
  <el-button type="danger" @click="clear">确定</el-button>
</template>

<script>
import { questionTypeClear } from 'api/question'

export default {
  data() {
    return {
      id: null,
    }
  },
  mounted() {
    this.id = this.$route.params.questionTypeId
  },
  methods: {
    clear() {
      questionTypeClear({ id: this.id }).then(() => {
        this.$message.success('清空成功')
      })
    },
  },
}
</script>
