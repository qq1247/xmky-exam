<template>
  <div class="app-container">
    <app-header v-if="$route.path !== '/login'"></app-header>
    <sub-header v-if="layout"></sub-header>
    <app-main></app-main>
  </div>
</template>

<script>
import AppHeader from './AppHeader/Index.vue'
import SubHeader from './SubHeader/Index.vue'
import AppMain from './AppMain/Index.vue'
export default {
  name: 'Layout',
  components: {
    AppHeader,
    SubHeader,
    AppMain,
  },
  data() {
    return {
      layout: true,
    }
  },
  watch: {
    $route: {
      immediate: true,
      deep: true,
      handler(e) {
        this.layout = e.meta?.layout
      },
    },
  },
}
</script>

<style lang="scss">
@import 'assets/style/element-variables.scss';
.app-container {
  position: relative;
  width: 100%;
  height: 100vh;
  background: $--background-color-base;
  color: $--color-text-regular;
  display: flex;
  flex-direction: column;
}
</style>
