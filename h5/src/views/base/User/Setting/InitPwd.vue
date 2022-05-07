<template>
  <div class="form-footer">
    <el-button type="primary" @click="initPwd">重置</el-button>
  </div>
</template>

<script>
import { userPwdInit } from 'api/user'
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
    // 重置密码
    async initPwd() {
      const res = await userPwdInit({
        id: this.id
      })

      if (res.code !== 200) {
        this.$message.error(res.msg)
        return
      }

      if (res.data.initPwd) {
        this.$alert('当前密码：' + res.data.initPwd, '提示', {
          confirmButtonText: '确定'
        }).then((res) => {
          this.$router.back()
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
