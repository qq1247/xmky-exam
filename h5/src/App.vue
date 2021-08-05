<template>
  <div id="app">
    <header class="header" v-if="show">
      <div class="header-content">
        <div class="header-left">
          <router-link
            :class="[nav.img ? 'header-logo' : 'header-link']"
            :key="nav.title"
            :to="nav.to"
            exact
            v-for="nav in navList"
          >
            <i :class="nav.icon"></i>{{ nav.content }}
            <el-image :src="nav.img" class="logo-img" v-if="nav.img"></el-image>
          </router-link>
        </div>
        <div class="header-userInfo">
          <template v-if="userInfo">
            欢迎您！
            <span class="user-name">{{ JSON.parse(userInfo).userName }}</span>
            <span @click="editForm.show = true">修改密码</span>
            <span @click="loginOut">退出</span>
          </template>
          <router-link class="header-login" to="/login" v-else
            ><i class="common common-login-out"></i>登录</router-link
          >
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer" v-if="show">
      Copyright© 2018 All Rights Reserved 版权所有 在线考试
    </footer>
    <el-dialog :visible.sync="editForm.show" title="修改密码">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="旧密码" label-width="120px">
          <el-input
            placeholder="请输入旧密码"
            v-model="editForm.oldPwd"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" label-width="120px">
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
import { mapState, mapActions } from 'vuex'
export default {
  data() {
    return {
      editForm: {
        show: false,
        oldPwd: null,
        newPwd: null,
      },
      show: true,
      menuList: [
        'PaperAdd',
        'ExamPaperEdit',
        'LibraryAdd',
        'ExamQuestionEdit',
        'MyExam',
        'MyMarkExam',
      ],
      navList: [
        {
          to: '/',
          img: require('../src/assets/logo.png'),
        },
        {
          content: '首页',
          to: '/',
          icon: 'common common-index',
        },
        {
          content: '试题管理',
          to: '/examQuestion',
          icon: 'common common-index',
        },
        {
          content: '试卷管理',
          to: '/examPaper',
          icon: 'common common-manage',
        },
        {
          content: '考试管理',
          to: '/examSetting',
          icon: 'common common-index',
        },
        {
          content: '我的',
          to: '/my',
          icon: 'common common-mine',
        },
        {
          content: '用户管理',
          to: '/user',
        },
        {
          content: '基础管理',
          to: '/base',
        },
      ],
    }
  },
  watch: {
    $route: {
      handler(to) {
        if (to.name && this.menuList.includes(to.name)) {
          this.show = false
          return
        }
        this.show = true
      },
      deep: true,
      immediate: true,
    },
  },
  computed: mapState(['userInfo']),
  methods: {
    ...mapActions(['delUserInfo']),
    loginOut() {
      this.delUserInfo()
      this.$router.push('/')
    },
    async pwdUpdate() {
      const res = await this.$https.loginPwdUpdate({
        oldPwd: this.editForm.oldPwd,
        newPwd: this.editForm.newPwd,
      })

      if (res.code != 200) {
        alert(res.msg)
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
@import url(//at.alicdn.com/t/font_840312_ujhku1cp2xd.css);
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: 100%;
  background: #f4f5f7;
  color: #2c3e50;
  text-align: left;
}
</style>
