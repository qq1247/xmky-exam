<!--
 * @Description: 删除
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-21 10:11:03
 * @LastEditors: Che
 * @LastEditTime: 2022-01-14 17:11:54
-->
<template>
  <div class="container">
    <div class="form-footer">
      <el-button @click="initPwd" type="primary">重置</el-button>
    </div>
  </div>
</template>

<script>
import { userPwdInit } from 'api/user'
export default {
  data() {
    return {
      id: null,
    }
  },
  mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    // 重置密码
    async initPwd() {
      const res = await userPwdInit({
        id: this.id,
      })

      if (res.code != 200) {
        this.$message.error(res.msg)
        return
      }

      if (res.data.initPwd) {
        this.$alert('当前密码：' + res.data.initPwd, '提示', {
          confirmButtonText: '确定',
        }).then((res) => {
          this.$router.back()
        })
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
