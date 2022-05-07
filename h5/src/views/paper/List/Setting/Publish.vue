<template>
  <el-button type="primary" @click="publish"> 发布 </el-button>
</template>

<script>
import { paperPublish, paperGet } from 'api/paper'
export default {
  data() {
    return {
      id: null,
      state: 1
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await paperGet({ id: this.id })
      this.$nextTick(() => {
        this.state = res.data.state
      })
    }
  },
  methods: {
    publish() {
      if (this.state === 1) {
        this.$message.warning('试卷已发布!')
        return
      }
      this.$confirm(`确认发布试卷吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          const res = await paperPublish({ id: this.id })
          if (res?.code === 200) {
            this.$message.success('考试发布成功！')
            this.$router.back()
          } else {
            this.$message.error('请重新发布考试！')
          }
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
