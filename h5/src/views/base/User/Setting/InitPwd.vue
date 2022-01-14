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
    <el-alert
      show-icon
      type="success"
      effect="dark"
      title="此操作将恢复到初始密码"
      description="恢复的初始密码由系统设置中确定"
    ></el-alert>
    <div class="form-footer">
      <el-button @click="initPwd" type="success">初始化密码</el-button>
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
    // 初始化密码
    async initPwd() {
      const res = await userPwdInit({
        id: this.id,
      })

      if (res.code != 200) {
        this.$message.error(res.msg)
        return
      }

      if (res.data.initPwd) {
        this.$alert(res.data.initPwd, '初始化密码', {
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
