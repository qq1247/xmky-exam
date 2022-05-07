<template>
  <main class="app-main">
    <transition name="fade-transform" mode="out-in">
      <router-view :key="key" />
    </transition>
  </main>
</template>

<script>
export default {
  name: 'AppMain',
  data() {
    return {
      navs: [],
      layout: '',
      active: ''
    }
  },
  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  flex: 1;
  width: 100%;
  position: relative;
  overflow: scroll;
  display: flex;
}
</style>
