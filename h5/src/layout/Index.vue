<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 14:25:53
 * @LastEditors: Che
 * @LastEditTime: 2021-08-17 18:39:14
-->
<template>
  <div class="app-container">
    <app-header v-if="!hideHeader"></app-header>
    <app-main></app-main>
    <app-footer v-if="!hideFooter"></app-footer>
  </div>
</template>

<script>
import AppHeader from './AppHeader/Index.vue'
import AppMain from './AppMain.vue'
import AppFooter from './AppFooter.vue'
export default {
  name: 'LayOut',
  components: {
    AppHeader,
    AppMain,
    AppFooter,
  },
  data() {
    return {}
  },
  computed: {
    hideHeader() {
      return this.$store.state.setting.hideHeader
    },
    hideFooter() {
      return this.$store.state.setting.hideFooter
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
  min-height: 100%;
  background: $--background-color-base;
  color: $--color-text-regular;
}
</style>
