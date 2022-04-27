<template>
  <header class="sub-header">
    <div class="sub-content">
      <Back></Back>
      <Content></Content>
      <div style="width: 20%"></div>
      <!-- <Info></Info> -->
    </div>
  </header>
</template>

<script>
import Back from './Back.vue'
import Content from './Content.vue'
// import Info from './Info.vue'
export default {
  name: 'AppHeader',
  components: {
    Back,
    Content,
    // Info,
  },
  data() {
    return {}
  },
  mounted() {},
  methods: {},
}
</script>

<style lang="scss">
.sub-header {
  height: 40px;
  background: #fff;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  .sub-content {
    width: 1200px;
    height: 100%;
    margin: 0 auto;
    display: flex;
    align-items: center;
  }
}
</style>
