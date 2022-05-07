<template>
  <div class="form-footer">
    <el-button type="danger" @click="del">删除</el-button>
  </div>
</template>

<script>
import { userDel } from 'api/user'
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
    // 删除
    del() {
      if (!this.id) return false
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        const res = await userDel({ id: this.id })
        if (res.code !== 200) {
          this.$message.error(res.msg)
        }
        this.$router.back()
        this.$message.success('删除成功！')
        this.$router.back()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
