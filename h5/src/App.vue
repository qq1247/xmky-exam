<template>
  <div id="app">
    <header class="header" v-if="show">
      <div class="header-content">
        <div class="header-left">
          <router-link
            exact
            :class="[nav.img ? 'header-logo' : 'header-link']"
            v-for="nav in navList"
            :key="nav.title"
            :to="nav.to"
          >
            {{ nav.content }}
            <el-image v-if="nav.img" :src="nav.img" class="logo-img"></el-image>
          </router-link>
        </div>
        <div class="header-userInfo">
          <template v-if="userInfo">
            欢迎您！
            <span class="user-name">{{ JSON.parse(userInfo).userName }}</span>
            <el-tooltip effect="dark" content="退出" placement="right">
              <i class="common common-loginOut" @click="loginOut"></i>
            </el-tooltip>
          </template>
          <router-link v-else class="header-login"
to="/login"
            >登录</router-link
          >
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer v-if="show" class="footer">
      Copyright© 2018 All Rights Reserved 版权所有 在线考试
    </footer>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
export default {
  data() {
    return {
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
        },
        {
          content: '试题管理',
          to: '/examQuestion',
        },
        {
          content: '试卷管理',
          to: '/examPaper',
        },
        {
          content: '考试管理',
          to: '/examSetting',
        },
        {
          content: '我的',
          to: '/my',
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
  },
}
</script>

<style lang="scss">
@import url(//at.alicdn.com/t/font_840312_polq493yfqg.css);
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
