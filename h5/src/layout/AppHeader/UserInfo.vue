<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 17:59:09
 * @LastEditors: Che
 * @LastEditTime: 2021-08-13 10:38:00
-->
<template>
  <div class="header-userInfo">
    <template v-if="token">
      <el-dropdown trigger="hover" @command="handleCommand">
        <span class="el-dropdown-link">
          欢迎您！{{ name }}<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="common common-edit" command="edit"
            >修改密码</el-dropdown-item
          >
          <el-dropdown-item icon="common common-login-out" command="out"
            >退出</el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
    </template>
    <router-link class="header-login" to="/login"
v-else
      ><i class="common common-login-out"></i>登录</router-link
    >
    <el-dialog
      width="40%"
      :visible.sync="editForm.show"
      title="修改密码"
      append-to-body
    >
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="旧密码" label-width="80px">
          <el-input
            placeholder="请输入旧密码"
            v-model="editForm.oldPwd"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" label-width="80px">
          <el-input
            placeholder="请输入新密码"
            v-model="editForm.newPwd"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="pwdUpdate" type="primary">确定</el-button>
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { loginPwdUpdate } from '@/api/common'
export default {
  name: 'UserInfo',
  data() {
    return {
      editForm: {
        show: false,
      },
    }
  },
  computed: {
    ...mapGetters(['token', 'name']),
  },
  methods: {
    handleCommand(command) {
      if (command === 'edit') {
        this.editForm.show = true
      }

      if (command === 'out') {
        this.$store.dispatch('user/resetToken').then(() => {
          this.$tools.message('登出成功！', 'info')
          this.$router.push('/')
        })
      }
    },
    async pwdUpdate() {
      const res = await loginPwdUpdate({
        oldPwd: this.editForm.oldPwd,
        newPwd: this.editForm.newPwd,
      })

      if (res.code != 200) {
        this.$tools.message(res.msg, 'warning')
        return
      }

      // this.$refs['editForm'].resetFields()
      this.editForm.oldPwd = null
      this.editForm.newPwd = null
      this.editForm.show = false
    },
  },
}
</script>

<style lang="scss">
.header-userInfo {
  display: flex;
  align-items: center;
  color: #fff;
  span {
    margin-right: 10px;
    cursor: pointer;
    &:first-child {
      margin-right: 20px;
      cursor: default;
    }
  }
  .common-login-out {
    margin-right: 10px;
  }
  .header-login {
    width: 100px;
    height: 35px;
    line-height: 35px;
    color: #fff;
    font-size: 14px;
    border-radius: 3px;
    text-align: center;
    text-decoration: none;
    background: #0095e5;
  }
}

.el-dropdown-link {
  color: #fff;
}
</style>
