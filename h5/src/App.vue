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
      Copyright© 2018 All Rights Reserved 版权所有 乐调查 沪ICP备16024976号-7
    </footer>
  </div>
</template>

<script>
export default {
  data() {
    return {
      show: true,
      menuList: ["Question"]
    }
  },
  watch: {
    $route: {
      handler(to, from) {
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
@import url(https://at.alicdn.com/t/font_840312_p434w5vp2b.css);
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  height: 100%;
  background: #fff;
  box-sizing: border-box;
}
</style>
