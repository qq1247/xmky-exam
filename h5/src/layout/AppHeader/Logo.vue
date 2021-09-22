<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 17:59:47
 * @LastEditors: Che
 * @LastEditTime: 2021-09-14 16:51:26
-->
<template>
  <router-link to="/" class="header-logo">
    <img :src="orgLogo" alt="" class="logo-img" />
  </router-link>
</template>

<script>
export default {
  data() {
    return {}
  },
  computed: {
    orgLogo: {
      get() {
        return this.$store.getters.orgLogo
          ? `/api/file/download?id=${this.$store.getters.orgLogo}`
          : require('../../assets/img/logo.png')
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'orgLogo',
          value: val,
        })
      },
    },
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
