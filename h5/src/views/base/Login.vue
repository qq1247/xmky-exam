<template>
  <div class="container">
    <el-form
      :model="ruleForm"
      status-icon
      :rules="rules"
      ref="ruleForm"
      class="login-wrap"
    >
      <div class="login-title">欢迎登录</div>
      <el-form-item prop="account">
        <template slot="label">
          <i class="common common-wo"></i>
        </template>
        <el-input
          type="text"
          placeholder="请输入账号"
          v-model="ruleForm.account"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <template slot="label">
          <i class="common common-lock"></i>
        </template>
        <el-input
          type="password"
          placeholder="请输入密码"
          v-model="ruleForm.password"
          autocomplete="off"
          @change="login('ruleForm')"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" class="login-btn" @click="login('ruleForm')"
          >登录</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapMutations } from "vuex"
export default {
  data() {
    return {
      ruleForm: {
        account: "",
        password: ""
      },
      rules: {
        account: [{ required: true, message: "账号不能为空", trigger: "blur" }],
        password: [{ required: true, message: "密码不能为空", trigger: "blur" }]
      }
    }
  },
  methods: {
    ...mapMutations(["setToken"]),
    // 登录
    login(formName) {
      this.$refs[formName].validate(async valid => {
        if (valid) {
          const res = await this.$https.login({
            loginName: this.ruleForm.account,
            pwd: this.ruleForm.password
          })
          res.data?.accessToken && this.setToken(res.data?.accessToken)
          !this.$route.query.redirect
            ? this.$router.push({
                path: "/"
              })
            : this.$router.push({
                path: this.$route.query.redirect
              })
        } else {
          this.$message({
            message: "请核对登录信息",
            duration: 2000,
            type: "warning"
          })
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  background: url(../../assets/img/login_bgs.jpg) no-repeat;
  background-size: cover;
  justify-content: center;
  align-items: center;
}
.login-wrap {
  position: relative;
  width: 35%;
  height: auto;
  padding: 60px;
  margin: 0 auto;
  box-sizing: border-box;
  background: #fff;
  border-radius: 3px;
  box-shadow: 0 0 16px 3px rgba(0, 0, 0, 0.15);
  .login-title {
    color: #4a5768;
    font-size: 20px;
    font-weight: bold;
    margin: 0 0 30px;
  }
  .login-btn {
    width: 100%;
    margin-top: 30px;
    height: 45px;
  }
}
/deep/ .el-form-item {
  position: relative;
}
/deep/ .el-form-item__label {
  position: absolute;
  left: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  align-items: center;
  i {
    font-size: 20px;
  }
}

/deep/
  .el-form-item.is-required:not(.is-no-asterisk)
  > .el-form-item__label:before {
  color: transparent;
}

/deep/ .el-input__suffix {
  color: #1e9fff;
}

/deep/ .el-input__inner {
  padding: 0 15px 0 40px;
}
</style>
