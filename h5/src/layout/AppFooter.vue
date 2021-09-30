<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 15:14:50
 * @LastEditors: Che
 * @LastEditTime: 2021-09-26 11:17:39
-->
<template>
  <footer class="app-footer">
    Copyright© 2021 All Rights Reserved
    <span class="footer-info"
      >版权所有&nbsp;{{ orgName }}&nbsp;<img
        src="../assets/img/logo.png"
        alt="logo"
        class="footer-logo"
    /></span>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter',
  data() {
    return {}
  },
  computed: {
    orgName: {
      get() {
        return this.$store.getters.orgName || '在线考试'
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'orgName',
          value: val,
        })
      },
    },
  },
}
</script>

<style lang="scss">
@import 'assets/style/variables.scss';
// footer
.app-footer {
  height: 50px;
  color: $white;
  font-size: 13px;
  background: $menuBg;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 0 calc((100% - 1200px) / 2 + 20px);
  .footer-info {
    display: flex;
    align-items: center;
  }
  .footer-logo {
    width: 20px;
    height: 20px;
    margin-left: 5px;
  }
}
</style>
