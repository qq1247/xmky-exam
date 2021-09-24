<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-22 18:05:35
 * @LastEditors: Che
 * @LastEditTime: 2021-09-14 10:35:27
-->
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {}
  },
  methods: {},
}
</script>

<style lang="scss">
@import url(//at.alicdn.com/t/font_840312_p4zri9luszn.css);
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;
}
</style>
