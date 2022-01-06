<!--
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-09 17:58:47
 * @LastEditors: Che
 * @LastEditTime: 2021-08-12 11:28:57
-->
<template>
  <el-menu
    mode="horizontal"
    class="header-menu"
    :default-active="activeMenu"
    :background-color="variables.menuBg"
    :text-color="variables.menuText"
    :unique-opened="false"
    :active-text-color="variables.menuActiveText"
    :collapse-transition="false"
  >
    <navbar-item
      v-for="route in permission_routes"
      :key="route.path"
      :item="route"
      :base-path="route.path"
    />
  </el-menu>
</template>

<script>
import { mapGetters } from 'vuex'
import NavbarItem from './NavbarItem'
import variables from 'assets/style/variables.scss'
export default {
  name: 'NavBar',
  data() {
    return {}
  },
  components: { NavbarItem },
  computed: {
    ...mapGetters(['permission_routes']),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    variables() {
      return variables
    },
  },
}
</script>

<style lang="scss">
.header-menu {
  flex: 1;
  display: flex;
  padding: 0 20px;
}
</style>
