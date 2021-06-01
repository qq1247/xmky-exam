<template>
  <div id="app">
    <header class="header" v-if="show">
      <div class="header-content">
        <div class="header-left">
          <router-link class="header-link" to="/">
            <img src="../src/assets/logo.png" alt="" class="header-logo" />
          </router-link>
          <router-link class="header-link" to="/">a</router-link>
          <router-link class="header-link" to="/">a</router-link>
          <router-link class="header-link" to="/">a</router-link>
          <router-link class="header-link" to="/">a</router-link>
          <router-link class="header-link" to="/">a</router-link>
        </div>
        <router-link class="header-login" to="/login">登录</router-link>
      </div>
    </header>
    <router-view />
    <footer class="footer" v-if="show">
      Copyright© 2018 All Rights Reserved 版权所有 在线考试
    </footer>
  </div>
</template>

<script>
export default {
  data() {
    return {
      show: true,
      menuList: ["Question", "Exam"]
    }
  },
  watch: {
    $route: {
      handler(to) {
        if (to && this.menuList.includes(to.name)) {
          this.show = false
        }
      },
      deep: true,
      immediate: true
    }
  }
}
</script>

<style lang="scss">
@import url(//at.alicdn.com/t/font_840312_mn3tmtkdwg.css);
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: 100%;
  background: #fff;
  color: #2c3e50;
  text-align: left;
}
</style>
