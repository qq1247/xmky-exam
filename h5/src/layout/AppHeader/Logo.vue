<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 17:59:47
 * @LastEditors: Che
 * @LastEditTime: 2021-08-12 11:29:29
-->
<template>
  <router-link to="/" class="header-logo">
    <img src="../../assets/img/logo.png" alt="" class="logo-img" />
  </router-link>
</template>

<script>
export default {
  data() {
    return {}
  },
  mounted() {},
  methods: {},
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
