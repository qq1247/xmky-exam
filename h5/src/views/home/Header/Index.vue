<!--
 * @Description: 用户信息
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-13 09:29:15
 * @LastEditors: Che
 * @LastEditTime: 2022-01-07 10:28:58
-->
<template>
  <div class="header">
    <div class="container">
      <Nav></Nav>
      <Info></Info>
    </div>
  </div>
</template>

<script>
import Nav from './Nav.vue'
import Info from './Info.vue'
export default {
  name: 'Header',
  components: {
    Nav,
    Info,
  },
  data() {
    return {}
  },
}
</script>

<style lang="scss" scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 50px;
  background: #fff;
  z-index: 1500;
}
.container {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
</style>
