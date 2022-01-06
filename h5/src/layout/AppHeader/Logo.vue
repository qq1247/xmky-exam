<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 17:59:47
 * @LastEditors: Che
 * @LastEditTime: 2021-11-09 13:34:52
-->
<template>
  <router-link to="/" class="header-logo">
    <img :src="orgLogo" alt="" class="logo-img" />
  </router-link>
</template>

<script>
export default {
  data() {
    return {
      orgLogo:
        `${process.env.VUE_APP_BASE_URL}login/logo` ||
        require('../../assets/img/logo.png'),
    }
  },
}
</script>

<style lang="scss">
.header-logo {
  display: flex;
  justify-content: center;
  align-items: center;
  .logo-img {
    width: 40px;
    height: 40px;
  }
}
</style>
