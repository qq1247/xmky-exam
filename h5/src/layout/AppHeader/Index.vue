<template>
  <div class="header">
    <div class="header-container">
      <Nav />
      <Info />
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
    Info
  },
  data() {
    return {}
  }
}
</script>

<style lang="scss" scoped>
.header {
  width: 100%;
  height: 45px;
  background: #fff;
}
.header-container {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
</style>
