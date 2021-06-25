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
        <router-link class="header-login" to="/login">登录</router-link>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer" v-if="show">Copyright© 2018 All Rights Reserved 版权所有 在线考试</footer>
  </div>
</template>

<script>
export default {
  data() {
    return {
      show: true,
      menuList: ["PaperAdd", "PaperEdit", "LibraryAdd", "LibraryEdit"],
      navList: [
        {
          to: "/",
          img: require("../src/assets/logo.png"),
        },
        {
          content: "首页",
          to: "/",
        },
        {
          content: "试题管理",
          to: "/examLibrary",
        },
        {
          content: "试卷管理",
          to: "/examPaper",
        },
        {
          content: "考试管理",
          to: "/examSetting",
        },
      ],
    };
  },
  watch: {
    $route: {
      handler(to) {
        if (to.name && this.menuList.includes(to.name)) {
          this.show = false;
          return
        }
        this.show = true;
      },
      deep: true,
      immediate: true,
    },
  },
};
</script>

<style lang="scss">
@import url(//at.alicdn.com/t/font_840312_th8meklu86o.css);
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
