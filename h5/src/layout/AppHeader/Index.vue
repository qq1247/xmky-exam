<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 18:01:26
 * @LastEditors: Che
 * @LastEditTime: 2021-08-12 09:18:30
-->
<template>
  <header class="app-header">
    <div class="header-container">
      <logo></logo>
      <nav-bar></nav-bar>
      <user-info></user-info>
    </div>
  </header>
</template>

<script>
import Logo from './Logo'
import NavBar from './Navbar'
import UserInfo from './UserInfo'
export default {
  name: 'AppHeader',
  components: {
    Logo,
    NavBar,
    UserInfo,
  },
  data() {
    return {}
  },
  mounted() {},
  methods: {},
}
</script>

<style lang="scss">
@import '@/assets/style/variables.scss';
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 50px;
  z-index: 1000;
  background: $menuBg;
  .header-container {
    width: 1200px;
    height: 100%;
    margin: 0 auto;
    display: flex;
    align-items: center;
  }
}
</style>
