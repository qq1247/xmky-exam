<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 14:25:53
 * @LastEditors: Che
 * @LastEditTime: 2022-01-21 15:48:49
-->
<template>
  <div class="app-container">
    <app-header v-if="layout"></app-header>
    <app-main :height="layout"></app-main>
    <!-- <app-footer></app-footer> -->
  </div>
</template>

<script>
import AppHeader from './AppHeader/Index.vue'
import AppMain from './AppMain/Index.vue'
import AppFooter from './AppFooter.vue'
export default {
  name: 'LayOut',
  components: {
    AppHeader,
    AppMain,
    AppFooter,
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
  mounted() {},
  methods: {},
}
</script>

<style lang="scss">
@import 'assets/style/element-variables.scss';
.app-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  height: 100%;
  background: $--background-color-base;
  color: $--color-text-regular;
  display: flex;
  flex-direction: column;
}
</style>
