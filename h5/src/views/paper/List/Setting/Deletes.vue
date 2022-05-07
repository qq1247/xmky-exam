<template>
  <el-button type="danger" @click="del">删除</el-button>
</template>

<script>
import { paperDel } from 'api/paper'

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
      if (!Number(this.id)) return false
      this.$confirm(`确认删除吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          const res = await paperDel({ id: this.id })
          if (res?.code === 200) {
            this.$message('删除成功！')
            this.$router.back()
          } else {
            this.$message(res.msg || '删除失败！', 'error')
          }
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
