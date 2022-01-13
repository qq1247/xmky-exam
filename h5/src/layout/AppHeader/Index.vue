<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 18:01:26
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:53:37
-->
<template>
  <header class="app-header">
    <Back></Back>
    <Content></Content>
    <Info></Info>
  </header>
</template>

<script>
import Back from './Back.vue'
import Content from './Content.vue'
import Info from './Info.vue'
export default {
  name: 'AppHeader',
  components: {
    Back,
    Content,
    Info,
  },
  data() {
    return {}
  },
  mounted() {},
  methods: {},
}
</script>

<style lang="scss">
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 50px;
  z-index: 2000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
</style>
