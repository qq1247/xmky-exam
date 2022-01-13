<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 15:14:50
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:51:30
-->
<template>
  <footer class="app-footer">
    <span>Copyright© {{ year }} All Rights Reserved</span>
    <span class="footer-info">版权所有&nbsp;{{ orgName }}</span>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter',
  data() {
    return {
      year: new Date().getFullYear(),
    }
  },
  computed: {
    orgName: {
      get() {
        return this.$store.getters.orgName || '在线考试'
      },
      set(val) {
        this.$store.dispatch('setting/changeSetting', {
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
  color: $menuBg;
  font-size: 13px;
  background: $white;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 0 calc((100% - 1200px) / 2 + 20px);
  border-top: 1px solid #f2f2f2;
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
