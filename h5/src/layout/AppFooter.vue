<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 15:14:50
 * @LastEditors: Che
 * @LastEditTime: 2021-09-14 16:55:34
-->
<template>
  <footer class="app-footer">
    Copyright© 2018 All Rights Reserved 版权所有 <span>{{ orgName }}</span>
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
  line-height: 50px;
  text-align: center;
  color: $white;
  font-size: 12px;
  background: $menuBg;
}
</style>
