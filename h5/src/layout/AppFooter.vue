<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 15:14:50
 * @LastEditors: Che
 * @LastEditTime: 2021-08-11 11:01:35
-->
<template>
  <footer class="app-footer">
    Copyright© 2018 All Rights Reserved 版权所有 在线考试
  </footer>
</template>

<script>
export default {
  name: 'AppFooter',
  data() {
    return {}
  },
  components: {},
  mounted() {},
  methods: {},
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
